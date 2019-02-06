package com.jihan.plugins.util;

import com.jihan.plugins.entity.ColumnBean;

import java.util.ArrayList;
import java.util.List;

public class ThreadLocalUtil {

    private static final ThreadLocal<List<ColumnBean>> columnBeanThreadLocal = new ThreadLocal() {
        @Override
        protected List<ColumnBean> initialValue() {
            return new ArrayList<>();
        }
    };

    private static final ThreadLocal<String> schemaThreadLocal = new ThreadLocal() {
        @Override
        protected String initialValue() {
            return "";
        }
    };


    public static String getSchema(){
        return schemaThreadLocal.get();
    }

    public static void setSchema(String schema) {
        schemaThreadLocal.set(schema);
    }

    public static void removeSchema() {
        schemaThreadLocal.remove();
    }




    public static List<ColumnBean> getColumnBeanList(){
        return columnBeanThreadLocal.get();
    }

    public static void setColumnBean(List<ColumnBean> columnBeans) {
        columnBeanThreadLocal.set(columnBeans);
    }

    public static void removeColumn() {
        columnBeanThreadLocal.remove();
    }


}
