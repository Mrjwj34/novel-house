package org.jwj.novelconfig.wrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Map;

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static final Map<String, String> REPLACE_RULE = new HashMap<>();

    static {
        REPLACE_RULE.put("<", "&lt;");
        REPLACE_RULE.put(">", "&gt;");
    }
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        if (parameterValues != null) {
            // 遍历所有请求参数并替换其中的尖括号
            int length = parameterValues.length;
            String[] values = new String[length]; // 创建请求参数副本, 防止对原数组进行修改
            for (int i = 0; i < length; i++) {
                values[i] = parameterValues[i];
                int index = i;// lambda表达式中必须使用final对象或不可变对象
                REPLACE_RULE.forEach((k, v) ->
                        values[index] = values[index].replaceAll(k, v));
            }
            return values;
        }
        return null;
    }
}
