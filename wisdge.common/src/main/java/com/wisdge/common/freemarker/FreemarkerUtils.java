package com.wisdge.common.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

public class FreemarkerUtils {
    public static String freemarkerByTemplate(String templateFilename, Map params) throws Exception {
        // 创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 设置字符集
        configuration.setDefaultEncoding("UTF-8");
        // 加载模板
        Template template = configuration.getTemplate(templateFilename);
        // 静态化
        return FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
    }

    public static String freemarkerByString(String content, Map params) throws Exception {
        // 创建配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
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
