package bd.com.momagic.cbs.dashboard.android.core.threading;

import androidx.annotation.NonNull;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import lombok.Data;

@Data
public class AsyncTaskConfiguration {

    private int minimumPlatformThreadCount;
    private int maximumPlatformThreadCount;

    /**
     * This multiplier value is used to determine the number of platform threads to use.
     * If the value (after multiplying available processor count by the multiplier)
     * is less than the minimum platform thread count or exceeds the maximum platform
     * thread count, the value shall be discarded. Thus, the minimum or the maximum
     * platform thread count shall be utilized respectively.
     */
    private float availableProcessorCountMultiplier;

    public String toJson(final boolean prettyPrint) {
        return JsonSerializer.serialize(this, prettyPrint);
    }

    @NonNull
    @Override
    public String toString() {
        return toJson(true);
    }
}
