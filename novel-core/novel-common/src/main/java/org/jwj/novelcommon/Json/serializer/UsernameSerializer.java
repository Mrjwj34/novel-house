package org.jwj.novelcommon.Json.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 用户名序列化器, 将敏感信息隐藏
 */
public class UsernameSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String s, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        // 将用户名的中间部分隐藏
        jsonGenerator.writeString(s.substring(0, 4) + "****" + s.substring(8));
    }
}
