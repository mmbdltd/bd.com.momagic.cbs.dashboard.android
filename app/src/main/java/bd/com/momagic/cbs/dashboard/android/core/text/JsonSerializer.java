package bd.com.momagic.cbs.dashboard.android.core.text;

import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss:SSS a z", Locale.US);

    private static final ObjectMapper primaryObjectMapper = (new ObjectMapper())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .setDateFormat(dateFormat)
            .enable(SerializationFeature.INDENT_OUTPUT);

    private static final ObjectMapper secondaryObjectMapper = (new ObjectMapper())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setDateFormat(dateFormat);

    public static String serialize(Object object) {
        return serialize(object, true);
    }


    public static String serialize(Object object, boolean prettyPrint) {
        ObjectMapper objectMapper = prettyPrint ? primaryObjectMapper : secondaryObjectMapper;

        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            logger.error("An exception occurred while serializing object as JSON.", exception);

            return StringUtilities.getEmptyString();
        }
    }

    private static <Type> Type _deserialize(String json, Class<Type> classOfType) throws Exception {
        return primaryObjectMapper.readValue(json, classOfType);
    }

    private static <Type> Type _deserialize(String json, TypeReference<Type> typeReference) throws Exception {
        return primaryObjectMapper.readValue(json, typeReference);
    }

    public static <Type> Type deserialize(String json, Class<Type> classOfType) {
        try {
            return _deserialize(json, classOfType);
        } catch (Exception exception) {
            logger.error("An exception occurred while deserializing JSON as object.", exception);

            return null;
        }
    }


    public static <Type> Type deserialize(String json, Class<Type> classOfType, boolean throwException) throws Exception {
        if (throwException) {
            return _deserialize(json, classOfType);
        }


        return deserialize(json, classOfType);
    }

    private static <Type> Type deserialize(String json, TypeReference<Type> typeReference) {
        try {
            return _deserialize(json, typeReference);
        } catch (Exception exception) {
            logger.error("An exception occurred while deserializing JSON as object.", exception);

            return null;
        }
    }


    private static <Type> Type deserialize(String json, TypeReference<Type> typeReference, boolean throwException) throws Exception {
        if (throwException) {
            return _deserialize(json, typeReference);
        }

        return deserialize(json, typeReference);
    }

    public static <ValueType> List<ValueType> deserializeAsList(String json) {
        TypeReference<List<ValueType>> typeReference = new TypeReference<List<ValueType>>() { };

        return deserialize(json, typeReference);
    }


    public static <ValueType> List<ValueType> deserializeAsList(String json, boolean throwException) throws Exception {
        TypeReference<List<ValueType>> typeReference = new TypeReference<List<ValueType>>() { };

        return deserialize(json, typeReference, throwException);
    }

    public static <KeyType, ValueType> Map<KeyType, ValueType> deserializeAsMap(String json) {
        TypeReference<Map<KeyType, ValueType>> typeReference = new TypeReference<Map<KeyType, ValueType>>() { };

        return deserialize(json, typeReference);
    }


    public static <KeyType, ValueType> Map<KeyType, ValueType> deserializeAsMap(String json, boolean throwException) throws Exception {
        TypeReference<Map<KeyType, ValueType>> typeReference = new TypeReference<Map<KeyType, ValueType>>() { };

        return deserialize(json, typeReference, throwException);
    }
}
