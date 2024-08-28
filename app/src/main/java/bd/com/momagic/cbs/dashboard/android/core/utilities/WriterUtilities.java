package bd.com.momagic.cbs.dashboard.android.core.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class WriterUtilities {

    private static final Logger logger = LoggerFactory.getLogger(WriterUtilities.class);

    private static final int BUFFERED_WRITER_DEFAULT_BUFFER_LENGTH = 8 * 1024;       // <-- 8 KB...

    public static boolean writeString(
            String content,
            String filePath,
            boolean append) {
        // first we shall try to create print writer for the given file path...
        final PrintWriter printWriter = tryCreatePrintWriter(filePath, append, true);

        // if print writer creation fails, we shall return false...
        if (printWriter == null) { return false; }

        // otherwise, we shall write the content to the file...
        printWriter.println(content);

        // then we shall check if error occurred...
        final boolean errorOccurred = printWriter.checkError();

        // and try to close the print writer...
        CloseableUtilities.tryClose(printWriter);

        // finally, we shall return if the operation is successful...
        return !errorOccurred;
    }

    public static PrintWriter createPrintWriter(
            String filePath,
            boolean append,
            boolean flushAutomatically) throws Exception {
        return createPrintWriter(filePath, append, flushAutomatically, -1);
    }

    public static PrintWriter createPrintWriter(
            String filePath,
            boolean append,
            boolean flushAutomatically,
            int bufferLength) throws Exception {
        // if buffer length is less than or equal to zero (0),
        // we shall assign the default buffer length...
        if (bufferLength < 1) { bufferLength = BUFFERED_WRITER_DEFAULT_BUFFER_LENGTH; }

        // first, we shall retrieve the absolute file path...
        final String absoluteFilePath = FileSystemUtilities.getAbsolutePath(filePath);
        // then, we shall extract the absolute directory path from the absolute file path...
        final String directoryPath = FileSystemUtilities.extractDirectoryPath(absoluteFilePath);

        // then we shall create the directory if it does not exist...
        FileSystemUtilities.createDirectoryIfDoesNotExist(directoryPath);

        // then we shall create a file output stream...
        final OutputStream fileOutputStream = new FileOutputStream(absoluteFilePath, append);
        // then we shall create a file output stream writer...
        final Writer fileOutputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        // then we shall create a buffered writer...
        final Writer bufferedWriter = new BufferedWriter(fileOutputStreamWriter, bufferLength);

        // lastly, we shall create and return the print writer...
        return new PrintWriter(bufferedWriter, flushAutomatically);
    }

    public static PrintWriter tryCreatePrintWriter(
            String filePath,
            boolean append,
            boolean flushAutomatically) {
        return tryCreatePrintWriter(filePath, append, flushAutomatically, -1);
    }

    public static PrintWriter tryCreatePrintWriter(
            String filePath,
            boolean append,
            boolean flushAutomatically,
            int bufferLength) {
        PrintWriter printWriter = null;

        try {
            // trying to create print writer...
            printWriter = createPrintWriter(filePath, append, flushAutomatically, bufferLength);
        } catch (Exception exception) {
            logger.error("An exception occurred while creating print writer for file path '" + filePath + "'.", exception);
        }

        // returning the print writer if creation succeeds.
        // otherwise returning null...
        return printWriter;
    }
}
