<#if sign == "">
package ${basePackage}.dao.impl;
import ${basePackage}.pojo.dataobject.${entityName};
import ${basePackage}.dao.${tableCamelName}Dao;
<#else>
package ${basePackage}.dao.impl.${sign};
import ${basePackage}.pojo.dataobject.${sign}.${entityName};
import ${basePackage}.dao.${sign}.${tableCamelName}Dao;
</#if>

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import ${jdbcTemplateFactoryPackage}.JdbcTemplateFactory;
<#if isNeedGeneBatch>
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import java.sql.SQLException;
import java.util.Map;
import ${jdbcTemplateFactoryPackage}.CustomerJdbcTemplate;
</#if>
<#-- 定义主键key字段 -->
<#assign columnKey = "id">
<#-- 定义主键映射到java实体的字段 -->
<#assign columnKeyCamelName = "id">
<#assign columnKeyType = "bigint">
<#-- accid -->
<#assign columnAccIdName = "acc_id">
<#-- 获取主键key -->
<#list columnList as x>
    <#if x.columnKey != "" && x.columnKey == "PRI">
        <#assign columnKey= x.columnName >
        <#assign columnKeyCamelName= x.columnCamelName >
        <#assign columnKeyType = x.dataType>
    </#if>
    <#if x.columnName ? lower_case == "accid" || x.columnName ? lower_case == "acc_id" >
        <#assign columnAccIdName = x.columnName>
    </#if>
</#list>
/**
 * @author ${author}
 * @date  ${date}
*/
@Repository
public class ${tableCamelName}DaoImpl implements ${tableCamelName}Dao{

    @Autowired
    private JdbcTemplateFactory jdbcTemplateFactory;

    /**
     * 查询所有
     * @param entity
     * @return
     */
    @Override
    public List<${entityName}> list(String accId,${entityName} entity){
        final String sql = "SELECT * FROM  ${tableName}";
        return jdbcTemplateFactory.getJdbctemplate(accId).query(sql,new BeanPropertyRowMapper<>(${entityName}.class));
    }

    /**
    * 添加
    * @param entity
    * @return
    */
    @Override
    public ${entityName} insert(String accId,${entityName} entity){
        List<Object> params = new ArrayList<>();
    <#if columnKeyType == "varchar">
        jdbcTemplateFactory.getJdbctemplate(accId).update(createSql(entity,false,params),params.toArray());
    <#else>
    <#-- 需要生成主键，仅支持主键自增 -->
        if (null == entity.get${columnKeyCamelName ? cap_first}()){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplateFactory.getJdbctemplate(accId).update(conn -> {
        PreparedStatement ps = conn.prepareStatement(createSql(entity,true,params), Statement.RETURN_GENERATED_KEYS);
        <#list columnList as x>
            <#if  x.columnKey != "PRI" >
                ps.setObject(${x_index},entity.get${x.columnCamelName ? cap_first}());
            </#if>
        </#list>
        return ps;
        }, keyHolder);
        entity.set${columnKeyCamelName ? cap_first}(keyHolder.getKey().longValue());
        }else{
    <#-- 自带主键，不需要数据库来生成-->
        jdbcTemplateFactory.getJdbctemplate(accId).update(createSql(entity,false,params),params.toArray());
        }
    </#if>
        return entity;
    }


    /**
    * 根据主键id删除
    * @param entity
    * @return
    */
    @Override
    public int delete(String accId,${entityName} entity){
      if (null == entity.get${columnKeyCamelName ? cap_first}()){
         throw new IllegalArgumentException("PRIMARY KEY ${columnKeyCamelName} can not null !");
       }
       String sql = "DELETE FROM ${tableName} where ${columnKey} = ?";
       return jdbcTemplateFactory.getJdbctemplate(accId).update(sql,entity.get${columnKeyCamelName ? cap_first}());
    }

    /**
    * 根据主键id修改
    * @param entity
    * @return
    */
    @Override
    public int update(String accId,${entityName} entity){

        if (null == entity.get${columnKeyCamelName ? cap_first}()){
        throw new IllegalArgumentException("PRIMARY KEY ${columnKeyCamelName} can not null !");
        }
        List<Object> paramStatement = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UPDATE ${tableName} SET ");
        <#list columnList as x>
            <#if  x.columnKey != "PRI" >
                if (null != entity.get${x.columnCamelName ? cap_first}()){
                stringBuilder.append("${x.columnName} = ?,");
                paramStatement.add(entity.get${x.columnCamelName ? cap_first}());
                }
            </#if>
        </#list>
            if (paramStatement.size() == 0){
               return 0;
            }
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
            stringBuilder.append(" WHERE ${columnKey} = ? ");
            paramStatement.add(entity.get${columnKeyCamelName ? cap_first}());
            return jdbcTemplateFactory.getJdbctemplate(accId).update(stringBuilder.toString(),paramStatement.toArray());
        }

