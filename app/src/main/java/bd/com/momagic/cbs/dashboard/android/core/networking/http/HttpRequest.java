package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import java.util.Map;
import java.util.Set;

import bd.com.momagic.cbs.dashboard.android.core.common.Transformer;

public interface HttpRequest<Type> {
    Transformer<Type> getBodyTransformer();
    HttpRequest<Type> setBodyTransformer(final Transformer<Type> transformer);

    boolean isInsecure();
    HttpRequest<Type> setInsecure(final boolean insecure);

    int getRetryCount();
    HttpRequest<Type> setRetryCount(final int retryCount);

    HttpMethod getMethod();
    HttpRequest<Type> setMethod(final HttpMethod method);

    String getUrl();
    HttpRequest<Type> setUrl(final String url);

    HttpRequest<Type> addHeader(final String headerName, final String headerValue);
    HttpRequest<Type> removeHeader(final String headerName, final String headerValue);
    Map<String, Set<String>> getHeaders();
    HttpRequest<Type> clearHeaders();

    byte[] getBodyAsBytes();
    String getBodyAsString();
    Type getBody();
    HttpRequest<Type> setBody(final Type body);

    String toJson(final boolean prettyPrint);

    static <Type> HttpRequest<Type> create() {
        return new HttpRequestImpl<>();
    }

    static HttpRequest<String> createForString() {
        return new HttpRequestImpl<String>()
                .setBodyTransformer(Transformer.createStringTransformer());
    }
}
