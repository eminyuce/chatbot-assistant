package com.yuce.chat.assistant.util;

import com.yuce.chat.assistant.persistence.entity.Drug;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class HtmlTextUtil {

    private static final HtmlTextUtil instance = new HtmlTextUtil();

    public static HtmlTextUtil getInstance() {
        return instance;
    }

    private final Configuration cfg;

    public HtmlTextUtil() {
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateUpdateDelayMilliseconds(0); // always reload templates (no caching)
    }

    public String formatDrugResponse(Drug drug) {
        try {
            Template template = cfg.getTemplate("drug.ftl");
            Map<String, Object> model = new HashMap<>();
            model.put("drug", drug);
            model.put("formattedDate", drug.getExpiredDate() != null
                    ? drug.getExpiredDate().toLocalDate().toString()
                    : "Not specified");
            StringWriter out = new StringWriter();
            template.process(model, out);
            return out.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return "<p>Error generating drug response</p>";
        }
    }
}
