package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import bd.com.momagic.cbs.dashboard.android.core.common.Stopwatch;
import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.text.csv.CsvWriterConfiguration;
import bd.com.momagic.cbs.dashboard.android.core.threading.AsyncTask;
import bd.com.momagic.cbs.dashboard.android.core.threading.LimitedAsyncTaskExecutor;
import bd.com.momagic.cbs.dashboard.android.core.text.csv.CsvWriter;
import bd.com.momagic.cbs.dashboard.android.core.utilities.DateTimeFormatter;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ExceptionUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StreamUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.ThreadUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.UrlUtilities;

class HttpClientImpl implements HttpClient {

    private final Logger logger = LoggerFactory.getLogger(HttpClientImpl.class);
    private final HttpClientConfiguration configuration;
    private static final HostnameVerifier insecureHostnameVerifier = new InsecureHostnameVerifier();
    private static SSLSocketFactory insecureSslSocketFactory;
    private final LimitedAsyncTaskExecutor limitedAsyncTaskExecutor;
    private final CsvWriter csvWriter;

    private static final String CSV_WRITER_KEY = HttpClientImpl.class.getName() + "_outgoing-requests";
    private static final String[] CSV_HEADERS = new String[] {
            "method", "request_url", "status_code", "message", "time_elapsed", "attempt", "requested_at",
            "response_received_at", "time_elapsed_in_milliseconds", "requested_at_in_milliseconds",
            "response_received_at_in_milliseconds", "request_headers", "request_body", "response_body", "stack_trace"
    };
    private static final String DATE_TIME_FORMAT_PATTERN = "dd-MMM-yyyy hh:mm:ss:SSS a z";

    HttpClientImpl(final HttpClientConfiguration configuration) {
        this.configuration = configuration;
        limitedAsyncTaskExecutor = new LimitedAsyncTaskExecutor(
                this.configuration.getDefaultMaximumAsynchronousParallelRequestsPerHost(),
                this.configuration.getHostBasedMaximumAsynchronousParallelRequests());

        // setting CSV headers to configuration...
        final CsvWriterConfiguration csvWriterConfiguration = configuration.getCsvWriter();
        csvWriterConfiguration.setHeaders(CSV_HEADERS);

        csvWriter = CsvWriter.getInstance(CSV_WRITER_KEY, csvWriterConfiguration);
    }

    @Override
    public <RequestBodyType> HttpResponse<RequestBodyType> sendRequest(
            final HttpRequest<RequestBodyType> request) {
        return sendHttpRequestWithRetry(request, configuration, logger, csvWriter);
    }

    @Override
    public <RequestBodyType> AsyncTask<HttpResponse<RequestBodyType>> sendRequestAsync(
            final HttpRequest<RequestBodyType> request) {
        // extracting the host from the request URL...
        // NOTE: THIS HOST SHALL BE USED AS THE TASK
        // EXECUTION CONTEXT...
        final String host = UrlUtilities.extractHost(request.getUrl());

        // asynchronously sends HTTP request
        return limitedAsyncTaskExecutor.run(host, () -> sendRequest(request));
    }

    private static SSLSocketFactory createInsecureSocketFactory() throws Exception {
        final TrustManager[] trustManagers = new TrustManager[] {
                new InsecureTrustManager(),
        };
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, new SecureRandom());

