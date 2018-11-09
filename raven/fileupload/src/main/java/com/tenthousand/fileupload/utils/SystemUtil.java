package com.tenthousand.fileupload.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SystemUtil {

    private static String rootPath;
    private static String backPath;

    public static String getRootPath() {
        if (StringUtils.isBlank(rootPath)) {
            rootPath = createPathAndDir("/IIRI/");
        }
        return rootPath;
    }

    public static String getBackupPath() {
        if (StringUtils.isBlank(backPath)) {
            backPath = createPathAndDir("/IIRI_BK/");
        }
        return backPath;
    }

    private static String createPathAndDir(String pathPart) {
        String path = StringUtils.trim(new File("").getAbsolutePath()) + pathPart;
        path = path.replaceAll("\\\\", "/");
        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createDirectory(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }
}
