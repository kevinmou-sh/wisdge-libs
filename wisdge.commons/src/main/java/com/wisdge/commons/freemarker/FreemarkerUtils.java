package com.wisdge.commons.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.util.Map;

public class FreemarkerUtils {

    /**
     * 从一个文件进行模版合并
     * @param templateFilename
     * @param params
     * @return
     * @throws Exception
     */
    public static String processByTemplate(String templateFilename, Map params) throws Exception {
        // 创建配置类
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        // 设置字符集
        configuration.setDefaultEncoding("UTF-8");

        // 设置模板路径 toURI()防止路径出现空格
        String classpath = FreemarkerUtils.class.getResource("/").toURI().getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath+"/templates/"));

        // 加载模板
        Template template = configuration.getTemplate(templateFilename);
        // 静态化
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
    }

    /**
     * 从一段字符串进行模版合并
     * @param content
     * @param params
     * @return
     * @throws Exception
     */
    public static String processByString(String content, Map params) throws Exception {
        // 创建配置类
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        // 设置字符集
        configuration.setDefaultEncoding("UTF-8");
        // 模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", content);
        configuration.setTemplateLoader(stringTemplateLoader);
        // 加载模板
        Template template = configuration.getTemplate("template", "UTF-8");
        // 静态化
        String result = FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
        stringTemplateLoader.removeTemplate("template");
        return result;
    }
}
