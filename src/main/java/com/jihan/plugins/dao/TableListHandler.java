package com.jihan.plugins.dao;


import com.jihan.plugins.entity.TableBean;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * schema查询结果处理
 */
public class TableListHandler implements ResultSetHandler<List<TableBean>> {

    @Override
    public List<TableBean> handle(ResultSet rs) throws SQLException {
        List<TableBean> list = new ArrayList<>();
        TableBean bean = null;
        while(rs.next()){
            bean = new TableBean();
            bean.setTableName(rs.getString("table_name"));
            bean.setTableType(rs.getString("table_type"));
            bean.setTableComment(rs.getString("table_comment"));
            bean.setEngine(rs.getString("ENGINE"));
            bean.setTableCollation(rs.getString("table_collation"));
            bean.setCreateOptions(rs.getString("create_options"));
            list.add(bean);
        }
        return list;
    }

}
