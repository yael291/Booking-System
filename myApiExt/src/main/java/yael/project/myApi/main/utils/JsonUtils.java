package yael.project.myApi.main.utils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtils {
    private static final String UTF_8 = "UTF-8";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public JsonUtils() {
    }

    public static <T> T jsonToObject(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        } else {
            T obj = null;

            try {
                InputStream is = new ByteArrayInputStream(json.getBytes("UTF-8"));
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                obj = mapper.readValue(is, clazz);
                return obj;
            } catch (IOException var5) {
                logger.error("Failed to map json {} to class {}", json, clazz);
                throw new RuntimeException("Failed to map json [" + json + "] to class " + clazz, var5);
            }
        }
    }

    public static <T> T jsonToObject(String json, TypeReference<T> valueTypeRef) {
        T obj = null;

        try {
            InputStream is = new ByteArrayInputStream(json.getBytes("UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            obj = mapper.readValue(is, valueTypeRef);
            return obj;
        } catch (IOException var5) {
            logger.error("Failed to map json [{}] to object", json);
            throw new RuntimeException("Failed to map json [" + json + "] to object", var5);
        }
    }

    public static String objectToJson(Object obj) {
        return objectToJson(obj, true);
    }

    public static String objectToJson(Object obj, boolean withoutWhiteSpaces) {
        String json = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            if (withoutWhiteSpaces) {
                json = mapper.writer().writeValueAsString(obj);
            } else {
                json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }

            return json;
        } catch (JsonProcessingException var4) {
            logger.error("Failed to convert object [{}] to JSON", obj.getClass().getName());
            throw new RuntimeException("Failed to convert object [" + obj.getClass().getName() + "] to JSON", var4);
        }
    }

    public static <T> T fileToObject(String fileName, Class<T> clazz) {
        try {
            String json = readFromResource(fileName);
            T configuration = null;
            if (json != null && !json.isEmpty()) {
                configuration = jsonToObject(json, clazz);
            }

            return configuration;
        } catch (Exception var4) {
            logger.error("error reading file resource {}", var4);
            return null;
        }
    }

    private static String readFromResource(String fileName) throws IOException {
        InputStream inputStream = JsonUtils.class.getResourceAsStream(fileName);
        StringBuilder resultStringBuilder = new StringBuilder();
        if (inputStream == null) {
            logger.error("file resource not found {}", fileName);
            throw new FileNotFoundException(fileName);
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            try {
                while((line = br.readLine()) != null) {
                    resultStringBuilder.append(line).append("\n");
                }
            } catch (Throwable var7) {
                try {
                    br.close();
                } catch (Throwable var6) {
                    var7.addSuppressed(var6);
                }

                throw var7;
            }

            br.close();
            return resultStringBuilder.toString();
        }
    }
}
