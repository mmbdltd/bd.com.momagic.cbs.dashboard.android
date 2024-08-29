package bd.com.momagic.cbs.dashboard.android.core.networking.http;

import androidx.annotation.NonNull;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import bd.com.momagic.cbs.dashboard.android.core.text.csv.CsvWriterConfiguration;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HttpClientConfiguration {

    private boolean hostnameVerificationDisabled;
    private boolean securityDisabled;
    // the number of times the basic authentication filter will attempt
    // to retry a failed authentication...
    private int basicAuthenticationRetryLimit;
    private int maximumRetryCount;
    private int retryIntervalInMilliseconds;
    private int lowerLimitOfStatusCodeToRetry;
    private int upperLimitOfStatusCodeToRetry;
    private List<Integer> statusCodesToRetry;           // <-- upon receiving these status codes, retry shall be performed...
    private int connectTimeoutInMilliseconds;
    private int readTimeoutInMilliseconds;
    private String preferredHttpVersion;
    private int defaultMaximumAsynchronousParallelRequestsPerHost;
    private int headerValuesListInitialCapacity;
    private int headersMapInitialCapacity;
    private String userAgent;
    private Map<String, Integer> hostBasedMaximumAsynchronousParallelRequests;
    private CsvWriterConfiguration csvWriter;

    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson(true);
    }
}