        return sslContext.getSocketFactory();
    }

    private static HttpURLConnection createHttpUrlConnection(
            final String requestUrl,
            final boolean isInsecure) throws Exception {
        final URL url = new URL(requestUrl);
        final URLConnection urlConnection = url.openConnection();

        if (isInsecure && urlConnection instanceof HttpsURLConnection) {
            final HttpsURLConnection connection = (HttpsURLConnection) urlConnection;
            connection.setHostnameVerifier(insecureHostnameVerifier);
            connection.setSSLSocketFactory(insecureSslSocketFactory);
        }

        return (HttpURLConnection) urlConnection;
    }

    private static Map<String, Set<String>> processHeaders(final Map<String, List<String>> headers) {
        if (headers == null || headers.isEmpty()) { return Collections.emptyMap(); }

        final Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
        final Map<String, Set<String>> processedHeaders = new TreeMap<>();

        for (final Map.Entry<String, List<String>> entry : entrySet) {
            final String key = entry.getKey();

            if (StringUtilities.isNull(key)) { continue; }

            final List<String> valueList = entry.getValue();

            if (valueList == null || valueList.isEmpty()) { continue; }

            final Set<String> valueSet = new TreeSet<>(valueList);

            processedHeaders.put(entry.getKey(), valueSet);
        }

        return processedHeaders;
    }

    private static <RequestBodyType> HttpResponse<RequestBodyType> sendHttpRequest(
            final HttpRequest<RequestBodyType> request,
            final HttpClientConfiguration configuration,
            final Logger logger) {
        final HttpResponse<RequestBodyType> response = HttpResponse.create();
        response.setRequest(request);

        try {
            final HttpURLConnection connection = createHttpUrlConnection(request.getUrl(), request.isInsecure());
            connection.setRequestMethod(request.getMethod().name());
            connection.setConnectTimeout(configuration.getConnectTimeoutInMilliseconds());
            connection.setReadTimeout(configuration.getReadTimeoutInMilliseconds());

            // if we have request body...
            if (request.getBody() != null) {
                connection.setDoOutput(true);

                // writing the request body...
                try (final OutputStream outputStream = connection.getOutputStream()) {
                    final byte[] requestBodyAsBytes = request.getBodyAsBytes();
                    outputStream.write(requestBodyAsBytes, 0, requestBodyAsBytes.length);
                    outputStream.flush();
                }
            }

            // setting headers...
            response.setHeaders(processHeaders(connection.getHeaderFields()));
            // setting status code...
            response.setStatusCode(connection.getResponseCode());
            // setting message...
            response.setMessage("Received HTTP status code " + response.getStatusCode() + ".");

            // reading content (as bytes) from the input stream...
            final byte[] content = StreamUtilities.readBytes(connection.getInputStream());

            // setting body...
            response.setBody(content);
        } catch (final Exception exception) {
            logger.warn("An exception occurred while sending '" + request.getMethod().name()
                    + "' request to url, '" + request.getUrl() + "'.", exception);

            // setting exception...
            response.setException(exception);
            response.setMessage(exception.getMessage());
        }

        return response;
    }

    private static <RequestBodyType> HttpResponse<RequestBodyType> sendHttpRequestWithRetry(
            final HttpRequest<RequestBodyType> request,
            final HttpClientConfiguration configuration,
            final Logger logger,
            final CsvWriter csvWriter) {
        final Stopwatch stopwatch = Stopwatch.create().start();
        boolean shallRetry;
        int retryCount = request.getRetryCount();
        int attempt = 1;
        HttpResponse<RequestBodyType> response;

        do {
            // sending HTTP request and retrieving response...
            response = sendHttpRequest(request, configuration, logger);
            // checking if we shall perform retry...
            shallRetry =
                    // if the status code is greater than or equal to the lower limit...
                    (response.getStatusCode() >= configuration.getLowerLimitOfStatusCodeToRetry()
                    // and the status code is less than or equal to the upper limit...
                    && response.getStatusCode() <= configuration.getUpperLimitOfStatusCodeToRetry())
                    // or the status code is present within the list of status codes...
                    || configuration.getStatusCodesToRetry().contains(response.getStatusCode())
                    // or there is an exception...
                    || response.getException() != null;

            // if we don't need to retry or if all the retry attempts failed,
            // we shall return the response...
            if (!shallRetry || retryCount == 0) { break; }

            // incrementing attempt...
            ++attempt;
            // decrementing retry count...
            --retryCount;

            if (configuration.getRetryIntervalInMilliseconds() < 1) {
                logger.info("Attempting retry.");
            } else {
                logger.info("Attempting retry in " + DateTimeFormatter.formatTime(configuration.getRetryIntervalInMilliseconds()) + ".");

                // we shall wait for a while before attempting retry...
                // NOTE: WE MAY NOT ALWAYS NEED ADDITIONAL WAIT BEFORE RETRY
                // e.g. Internal socket connection of HttpURLConnection waits for a while
                // before throwing java.net.SocketTimeoutException...
                ThreadUtilities.trySleep(configuration.getRetryIntervalInMilliseconds());
            }
        } while (true);

        stopwatch.stop();

        // setting elapsed time...
        response.setElapsedTime(stopwatch.getElapsedTime());
        // setting human readable elapsed time...
        response.setHumanReadableElapsedTime(stopwatch.getHumanReadableElapsedTime());

        // if CSV writer is not enabled, we shall not proceed any further...
        if (!configuration.getCsvWriter().isEnabled()) { return response; }

        // serializing the request headers...
        final String requestHeaders = request.getHeaders() == null
                ? StringUtilities.getEmptyString()
                : JsonSerializer.serialize(request.getHeaders(), false);
        // retrieving request body from the body publisher...
        final String requestBody = request.getBodyAsString();
        // retrieving response body from the response...
        final String responseBody = response.getBodyAsString();
        // retrieving exception stack trace...
        final String stackTrace = ExceptionUtilities.retrieveStackTrace(response.getException());
        // creating a new date format...
        // NOTE: NEED TO CREATE NEW DATE FORMAT BECAUSE SimpleDateFormat CLASS IS NOT THREAD-SAFE...
        final DateFormat dateTimeFormat = DateTimeFormatter.createDateFormat(DATE_TIME_FORMAT_PATTERN);
        // time at which the request was initiated...
        final String requestedAt = dateTimeFormat.format(new Date(stopwatch.getStartTime()));
        // time at which the response was received...
        final String responseReceivedAt = dateTimeFormat.format(new Date(stopwatch.getEndTime()));

        // writing to CSV file...
        csvWriter.write(request.getMethod().name(), request.getUrl(), response.getStatusCode(), response.getMessage(),
                stopwatch.getHumanReadableElapsedTime(), attempt, requestedAt, responseReceivedAt,
                stopwatch.getElapsedTime(), stopwatch.getStartTime(), stopwatch.getEndTime(),
                requestHeaders, requestBody, responseBody, stackTrace);

        // lastly, we shall return the response...
        return response;
    }

    static void initialize() throws Exception {
        // if insecure SSL socket factory is null...
        if (insecureSslSocketFactory == null) {
            // we shall create new insecure SSL socket factory...
            insecureSslSocketFactory = createInsecureSocketFactory();
        }
    }
}
