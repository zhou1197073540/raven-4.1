package com.tenthousand.fileupload.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileRegexTest {


    public static void main(String[] args) throws Exception {
//        FileRegexTest test = new FileRegexTest();
//        String path = "D:\\123.html";
//        String content = test.replacePMark(path);
//        test.write(path, content);

        String str = "<div style=\"width:595.0pt;margin-bottom:72.0pt;margin-top:72.0pt;margin-left:90.0pt;margin-right:90.0pt;\">";
        Pattern pattern = Pattern.compile("^<div.*margin-left:(\\d+\\.?.*?;).*");
        Matcher matcher = pattern.matcher(str);
        matcher.find();
        String change = matcher.group(1);
        System.out.println(str.replaceFirst(change, "15px;"));
    }

    private String replacePMark(String path) throws Exception {
        BufferedReader bufferedReader = null;
        String content = null;
        try {
            bufferedReader = Files.newBufferedReader(Paths.get(path));
            content = bufferedReader.readLine().replaceAll("(<p[^>]*></p>)+", "")
                    .replaceAll("(<p[^>]*/>)+", "")
                    .replaceAll("(<p[^>]*><br/></p>)+", "<p><br/></p>");
        } finally {
            bufferedReader.close();
        }
        return "<style type=\"text/css\">p {margin:0,3px; padding:0}</style>" + content;
    }

    private void write(String path, String content) throws Exception {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
        } finally {
            writer.close();
        }
    }
}
