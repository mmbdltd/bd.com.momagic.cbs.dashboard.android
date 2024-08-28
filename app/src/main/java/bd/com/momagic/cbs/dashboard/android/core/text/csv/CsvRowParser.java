package bd.com.momagic.cbs.dashboard.android.core.text.csv;

import java.util.Map;

public interface CsvRowParser {
    Object parseRow(final Map<String, String> rowData);
}
