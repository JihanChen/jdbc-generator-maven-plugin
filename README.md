# jdbc-generator-maven-plugin
## 介绍
自动生成jdbctemplate代码，生成DO & Dao & Service & ServiceImpl & Controller。
A Maven plugin for generate jdbctemplate code.
generate DO & Dao & Service & ServiceImpl & Controller.

## 使用方式
1.mvn clean install当前项目代码 <br>
2.引用插件到maven项目的pom.xml中
  ```java
    <build>
        <plugins>
            <plugin>
                <groupId>com.jihan</groupId>
                <artifactId>jdbc-generator-maven-plugin</artifactId>
                <version>1.0.0-SNAPSHOT</version>
            </plugin>
        </plugins>
    </build>
  ```
  <br>
3. 修改配置文件 <br>
generator-jdbc.properties 修改数据库连接信息
generator-config.properties 修改生成内容包名等信息
需要把这两个配置文件放入到实际需要生成代码的maven项目中的resources文件夹下

