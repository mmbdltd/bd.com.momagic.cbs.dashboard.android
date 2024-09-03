package bd.com.momagic.cbs.dashboard.android.core.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bd.com.momagic.cbs.dashboard.android.core.text.Encoder;

import java.io.*;
import java.net.SocketTimeoutException;

public final class StreamUtilities {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtilities.class);
    private static final int BUFFER_LENGTH = 8192;
    private static final int STRING_BUILDER_INITIAL_CAPACITY = 8192;
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Reads bytes into a portion of an array.
     * This method will block until some input is available,
     * an I/O error occurs, or the end of the stream is reached.
     * @param buffer Destination buffer.
     * @param offset Offset at which to start storing bytes.
     * @param length Maximum number of bytes to read.
     * @param inputStream Input stream from which to start reading.
     * @return The number of bytes read. Returns -1
     * if end of stream is reached. Returns -2 in case of exception.
     */
    public static int read(final byte[] buffer, final int offset, final int length, final InputStream inputStream) {
        return read(buffer, offset, length, inputStream, true);
    }

    /**
     * Reads bytes into a portion of an array.
     * This method will block until some input is available,
     * an I/O error occurs, or the end of the stream is reached.
     * @param buffer Destination buffer.
     * @param offset Offset at which to start storing bytes.
     * @param length Maximum number of bytes to read.
     * @param inputStream Input stream from which to start reading.
     * @param logsEnabled Flag to determine if logs are enabled.
     * @return The number of bytes read. Returns -1
     * if end of stream is reached. Returns -2 in case of exception.
     */
    public static int read(final byte[] buffer, final int offset, final int length,
                           final InputStream inputStream, final boolean logsEnabled) {
        try {
            return inputStream.read(buffer, offset, length);
        } catch (final SocketTimeoutException exception) {
            if (logsEnabled) {
                logger.error("Timeout exception occurred while reading from the input stream.", exception);
            }

            return -3;
        } catch (final Exception exception) {
            if (logsEnabled) {
                logger.error("An exception occurred while reading from the input stream.", exception);
            }

            return -2;
        }
    }

    /**
     * Reads data from the input stream as an array of bytes.
     * @param inputStream Input stream to read from.
     * @param closeAutomatically Setting this flag to true shall close
     *                           the input stream after reading or exception.
     * @param logsEnabled This flag is used to determine if logs shall be enabled.
     * @return The bytes read from the input stream.
     */
    public static byte[] readBytes(
            final InputStream inputStream,
            final boolean closeAutomatically,
            final boolean logsEnabled) {
        // if the input stream is null...
        if (inputStream == null) {
            logger.warn("Provided input stream is 'null'.");

            // we shall return an empty byte array...
            return EMPTY_BYTE_ARRAY;
        }

        final byte[] buffer = new byte[BUFFER_LENGTH];
        byte[] content;
        int bytesRead = 0;

        try (final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_LENGTH)) {
            // NOTE: IF BYTES READ IS EQUAL TO -2, IT MEANS EXCEPTION HAS OCCURRED.
            // THUS, THIS LOOP SHALL BE BROKEN...
            while ((bytesRead = read(buffer, 0, buffer.length, inputStream)) > 0) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // retrieving the entire content as byte array...
            content = byteArrayOutputStream.toByteArray();
        } catch (final Exception exception) {
            if (logsEnabled) {
                logger.error("An exception occurred while reading from the input stream.", exception);
            }

            content = EMPTY_BYTE_ARRAY;
        }

        // if 'closeAutomatically' flag is true,
        // we shall try to close the input stream...
        if (closeAutomatically) { CloseableUtilities.tryClose(inputStream); }
        // returns empty string in case of exception...
        if (bytesRead < -1) { return EMPTY_BYTE_ARRAY; }

        return content;
    }

    /**
     * Reads data from the input stream as an array of bytes.
     * @param inputStream Input stream to read from.
     * @param closeAutomatically Setting this flag to true shall close
     *                           the input stream after reading or exception.
     * @return The bytes read from the input stream.
     */
    public static byte[] readBytes(
            final InputStream inputStream,
            final boolean closeAutomatically) {
        return readBytes(inputStream, closeAutomatically, true);
    }

    /**
     * Reads data from the input stream as an array of bytes.
     * @param inputStream Input stream to read from.
     * @implNote The input stream is closed after reading completes
     * or exception occurs.
     * @return The bytes read from the input stream.
     */
    public static byte[] readBytes(final InputStream inputStream) {
        return readBytes(inputStream, true);
    }

    /**
     * Reads data from the input stream as string.
     * @implNote The input stream is closed after reading completes
     * or exception occurs.
     * @param inputStream Input stream to read from.
     * @return The string data read from the input stream.
     */
    public static String readString(final InputStream inputStream) {
        return readString(inputStream, true);
    }

    /**
     * Reads data from the input stream as string.
     * @param inputStream Input stream to read from.
     * @param closeAutomatically Setting this flag to true shall close
     *                           the input stream after reading or exception.
     * @return The string data read from the input stream.
     */
    public static String readString(
            final InputStream inputStream,
            final boolean closeAutomatically) {
        final byte[] content = readBytes(inputStream, closeAutomatically);

        // if no content is read...
        if (content.length == 0) {
            // we shall return an empty string...
            return StringUtilities.getEmptyString();
        }

        return Encoder.toUtf8(content, 0, content.length).trim();
    }

    /**
     * Reads data from the file.
     * @param filePath Path of the file.
     * @return The entire file content.
     */
    public static String readString(String filePath) {
        // trying to create input stream for the specified file path...
        final InputStream inputStream = tryCreateInputStream(filePath);

        // reading all the contents of the file...
        return readString(inputStream);
    }

    public static InputStream createInputStream(String filePath) throws Exception {
        // first, we shall retrieve the absolute file path...
        final String absoluteFilePath = FileSystemUtilities.getAbsolutePath(filePath);
        // otherwise, we shall create an instance of file...
        final File file = new File(absoluteFilePath);

        // lastly, we shall create and return the input stream...
        return new FileInputStream(file);
    }

    public static InputStream tryCreateInputStream(String filePath) {
        InputStream inputStream = null;

        try {
            // trying to create input stream...
            inputStream = createInputStream(filePath);
        } catch (Exception exception) {
            logger.error("An exception occurred while creating input stream for file path '" + filePath + "'.", exception);
        }

        // returning the input stream if creation succeeds.
        // otherwise returning null...
        return inputStream;
    }
}
