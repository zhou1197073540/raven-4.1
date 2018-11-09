package com.tenthousand.fileupload.controller;

import com.tenthousand.fileupload.bean.IIRIReportIndexBean;
import com.tenthousand.fileupload.service.FileHandleService;
import com.tenthousand.fileupload.utils.SystemUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class FileUploadController {

    @Autowired
    FileHandleService fileHandleService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String getUploadFile(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String fid = request.getParameter("path");
        String path = null;
        if (StringUtils.isEmpty(fid)) {
            response.sendError(HttpStatus.FORBIDDEN.value(), "路径非法");
            return null;
        } else {
            path = new String(SystemUtil.getRootPath() + fid + "/").replace("\\", "/");
        }
        fileHandleService.handle(file, path, fid);
        return "hehe!";
    }

    @RequestMapping(value = "/create_folder", method = RequestMethod.POST)
    @ResponseBody
    public String createFolder(@RequestBody IIRIReportIndexBean bean) throws Exception {
        String pathStr = SystemUtil.getRootPath() + bean.getFid() + "/";
        String backupPathStr = SystemUtil.getBackupPath() + bean.getFid() + "/";
        if (!Files.exists(Paths.get(pathStr))) {
            Files.createDirectory(Paths.get(pathStr));
            bean.setCreate_date(LocalDate.now().toString());
            bean.setCreate_time(LocalDateTime.now().toString());
            fileHandleService.createIndex(bean);
        }
        if (!Files.exists(Paths.get(backupPathStr))) {
            Files.createDirectory(Paths.get(backupPathStr));
        }
        return "yeah";
    }

    @RequestMapping(value = "/get_index/{fid}", method = RequestMethod.GET)
    @ResponseBody
    public String getIndex(@PathVariable String fid) throws Exception {
        IIRIReportIndexBean bean = new IIRIReportIndexBean();
        bean.setFid(fid);
        return fileHandleService.getIndex(bean);
    }

    @RequestMapping(value = "/change_input", method = RequestMethod.POST)
    @ResponseBody
    public String changeInput(@RequestBody IIRIReportIndexBean bean) throws Exception {
        fileHandleService.changeInput(bean);
        return "yeah";
    }
}
