//package com.riddler.guide.util;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.sql.Connection;
//import java.util.Properties;
//
//public class PropertiesUtil {
//
//	private static Properties prop = new Properties();
//	private final static String file = "/server.properties";
//	Connection conn=null;
//	static {
//		try {
//			InputStream in = PropertiesUtil.class.getResourceAsStream(file);
//			prop.load(in);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static String getProperty(String key) {
//		return prop.getProperty(key);
//	}
//
//	public static void main(String[] args) {
//		System.out.println("修改前值:"
//				+ PropertiesUtil.getProperty("kafka_ip"));
//	}
//
//}
