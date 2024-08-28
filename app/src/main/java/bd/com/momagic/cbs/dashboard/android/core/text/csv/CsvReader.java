package bd.com.momagic.cbs.dashboard.android.core.text.csv;

import bd.com.momagic.cbs.dashboard.android.core.utilities.StreamUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class CsvReader {

    private static final Logger logger = LoggerFactory.getLogger(CsvReader.class);

    private static final ObjectMapper objectMapper = new CsvMapper();
    private static final ObjectReader objectReader = objectMapper
            .readerForListOf(String.class)
            .with(CsvParser.Feature.WRAP_AS_ARRAY);

    private static final int LIST_INITIAL_CAPACITY = 8192;

    private CsvReader() { }

    public static List<Object> readContent(final String content, final CsvRowParser rowParser) {
        // if content is empty, we shall return an empty list...
        if (StringUtilities.isEmpty(content)) { return Collections.emptyList(); }

        // initializing a list to hold data...
        final List<Object> data = new ArrayList<>(LIST_INITIAL_CAPACITY);

        try (final MappingIterator<Object> mappingIterator = objectReader.readValues(content)) {
            int rowCount = 0;
            List<?> headers = Collections.emptyList();

            while (mappingIterator.hasNext()) {
                final Object row = mappingIterator.nextValue();

                if (!(row instanceof List<?>)) { continue; }

                final List<?> columns = (List<?>) row;

                // incrementing the row count...
                ++rowCount;

                // first row must contain headers...
                if (rowCount == 1) {
                    // so assigning the headers...
                    headers = columns;

                    continue;
                }

                // populating row data as map...
                final Map<String, String> rowDataAsMap = populateMap(headers, columns);
                // if row parser is not provided, we shall set the map as row data.
                // otherwise, we shall parse the row data using the row parser...
                final Object rowData = rowParser == null
                        ? rowDataAsMap
                        : rowParser.parseRow(rowDataAsMap);

                // if row data is null, we shall skip this iteration...
                if (rowData == null) { continue; }

                // adding row data to the list...
                data.add(rowData);
            }
        } catch (Exception exception) {
            logger.error("An exception occurred while reading CSV content.", exception);

            // in case of exception, we shall return an empty list...
            return Collections.emptyList();
        }

        // finally, we shall return the data...
        return data;
    }

    public static List<Object> readFile(final String filePath, final CsvRowParser rowParser) {
        // reading the entire content of the file in memory...
        final String content = StreamUtilities.readString(filePath);

        // reading content...
        return readContent(content, rowParser);
    }

    private static Map<String, String> populateMap(final List<?> headers, final List<?> columns) {
        final int minimumLength = Math.min(headers.size(), columns.size());
        final Map<String, String> map = new HashMap<>(minimumLength * 2);

        for (int i = 0; i < minimumLength; ++i) {
            final String header = (String) headers.get(i);
            final String column = (String) columns.get(i);

            map.put(header, column);
        }

        return map;
    }
}
