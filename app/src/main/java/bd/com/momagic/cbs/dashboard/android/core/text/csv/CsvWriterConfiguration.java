package bd.com.momagic.cbs.dashboard.android.core.text.csv;

import bd.com.momagic.cbs.dashboard.android.core.text.JsonSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CsvWriterConfiguration {

    private boolean enabled;
    @Getter(AccessLevel.NONE)
    private boolean escapeValues;
    @Getter(AccessLevel.NONE)
    private boolean flushAutomatically;
    private int bufferLength;
    private String filePathFormat;
    private String[] headers;

    public CsvWriterConfiguration(CsvWriterConfiguration configuration) {
        enabled = configuration.enabled;
        escapeValues = configuration.escapeValues;
        flushAutomatically = configuration.flushAutomatically;
        bufferLength = configuration.bufferLength;
        filePathFormat = configuration.filePathFormat;
        headers = configuration.headers;
    }

    public boolean shallEscapeValues() {
        return escapeValues;
    }

    public boolean shallFlushAutomatically() {
        return flushAutomatically;
    }

    @Override
    public String toString() {
        return JsonSerializer.serialize(this);
    }
}
