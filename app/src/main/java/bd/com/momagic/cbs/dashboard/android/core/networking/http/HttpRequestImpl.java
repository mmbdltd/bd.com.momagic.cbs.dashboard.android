package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import bd.com.momagic.cbs.dashboard.android.core.common.Transformer;
import bd.com.momagic.cbs.dashboard.android.core.text.Encoder;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;

class HttpRequestImpl<Type> implements HttpRequest<Type> {

    private boolean insecure = true;
    private int retryCount;
    private String url;
    private HttpMethod method = HttpMethod.GET;
    private final Map<String, Set<String>> headers = new TreeMap<>();
    private Type body;
    private Transformer<Type> transformer;

    HttpRequestImpl() { }

    @Override
    public Transformer<Type> getBodyTransformer() {
        return transformer;
    }

    @Override
    public HttpRequest<Type> setBodyTransformer(final Transformer<Type> transformer) {
        this.transformer = transformer;

        return this;
    }

    @Override
    public boolean isInsecure() {
        return insecure;
    }

    @Override
    public HttpRequest<Type> setInsecure(final boolean insecure) {
        this.insecure = insecure;

        return this;
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public HttpRequest<Type> setRetryCount(final int retryCount) {
        this.retryCount = retryCount;

        return this;
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public HttpRequest<Type> setMethod(final HttpMethod method) {
        this.method = method;

        return this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public HttpRequest<Type> setUrl(final String url) {
        this.url = url;

        return this;
    }

    @Override
    public HttpRequest<Type> addHeader(
            final String headerName,
            final String headerValue) {
        // first, we'll try to get the list of header values...
        Set<String> headerValues = headers.get(headerName);

        // if header values list is null...
        if (headerValues == null) {
            // we'll create a new set...
            headerValues = new TreeSet<>();

            // and put the header values to the headers map...
            headers.put(headerName, headerValues);
        }

        // then we shall add the header value...
        headerValues.add(headerValue);

        return this;
    }

    @Override
    public HttpRequest<Type> removeHeader(
            final String headerName,
            final String headerValue) {
        // first, we'll try to get the list of header values...
        final Set<String> headerValues = headers.get(headerName);

        // if header values does not exist, we shall not proceed any further...
        if (headerValues == null) { return this; }

        // otherwise, we shall remove the header value from the set...
        headerValues.remove(headerValue);

        return this;
    }

    @Override
    public Map<String, Set<String>> getHeaders() {
        return headers;
    }

    @Override
    public HttpRequest<Type> clearHeaders() {
        headers.clear();

        return this;
    }

    @Override
    public byte[] getBodyAsBytes() {
        return transformer.transform(getBody());
    }

    @Override
    public String getBodyAsString() {
        final Type body = getBody();

        if (body instanceof String) { return (String) body; }

        final byte[] bodyAsBytes = getBodyAsBytes();

        return Encoder.toUtf8(bodyAsBytes, 0, bodyAsBytes.length);
    }

    @Override
    public Type getBody() {
        return body;
    }

    @Override
    public HttpRequest<Type> setBody(final Type body) {
        this.body = body;

        return this;
    }

    @Override
    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson(true);
    }
}
