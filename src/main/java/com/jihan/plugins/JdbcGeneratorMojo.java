package com.jihan.plugins;

import com.jihan.plugins.config.CodeGeneratorManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
@Mojo(name = "generate", threadSafe = true)
public class JdbcGeneratorMojo extends AbstractMojo {
    /**
     * 日志工具
     */
    protected Log log = getLog();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("==========================开始代码生成==========================");
        // 初始化日志
        initLog();
        CodeGeneratorManager codeGeneratorManager = new CodeGeneratorManager();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {

            String schema = input(br, "请输入数据库名", "db_name");
            String tableNames = input(br, "请输入表名（多个用逗号分割）", "TABLE1,TABLE2...");
            String[] sourceStrArray = tableNames.split(",");
            if (sourceStrArray.length == 1){
                String isNeedCustom = input2(br, "是否需要自定义生成实体名?Y/N", "Y/N");
                if (Objects.equals(isNeedCustom.toUpperCase(),"Y") || Objects.equals(isNeedCustom.toLowerCase(),"yes")){
                    String customName = input(br, "请输入自定义实体名", "如:表名aa,自定义实体名Test");
                    codeGeneratorManager.genCodeWithCustomName(schema,sourceStrArray[0],customName);
                }else {
                    codeGeneratorManager.genCodeByTableName(schema,sourceStrArray[0]);
                }
            }else {
                codeGeneratorManager.genCodeByTableName(schema,sourceStrArray);
            }
            System.out.println("\n正在处理...\n");
        } catch (IOException e) {
            System.out.printf("程序崩溃*_*，原因:%s\n", e.getMessage());
        }

        log.info("==========================代码生成完成==========================");

    }

    private void initLog(){
        String customizedPath = "log4j.properties";
        System.setProperty("log4j.configuration", customizedPath);
    }

    /**
     * 读取用户输入值
     *
     * @param br
     * @param value
     * @param tip
     * @throws IOException
     */
    private static String input(BufferedReader br, String value, String tip) throws IOException {
        System.out.printf("请输入%s：", value);
        String read = br.readLine();
        while (read == null || "".equals(read.trim())) {
            System.out.printf("输入不合法，格式：<%s>\n", tip);
            System.out.printf("请重新输入%s：", value);
            read = br.readLine();
        }
        return read.trim();
    }

    private static String input2(BufferedReader br, String value, String tip) throws IOException {
        System.out.printf("%s：", value);
        String read = br.readLine();
        while (read == null || "".equals(read.trim())) {
            System.out.printf("输入不合法，格式：<%s>\n", tip);
            System.out.printf("%s：", value);
            read = br.readLine();
        }
        return read.trim();
    }

}
