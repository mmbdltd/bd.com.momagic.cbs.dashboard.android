package bd.com.momagic.cbs.dashboard.android.core.text.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bd.com.momagic.cbs.dashboard.android.core.configurations.Configuration;
import bd.com.momagic.cbs.dashboard.android.core.configurations.ConfigurationProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.ServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.dependencyinjection.SingletonServiceProvider;
import bd.com.momagic.cbs.dashboard.android.core.utilities.CloseableUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.DateTimeFormatter;
import bd.com.momagic.cbs.dashboard.android.core.utilities.FileSystemUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.WriterUtilities;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class CsvWriterImpl implements CsvWriter {

    private final Logger logger = LoggerFactory.getLogger(CsvWriterImpl.class);
    private String currentPrintWriterCreationDateAsString = StringUtilities.getEmptyString();
    private final CsvWriterConfiguration configuration;
    private final Lock lock = new ReentrantLock(false);
    private PrintWriter printWriter;

    private static final String DATE_FORMAT_PATTERN = "dd-MMM-yyyy";

    private CsvWriterImpl(CsvWriterConfiguration configuration) {
        this.configuration = configuration;
    }

    private PrintWriter getPrintWriter() {
        if (!configuration.isEnabled()) { return null; }

        // getting the current time...
        final Date currentTime = Calendar.getInstance().getTime();
        // creating a new instance of date format...
        // NOTE: NEED TO CREATE NEW DATE FORMAT BECAUSE SimpleDateFormat CLASS IS NOT THREAD-SAFE...
        final DateFormat dateFormat = DateTimeFormatter.createDateFormat(DATE_FORMAT_PATTERN);
        // formatting the time to extract the current date...
        final String currentDateAsString = dateFormat.format(currentTime);

        // if the date has changed or the print writer is 'null'...
        if (!currentDateAsString.equals(currentPrintWriterCreationDateAsString) || printWriter == null) {
            // we shall try to close the previous print writer...
            // NOTE: THIS METHOD INTERNALLY HANDLES 'NULL' PRINT WRITER...
            close(printWriter);

            // then we shall prepare the file path...
            final String filePath = toFilePath(configuration.getFilePathFormat(), currentDateAsString);
            // then we shall check if the file already exists...
            final boolean fileExists = FileSystemUtilities.exists(filePath);

            // then we shall create a new print writer...
            printWriter = WriterUtilities.tryCreatePrintWriter(filePath, true,
                    configuration.shallFlushAutomatically(), configuration.getBufferLength());
            // assigning the current date as the print writer creation date...
            currentPrintWriterCreationDateAsString = currentDateAsString;

            // if file did not exist before and the print writer
            // has just been created...
            if (!fileExists) {
                // we shall write the headers...
                write(configuration.shallEscapeValues(), printWriter, (Object[]) configuration.getHeaders());
            }
        }

        // lastly, we shall return the print writer...
        return printWriter;
    }

    private PrintWriter getPrintWriterSynchronized() {
        PrintWriter printWriter;

        lock.lock();        // <-- acquiring the lock...

        // we shall retrieve the print writer...
        printWriter = getPrintWriter();

        lock.unlock();      // <-- releasing the lock...

        return printWriter;
    }

    @Override
    public boolean write(String csv) {
        // first we shall retrieve the print writer...
        final PrintWriter printWriter = getPrintWriterSynchronized();

        // writing the value to print writer...
        return write(printWriter, csv);
    }

    @Override
    public boolean write(Object... values) {
        // first we shall retrieve the print writer...
        final PrintWriter printWriter = getPrintWriterSynchronized();

        // writing the values to print writer...
        return write(configuration.shallEscapeValues(), printWriter, values);
    }

    @Override
    public void flush() {
        // first we shall retrieve the print writer...
        final PrintWriter printWriter = getPrintWriterSynchronized();

        // if print writer is null, we shall not proceed any further...
        if (printWriter == null) { return; }

        // we shall flush the buffered content to file...
        printWriter.flush();
    }

    @Override
    public void close() {
        // first we shall retrieve the print writer...
        final PrintWriter printWriter = getPrintWriterSynchronized();

        // calling the static close() method...
        close(printWriter);
    }

    private static String toFilePath(String filePathFormat, String formattedDate) {
        // getting the configuration...
        final Configuration configuration = ConfigurationProvider.getConfiguration();
        // we'll retrieve the instance ID...
        String instanceId = configuration.getInstanceId();
        // we'll also get the application name...
        final String applicationName = configuration.getApplicationName();

        // if application name is empty and the instance ID is not empty...
        if (StringUtilities.isEmpty(applicationName) && !StringUtilities.isEmpty(instanceId)) {
            // we shall place a directory path separator before the instance ID...
            instanceId = '/' + instanceId;
        }

        // replacing all the placeholders...
        String filePath = filePathFormat
                .replace("{{applicationDataDirectory}}", configuration.getCacheDirectory())
                .replace("{{profile}}", configuration.getProfile())
                .replace("{{applicationName}}", applicationName)
                .replace("{{instanceId}}", instanceId)
                .replace("{{date}}", formattedDate);
        // sanitizing the path because it may contain "//"....
        filePath = FileSystemUtilities.sanitizePath(filePath);

        // finally, we shall return the file path...
        return filePath;
    }

    private static boolean write(PrintWriter printWriter, String csv) {
        // if the print writer is 'null', we shall return false...
        if (printWriter == null) { return false; }

        // and print the value to the file...
        printWriter.println(csv);
        // we shall flush the buffered content to file...
        // printWriter.flush();

        // finally, we shall return true...
        return true;
    }

    private static boolean write(boolean escape, PrintWriter printWriter, Object... values) {
        // then we shall prepare the comma separated value...
        final String commaSeparatedValue = StringUtilities.toCommaSeparatedValue(escape, values);

        // writing the value to print writer...
        return write(printWriter, commaSeparatedValue);
    }

    private static void close(PrintWriter printWriter) {
        // if print writer is null, we shall not proceed any further...
        if (printWriter == null) { return; }

        // we shall flush the buffered content to file...
        printWriter.flush();
        // then we shall try to close the print writer...
        // NOTE: THIS METHOD INTERNALLY HANDLES 'NULL' PRINT WRITER...
        CloseableUtilities.tryClose(printWriter);
    }

    static CsvWriter getInstance(String key, CsvWriterConfiguration configuration) {
        // creating an internal key for the instance...
        final String instanceKey = key + "@" + CsvWriter.class.getName();
        // getting the service provider...
        final ServiceProvider serviceProvider = SingletonServiceProvider.getInstance();
        // retrieving/creating an instance of CSV writer...
        final CsvWriter csvWriter = serviceProvider.get(
                instanceKey, CsvWriter.class,
                // if configuration is null, instance shall not be created...
                () -> configuration == null ? null : new CsvWriterImpl(configuration));

        // returns the instance...
        return csvWriter;
    }
}
