package com.turingSecApp.turingSec.util;

import java.util.Map;

public class EmailTemplateProcessor {

    public static String processTemplate(String template, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}