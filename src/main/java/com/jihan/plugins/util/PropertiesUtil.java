package com.jihan.plugins.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    /**
     *  根据名称加载文件信息
     * @param fileName
     * @return
     */
    public static Properties getProperties(String fileName) {
        // 先读取引用项目的resource下文件，没有再加载classpath的（jar）
        try {
            String outpath = System.getProperty("user.dir")+ File.separator+"src/main/resources"+File.separator;;
            String path = outpath + fileName;
            Properties properties = new Properties();
            InputStream in = new FileInputStream(new File(path));
            properties.load(in);
            return properties;
        } catch (IOException e) {
            try {
                Properties properties = new Properties();
                InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
                properties.load(in);
                return properties;
            } catch (IOException es) {
                System.out.println(es.getMessage());
                throw new RuntimeException("加载配置文件异常!", e);
            }
        }
    }
}
