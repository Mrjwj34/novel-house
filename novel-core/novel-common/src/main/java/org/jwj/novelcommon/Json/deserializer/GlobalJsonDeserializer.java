package org.jwj.novelcommon.Json.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class GlobalJsonDeserializer {
    /**
     * 自定义字符串反序列化器, 用来过滤特殊字符，解决 XSS 攻击
     */
    public static class StringDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser jsonParser,
                                  DeserializationContext ctxt) throws IOException {
            return jsonParser.getValueAsString()
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }
}
