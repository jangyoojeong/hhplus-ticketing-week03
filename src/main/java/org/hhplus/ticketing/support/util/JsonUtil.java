package org.hhplus.ticketing.support.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

public class JsonUtil {
    private JsonUtil() {
    }

    public static String toJson(Object target) {
        String result = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            ObjectWriter writer = mapper.writer().withoutAttribute("logger").withoutAttribute("typeName");
            return writer.writeValueAsString(target);
        } catch (JsonProcessingException var4) {
            var4.printStackTrace();
            return result;
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        T result = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            result = mapper.readValue(json, clazz);
        } catch (JsonProcessingException var5) {
            var5.printStackTrace();
        }

        return result;
    }
}
