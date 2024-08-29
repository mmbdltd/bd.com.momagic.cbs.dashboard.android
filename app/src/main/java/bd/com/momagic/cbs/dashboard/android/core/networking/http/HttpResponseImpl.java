package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.Set;

import bd.com.momagic.cbs.dashboard.android.core.text.Encoder;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;

class HttpResponseImpl<RequestBodyType> implements HttpResponse<RequestBodyType> {
    private int statusCode;
    private String message;
    private HttpRequest<RequestBodyType> request;
    private Map<String, Set<String>> headers;
    private byte[] body;
    private Exception exception;
    private long elapsedTime;
    private String humanReadableElapsedTime;

    HttpResponseImpl() { }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public HttpResponse<RequestBodyType> setStatusCode(final int statusCode) {
        this.statusCode = statusCode;

        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpResponse<RequestBodyType> setMessage(final String message) {
        this.message = message;

        return this;
    }

    @Override
    public HttpRequest<RequestBodyType> getRequest() {
        return request;
    }

    @Override
    public HttpResponse<RequestBodyType> setRequest(
            final HttpRequest<RequestBodyType> request) {
        this.request = request;

        return this;
    }

    @Override
    public Map<String, Set<String>> getHeaders() {
        return headers;
    }

    @Override
    public HttpResponse<RequestBodyType> setHeaders(final Map<String, Set<String>> headers) {
        this.headers = headers;

        return this;
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public HttpResponse<RequestBodyType> setBody(final byte[] body) {
        this.body = body;

        return this;
    }

    @Override
    public String getBodyAsString() {
        if (body == null || body.length == 0) { return StringUtilities.getEmptyString(); }

        return Encoder.toUtf8(body, 0, body.length);
    }

    @Override
    public Exception getException() {
        return exception;
    }

    @Override
    public HttpResponse<RequestBodyType> setException(final Exception exception) {
        this.exception = exception;

        return this;
    }

    @Override
    public long getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public HttpResponse<RequestBodyType> setElapsedTime(final long elapsedTime) {
        this.elapsedTime = elapsedTime;

        return this;
    }

    @Override
    public String getHumanReadableElapsedTime() {
        return humanReadableElapsedTime;
    }

    @Override
    public HttpResponse<RequestBodyType> setHumanReadableElapsedTime(
            final String humanReadableElapsedTime) {
        this.humanReadableElapsedTime = humanReadableElapsedTime;

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