  <#if isNeedGenQueryByAccId>

      /**
      * 根据accid查询列表
      * @param accId
      * @return
      */
      @Override
      public List<${entityName}> getByAccId(String accId){
          String sql = "SELECT * FROM ${tableName} where ${columnAccIdName} = ?";
          return jdbcTemplateFactory.getJdbctemplate(accId).query(sql,new Object[]{accId},new BeanPropertyRowMapper<>(${entityName}.class));

      }

  </#if>
  <#if isNeedGeneBatch>
        /**
        * 批量保存
        * @param entityList
        * @return 批量保存结果
        */
        @Override
        public List<${entityName}> insertBatch(String accId,List<${entityName}> entityList){
              // 参数校验
              if (null == entityList || entityList.size() == 0){
                return entityList;
              }
              final List<${entityName}> statementList = entityList;
              boolean isNeedGeneratePriKey = null == entityList.get(0).get${columnKeyCamelName ? cap_first}();
              // 获取sql语句
              String sql = createSql(entityList.get(0),isNeedGeneratePriKey,new ArrayList<>());
        <#if columnKeyType == "varchar">
              jdbcTemplateFactory.getJdbctemplate(accId).batchUpdate(sql, new BatchPreparedStatementSetter() {
              @Override
              public void setValues(PreparedStatement ps, int i) throws SQLException {
              <#list columnList as x>
                  ps.setObject(${x_index+1},statementList.get(i).get${x.columnCamelName ? cap_first}());
              </#list>
              }
              @Override
              public int getBatchSize() {
                 return statementList.size();
              }
              });
        <#else>
            if (isNeedGeneratePriKey){
              // 自动生成主键，前提mysql设置主键自增
              GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
             ((CustomerJdbcTemplate)jdbcTemplateFactory.getJdbctemplate(accId)).batchUpdate(sql, new BatchPreparedStatementSetter() {
              @Override
              public void setValues(PreparedStatement ps, int i) throws SQLException {
              <#list columnList as x>
                  <#if  x.columnKey != "PRI" >
                      ps.setObject(${x_index},statementList.get(i).get${x.columnCamelName ? cap_first}());
                  </#if>
              </#list>
              }
              @Override
              public int getBatchSize() {
              return statementList.size();
              }
              }, generatedKeyHolder);
              // 设置主键的值
              List<Map<String, Object>> objectMap = generatedKeyHolder.getKeyList();
                             for (int i = 0; i< objectMap.size();i++){
                             Map<String, Object> map =  objectMap.get(i);
              entityList.get(i).setId((Long) map.get("GENERATED_KEY"));
              }
              }else{
              // 不需要生成主键/ 外部传入
              jdbcTemplateFactory.getJdbctemplate(accId).batchUpdate(sql, new BatchPreparedStatementSetter() {
              @Override
              public void setValues(PreparedStatement ps, int i) throws SQLException {
              <#list columnList as x>
                  ps.setObject(${x_index+1},statementList.get(i).get${x.columnCamelName ? cap_first}());
              </#list>
              }
              @Override
              public int getBatchSize() {
                return statementList.size();
              }
              });
            }
        </#if>
            return entityList;
        }
  </#if>
        <#--创建insert的sql语句 -->
        private String createSql(${entityName} entity,boolean needGeneratePrimaryKey,List<Object> paramStatements){

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("INSERT INTO ");
            stringBuilder.append("${tableName}");
            stringBuilder.append("( ");

            if (needGeneratePrimaryKey){
            // 需要生成主键，mysql自动增长
            <#list columnList as x>
                <#if  x.columnKey != "PRI" >
                    stringBuilder.append("${x.columnName},");
                    paramStatements.add(entity.get${x.columnCamelName ? cap_first}());
                </#if>
            </#list>

                }else{
            <#list columnList as x>
                stringBuilder.append("${x.columnName},");
                paramStatements.add(entity.get${x.columnCamelName ? cap_first}());
            </#list>
             }
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length()).append(") ");
            stringBuilder.append("VALUES (");
            paramStatements.forEach(e->stringBuilder.append("?,"));
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length()).append(")");
            return stringBuilder.toString();
          }

 }
