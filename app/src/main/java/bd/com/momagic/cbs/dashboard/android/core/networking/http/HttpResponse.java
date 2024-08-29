package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import java.util.Map;
import java.util.Set;

public interface HttpResponse<RequestBodyType> {
    int getStatusCode();
    HttpResponse<RequestBodyType> setStatusCode(final int statusCode);

    String getMessage();
    HttpResponse<RequestBodyType> setMessage(final String message);

    HttpRequest<RequestBodyType> getRequest();
    HttpResponse<RequestBodyType> setRequest(final HttpRequest<RequestBodyType> request);

    Map<String, Set<String>> getHeaders();
    HttpResponse<RequestBodyType> setHeaders(final Map<String, Set<String>> headers);

    byte[] getBody();
    HttpResponse<RequestBodyType> setBody(final byte[] body);
    String getBodyAsString();

    Exception getException();
    HttpResponse<RequestBodyType> setException(final Exception exception);

    long getElapsedTime();
    HttpResponse<RequestBodyType> setElapsedTime(final long elapsedTime);

    String getHumanReadableElapsedTime();
    HttpResponse<RequestBodyType> setHumanReadableElapsedTime(final String humanReadableElapsedTime);

    String toJson(final boolean prettyPrint);

    static <RequestBodyType> HttpResponse<RequestBodyType> create() {
        return new HttpResponseImpl<>();
    }
}
