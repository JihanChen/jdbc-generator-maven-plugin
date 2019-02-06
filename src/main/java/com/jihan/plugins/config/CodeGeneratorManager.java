package com.jihan.plugins.config;

import com.jihan.plugins.service.impl.ControllerGenerator;
import com.jihan.plugins.service.impl.DaoGenerator;
import com.jihan.plugins.service.impl.EntityGenerator;
import com.jihan.plugins.service.impl.ServiceGenerator;
import com.jihan.plugins.util.PropertiesUtil;
import com.jihan.plugins.util.ThreadLocalUtil;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 代码生成器基础项
 */
public class CodeGeneratorManager extends CodeGeneratorConfig {
	
	protected static final Logger logger = LoggerFactory.getLogger(CodeGeneratorManager.class);
	
	private static Configuration configuration = null;
	
	static {
		// 初始化配置信息
		init();
	}
	
	/**
	 * 获取 Freemarker 模板环境配置
	 * @return
	 */
	public Configuration getFreemarkerConfiguration() {
		if (configuration == null) {
			configuration = initFreemarkerConfiguration();
		}
		return configuration;
	}

	/**
	 * 通过数据库表名, 生成代码
	 * 如表名为 demo
	 * 将生成  DemoDO & DemoDao & DemoService & DemoServiceImpl & DemoController
	 * @param tableNames 表名数组
	 */
	public void genCodeByTableName(String schema, String ...tableNames) {
		for (String tableName : tableNames) {
			genCodeByTableName(schema,tableName);
		}
	}
	
	/**
	 * 通过数据库表名, 和自定义 modelName 生成代码
	 * 如表名为 gen_test_demo, 自定义 modelName 为 IDemo
	 * 将生成  IDemo & IDemoMapper & IDemoService & IDemoServiceImpl & IDemoController
	 * @param tableName 表名
	 */
	public void genCodeByTableName(String schema,String tableName) {
		genCodeWithCustomName(schema,tableName,null);
	}

	/**
	 * 通过自定义 customName 生成代码
	 * 如表名为 gen_test_demo, 自定义 modelName 为 IDemo,將生成自定义的IDemo
	 * 将生成  IDemoDO & IDemoMapper & IDemoService & IDemoServiceImpl & IDemoController
	 * @param tableName 表名
	 */
	public void genCodeWithCustomName(String schema ,String tableName,String customName) {
		ThreadLocalUtil.setSchema(schema);
		new EntityGenerator().genCode(tableName, customName);
		new DaoGenerator().genCode(tableName, customName);
		new ServiceGenerator().genCode(tableName, customName);
		new ControllerGenerator().genCode(tableName, customName);
	}


	/**
	 * Freemarker 模板环境配置
	 * @return
	 * @throws IOException
	 */
	private Configuration initFreemarkerConfiguration() {
		Configuration cfg = null;
		try {
			cfg = new Configuration(Configuration.VERSION_2_3_23);
			cfg.setClassForTemplateLoading(CodeGeneratorManager.class,"/template");
			cfg.setDefaultEncoding("UTF-8");
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		} catch (Exception e) {
			throw new RuntimeException("Freemarker 模板环境初始化异常!", e);
		}
		return cfg;
	}


	/**
	 * 包转成路径
	 * eg: com.bigsea.sns ==> com/bigsea/sns
	 * @param packageName
	 * @return
	 */
	private static String packageConvertPath(String packageName) {
		return String.format("/%s/", packageName.contains(".") ? packageName.replaceAll("\\.", "/") : packageName);
	}
	
	/**
	 * 初始化配置信息
	 */
	private static void init() {
		Properties prop = loadProperties();

		JAVA_PATH = prop.getProperty("java.path");

		BASE_PACKAGE = prop.getProperty("base.package");
		ENTITY_PACKAGE = prop.getProperty("dataobject.package");
		DAO_PACKAGE = prop.getProperty("dao.package");
		DAO_IMPL_PACKAGE = prop.getProperty("dao.impl.package");
		SERVICE_PACKAGE = prop.getProperty("service.package");
		SERVICE_IMPL_PACKAGE = prop.getProperty("service.impl.package");
		CONTROLLER_PACKAGE = prop.getProperty("controller.package");
		// 包签名
		SIGN_PACKAGE_NAME = prop.getProperty("sign.package.name");

		// 包的文件路径名称
		PACKAGE_PATH_ENTITY= packageConvertPath(ENTITY_PACKAGE);
		PACKAGE_PATH_DAO = packageConvertPath(DAO_PACKAGE);
		PACKAGE_PATH_DAO_IMPL =packageConvertPath(DAO_IMPL_PACKAGE);
		PACKAGE_PATH_SERVICE = packageConvertPath(SERVICE_PACKAGE);
		PACKAGE_PATH_SERVICE_IMPL = packageConvertPath(SERVICE_IMPL_PACKAGE);
		PACKAGE_PATH_CONTROLLER = packageConvertPath(CONTROLLER_PACKAGE);


		NOT_GEN_CONTROLLER = "".equals(prop.getProperty("not.gen.controller")) ? false : Boolean.valueOf(prop.getProperty("not.gen.controller"));
		NOT_GEN_SERVICE =  "".equals(prop.getProperty("not.gen.service")) ? false : Boolean.valueOf(prop.getProperty("not.gen.service"));
		NOT_GEN_BATCH = "".equals(prop.getProperty("not.gen.batch")) ? false : Boolean.valueOf(prop.getProperty("not.gen.batch"));

		AUTHOR = prop.getProperty("author");
		String dateFormat = "".equals(prop.getProperty("date-format")) ? "yyyy/MM/dd" : prop.getProperty("date-format");
		DATE = new SimpleDateFormat(dateFormat).format(new Date());

		GEN_JDBC_TEMPLATE_FACTORY = "".equals(prop.getProperty("gen.jdbc.template.factory")) ? false :  Boolean.valueOf(prop.getProperty("gen.jdbc.template.factory"));
		GEN_JDBC_TEMPLATE_FACTORY_PACKAGE = prop.getProperty("gen.jdbc.template.factory.package");
		if (GEN_JDBC_TEMPLATE_FACTORY){
			if ("".equals(GEN_JDBC_TEMPLATE_FACTORY_PACKAGE)){
				throw new RuntimeException("请配置gen.jdbc.template.factory.package路径地址！或将gen.jdbc.template.factory设置为false！");
			}
		}
		GEN_QUERY_BY_ACCID = "".equals(prop.getProperty("gen.query.by.accid")) ? false :  Boolean.valueOf(prop.getProperty("gen.query.by.accid"));

	}
	
	/**
	 * 加载配置文件
	 * @return
	 */
	private static Properties loadProperties() {
		Properties prop = PropertiesUtil.getProperties("generator-config.properties");
		return prop;
	}


	public static void main(String[] args) {
		CodeGeneratorManager codeGeneratorManager = new CodeGeneratorManager();
		codeGeneratorManager.genCodeByTableName("account2","account_0");

	}
}
