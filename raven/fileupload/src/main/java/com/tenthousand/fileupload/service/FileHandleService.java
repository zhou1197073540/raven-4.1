package com.tenthousand.fileupload.service;

import com.tenthousand.fileupload.bean.IIRIReportIndexBean;
import com.tenthousand.fileupload.consts.Consts;
import com.tenthousand.fileupload.mapper.FileMapper;
import com.tenthousand.fileupload.utils.HashUtil;
import com.tenthousand.fileupload.utils.SystemUtil;
import com.tenthousand.fileupload.utils.W2H;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FileHandleService {

    @Autowired
    FileMapper fileMapper;

    /**
     * 处理文件
     *
     * @param file
     * @param pathStr
     * @param fid
     * @throws Exception
     */
    public void handle(MultipartFile file, String pathStr, String fid) throws Exception {
        String backupPath = SystemUtil.getBackupPath() + fid + "/";
        deleteOldFile(file, pathStr);
        deleteOldFile(file, backupPath);
        IIRIReportIndexBean bean = new IIRIReportIndexBean();
        String fileName = "";
        if (isDocx(file)) {
            fileName = HashUtil.encodeByMD5(file.getOriginalFilename()
                    + LocalDateTime.now().toString()) + ".html";
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
                //生成html文件
                W2H.convert2HtmlDocxOnly(inputStream,
                        pathStr, fileName);
            } finally {
                inputStream.close();
            }

            String htmlPath = Consts.mapping + "/" + fid + "/" + fileName;
            bean.setFid(fid);
            bean.setHtml_path(htmlPath);
            bean.setSummary(getSummary(pathStr + fileName));
            formatClearUp(pathStr + fileName);
            fileMapper.updateHtmlIndexAndSummary(bean);
        } else {
            int pos = file.getOriginalFilename().lastIndexOf(".");
            fileName = HashUtil.encodeByMD5(file.getOriginalFilename()
                    + LocalDateTime.now().toString())
                    + file.getOriginalFilename().substring(pos, file.getOriginalFilename().length());
            file.transferTo(new File(pathStr + fileName));
            String imgPath = Consts.mapping + "/" + fid + "/" + fileName;
            bean.setFid(fid);
            bean.setImg_path(imgPath);
            fileMapper.updateImageIndex(bean);
        }

        //备份 万一有屎呢
        backup(file, backupPath, fileName);
    }

    /**
     * 删除旧文件
     *
     * @param file
     * @param pathStr
     * @throws Exception
     */
    private void deleteOldFile(MultipartFile file, String pathStr) throws Exception {
        if (Files.exists(Paths.get(pathStr))) {
            if (isDocx(file)) {
                Files.list(Paths.get(pathStr)).forEach(path -> {
                    try {
                        if (path.toString().endsWith("docx") ||
                                path.toString().endsWith("html") ||
                                path.toString().endsWith("word")) {
                            deleteFile(path.toFile());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //ignore
                    }
                });
            } else {
                Files.list(Paths.get(pathStr)).forEach(path -> {
                    try {
                        if (path.toString().endsWith("jpg") ||
                                path.toString().endsWith("png")) {
                            deleteFile(path.toFile());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //ignore
                    }
                });
            }
        }
    }

    private boolean isDocx(MultipartFile file) {
        int pos = file.getOriginalFilename().lastIndexOf(".");
        String endWith = file.getOriginalFilename().substring(pos, file.getOriginalFilename().length());
        if (endWith.equals(".docx")) return true;
        return false;
    }

    private void deleteFile(File path) throws Exception {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteFile(files[i]);
        }
        path.delete();
    }


    /**
     * 获取summary
     *
     * @param pathStr
     * @return
     * @throws Exception
     */
    private String getSummary(String pathStr) throws Exception {
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(pathStr));
        String summary = bufferedReader.lines()
                .map(line -> line.replaceAll("<[^>]*>", "").replaceAll("&[^#][^\\d]+;", "").replace("&nbsp;", ""))
                .map(line -> Arrays.stream(line.split(";")))
                .flatMap(lines -> lines)
                .limit(210).collect(Collectors.joining(";"));
        bufferedReader.close();
        return summary;
    }


    /**
     * 整理格式
     *
     * @param path
     * @throws Exception
     */
    private void formatClearUp(String path) throws Exception {
        write(path, read(path));
    }

    /**
     * 整理
     *
     * @param path
     * @return
     * @throws Exception
     */
    private String read(String path) throws Exception {
        BufferedReader bufferedReader = null;
        String content = "";
        try {
            bufferedReader = Files.newBufferedReader(Paths.get(path));
            //去多余p标签
            content = bufferedReader.readLine().replaceAll("(<p[^>]*></p>)+", "")
                    .replaceAll("(<p[^>]*/>)+", "")
                    .replaceAll("(<p[^>]*><br/></p>)+", "<p><br/></p>");

            //margin-left改为15px
            Pattern pattern = Pattern.compile("^<div.*margin-left:(\\d+\\.?.*?;).*");
            Matcher matcher = pattern.matcher(content);
            matcher.find();
            String change = matcher.group(1);
            content = content.replaceFirst(change, "15px;");

            //更改p标签默认距离
            content = "<style type=\"text/css\">p {margin:0,3px; padding:0}</style>" + content;
        } finally {
            bufferedReader.close();
        }
        return content;
    }

    /**
     * 写回原文件
     *
     * @param path
     * @param content
     * @throws Exception
     */
    private void write(String path, String content) throws Exception {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(Paths.get(path));
            writer.write(content);
        } finally {
            writer.close();
        }
    }

    private void backup(MultipartFile file, String backupPath, String fileName) throws Exception {
        if (isDocx(file)) {
            W2H.convert2HtmlDocxOnly(file.getInputStream(),
                    backupPath, fileName);
        }
        Files.copy(file.getInputStream(), Paths.get(backupPath + file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 创建index
     *
     * @param bean
     * @throws Exception
     */
    public void createIndex(IIRIReportIndexBean bean) throws Exception {
        bean.setAuthor(bean.getAuthor().replace("，", ","));
        bean.setKey_words(bean.getKey_words().replace("，", ","));
        fileMapper.createIIRIReportIndex(bean);
    }

    /**
     * 获取index
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public String getIndex(IIRIReportIndexBean bean) throws Exception {
        return fileMapper.getIndexByFid(bean);
    }

    /**
     * 更改title，author，keyword，date
     *
     * @param bean
     * @throws Exception
     */
    public void changeInput(IIRIReportIndexBean bean) throws Exception {
        bean.setKey_words(bean.getKey_words().replace("，", ","));
        fileMapper.updateByFid(bean);
    }
}
