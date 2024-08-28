package bd.com.momagic.cbs.dashboard.android.core.text.csv;

public interface CsvWriter {

    boolean write(String csv);

    boolean write(Object... values);

    void flush();

    void close();

    static CsvWriter getInstance(String key, CsvWriterConfiguration configuration) {
        return CsvWriterImpl.getInstance(key, configuration);
    }
}
