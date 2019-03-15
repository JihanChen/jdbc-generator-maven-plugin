package com.jihan.plugins.dao;


import com.jihan.plugins.entity.ColumnBean;
import com.jihan.plugins.entity.TableBean;
import com.jihan.plugins.util.DBUtils;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据源连接数据库
 */
public final class CodeGenDao {

    private static final CodeGenDao dao = new CodeGenDao();

    private CodeGenDao(){}

    public static CodeGenDao getInstance(){
        return dao;
    }


    /**
     * 初始化QueryRunner对象
     */
    private static final QueryRunner qx  = new QueryRunner(DBUtils.getDataSource());


    /**
     * 查询指定数据库
     * 指定表的字段信息
     * @param schemaName
     * @param tableName
     * @return
     * @throws SQLException
     */
    public List<ColumnBean> selectColumns(String schemaName , String tableName) throws SQLException {
        String sql = "SELECT table_name ,column_Key  ,column_type," +
                "column_name ,is_nullable ," +
                "data_type , character_maximum_length , " +
                "column_comment  FROM information_schema.COLUMNS" +
                " WHERE table_schema =?  AND table_name = ? ORDER BY column_Key = 'PRI' DESC,ORDINAL_POSITION" ;
        Object[] params = new Object[]{schemaName,tableName};
        return qx.query(sql, new ColumnListHandler(), params);
    }

    /**
     * 获取数据库所有表信息
     * @param schemaName
     * @return
     * @throws SQLException
     */
    public List<TableBean> selectTables(String schemaName) throws SQLException {
        String sql = "SELECT table_name, table_type , ENGINE,table_collation,table_comment, create_options FROM information_schema.TABLES WHERE table_schema=?";
        Object[] params = new Object[]{schemaName};
        return qx.query(sql, new TableListHandler(), params);
    }
}
