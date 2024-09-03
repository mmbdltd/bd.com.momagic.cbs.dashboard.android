package bd.com.momagic.cbs.dashboard.android.core.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bd.com.momagic.cbs.dashboard.android.core.text.Encoder;
import bd.com.momagic.cbs.dashboard.android.core.text.Encoding;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class JsonWebTokenUtilities {

    private static final Logger logger = LoggerFactory.getLogger(JsonWebTokenUtilities.class);

    private static Map<String, Object> decodePart(String partOfToken, Encoding encoding) {
        String tokenContentAsJson;

        try {
            final byte[] tokenContentAsBytes = Encoder.decode(partOfToken, encoding);
            tokenContentAsJson = Encoder.encode(tokenContentAsBytes, Encoding.UTF_8);
        } catch (Exception exception) {
            logger.warn("An exception occurred while decoding part of token.", exception);

            // in-case of exception, we shall return an empty map...
            return Collections.emptyMap();
        }

        final Map<String, Object> tokenContent = JsonSerializer.deserializeAsMap(tokenContentAsJson);

        // if token content is null or empty, we shall return an empty map...
        if (tokenContent == null || tokenContent.isEmpty()) { return Collections.emptyMap(); }

        return tokenContent;
    }

    public static Map<String, Object> decode(String token, Encoding encoding) {
        // sanitizing the token...
        token = StringUtilities.getDefaultIfNullOrWhiteSpace(
                token, StringUtilities.getEmptyString(), true);

        if (StringUtilities.isEmpty(token)) { return Collections.emptyMap(); }

        // first we shall split the token...
        final String[] splitToken = token.split("\\.");

        if (splitToken.length != 3) { return Collections.emptyMap(); }

        final String header = splitToken[0];
        final Map<String, Object> decodedHeader = decodePart(header, encoding);
        final String payload = splitToken[1];
        final Map<String, Object> decodedPayload = decodePart(payload, encoding);
        final String signature = splitToken[2];

        final Map<String, Object> tokenContent = new HashMap<>();
        tokenContent.put("header", header);
        tokenContent.put("decodedHeader", decodedHeader);
        tokenContent.put("payload", payload);
        tokenContent.put("decodedPayload", decodedPayload);
        tokenContent.put("signature", signature);

        return tokenContent;
    }

    public static Object extractValue(final String attributeName, final String token) {
        final Map<String, Object> decodedToken = decode(token, Encoding.BASE_64);
        @SuppressWarnings("unchecked")
        final Map<String, Object> decodedTokenPayload = (Map<String, Object>) decodedToken.get("decodedPayload");

        return decodedTokenPayload == null
                ? null
                : decodedTokenPayload.get(attributeName);
    }
}
