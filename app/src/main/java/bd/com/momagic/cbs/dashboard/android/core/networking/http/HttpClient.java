package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;

public interface HttpClient {

    <RequestBodyType> HttpResponse<RequestBodyType> sendRequest(
            final HttpRequest<RequestBodyType> request);

    <RequestBodyType> AsyncTask<HttpResponse<RequestBodyType>> sendRequestAsync(
            final HttpRequest<RequestBodyType> request);

    static void initialize() throws Exception {
        HttpClientImpl.initialize();
    }

    /**
     * Creates a new instance of HttpClient class.
     * @param configuration Configuration of the HttpClient.
     * @return Returns a new instance.
     */
    static HttpClient create(final HttpClientConfiguration configuration) {
        return new HttpClientImpl(configuration);
    }
}
