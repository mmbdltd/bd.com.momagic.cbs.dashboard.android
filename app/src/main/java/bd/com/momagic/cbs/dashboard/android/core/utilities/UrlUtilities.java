package bd.com.momagic.cbs.dashboard.android.core.utilities;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import bd.com.momagic.cbs.dashboard.android.core.external.URLDecoder;

public final class UrlUtilities {

    private static final char PATH_SEPARATOR = '/';
    private static final char PORT_SEPARATOR = ':';
    private static final String PROTOCOL_SEPARATOR = "://";

    /**
     * Extracts host and port from the URL.
     * e.g. For URL 'https://www.abc.com:8080/hello?ignoreStatus=true',
     * this method shall return 'www.abc.com:8080'.
     * @param url URL from which the host and port shall be extracted.
     * @return The host and port of the URL separated by colon.
     */
    public static String extractHostAndPort(final String url) {
        // finds the first index of the protocol separator...
        final int indexOfProtocolSeparator = url.indexOf(PROTOCOL_SEPARATOR);

        // if protocol separator is not found, we shall return the URL...
        if (indexOfProtocolSeparator == -1) { return url; }

        // removes the protocol part from the URL...
        String hostAndPort = url.substring(indexOfProtocolSeparator + PROTOCOL_SEPARATOR.length());

        // finds the first index of path separator...
        final int indexOfPathSeparator = hostAndPort.indexOf(PATH_SEPARATOR);

        // if path separator is found, it means the URL contains path...
        if (indexOfPathSeparator != -1) {
            // so, we shall remove the path from the URL to extract the host and port...
            hostAndPort = hostAndPort.substring(0, indexOfPathSeparator);
        }

        // returns the host and port...
        return hostAndPort;
    }

    /**
     * Extracts host from the URL.
     * e.g. For URL 'https://www.abc.com:8080/hello?ignoreStatus=true',
     * this method shall return 'www.abc.com'.
     * @param url URL from which the host shall be extracted.
     * @return The host part of the URL.
     */
    public static String extractHost(final String url) {
        // extracts host and port part of the url...
        final String hostAndPort = extractHostAndPort(url);
        // finds the last index of port separator...
        final int indexOfPortSeparator = hostAndPort.lastIndexOf(PORT_SEPARATOR);

        // if port separator is not found, it means the host and port
        // does not contain port. so we shall return the host...
        if (indexOfPortSeparator == -1) { return hostAndPort; }

        // but if host and port contains port, we shall remove the port
        // from the URL to extract the host...
        final String host = hostAndPort.substring(0, indexOfPortSeparator);

        // returns the host...
        return host;
    }

    /**
     * Extracts last portion of path from the given URL.
     * @param url URL from which the last portion of path shall be extracted.
     * @return The last portion of path. If not found, returns an empty string.
     */
    public static String extractLastPortionOfPath(String url) {
        // retrieving the index of last character of the given URL...
        int indexOfLastCharacter = url.length() - 1;
        // retrieving the last character of the given URL using the index...
        char lastCharacter = url.charAt(indexOfLastCharacter);

        // if the last character is a path separator; or in other words,
        // the URL ends with the path separator...
        while (lastCharacter == PATH_SEPARATOR) {
            // we shall remove the path separator from the URL...
            url = url.substring(0, indexOfLastCharacter);

            // again retrieving the index of last character of the given URL...
            indexOfLastCharacter = url.length() - 1;
            // also retrieving the last character of the given URL again using the index...
            lastCharacter = url.charAt(indexOfLastCharacter);
        }

        // then we shall find the last index of forward slash...
        final int lastIndexOfForwardSlash = url.lastIndexOf(PATH_SEPARATOR);

        // if forward slash was not found, we shall return an empty string...
        if (lastIndexOfForwardSlash == -1) { return StringUtilities.getEmptyString(); }

        // otherwise, we shall extract the last portion of path...
        String lastPortionOfPath = url.substring(lastIndexOfForwardSlash + 1);
        // and sanitize the value...
        lastPortionOfPath = StringUtilities.getDefaultIfNullOrWhiteSpace(
                lastPortionOfPath, StringUtilities.getEmptyString(), true);

        // if the value is empty, we shall return an empty string...
        if (StringUtilities.isEmpty(lastPortionOfPath)) { return StringUtilities.getEmptyString(); }

        // otherwise, we shall transform the value to upper-case...
        lastPortionOfPath = lastPortionOfPath.toUpperCase();

        // and finally we shall return the last portion of path...
        return lastPortionOfPath;
    }

    /**
     * Decodes an application/x-www-form-urlencoded string.
     * @implNote This method uses UTF-8 as the default character set (charset).
     * @param urlEncodedString URL encoded string to be decoded.
     * @return The decoded string.
     */
    public static String decodeUrl(final String urlEncodedString) {
        return decodeUrl(urlEncodedString, StandardCharsets.UTF_8);
    }

    /**
     * Decodes an application/x-www-form-urlencoded string using a specific character set.
     * @param urlEncodedString URL encoded string to be decoded.
     * @param characterSet Character set (charset) to be used (e.g. UTF-8).
     * @return The decoded string.
     */
    public static String decodeUrl(final String urlEncodedString, final Charset characterSet) {
        // decoding the URI component and
        // returning the decoded URI component...
        return URLDecoder.decode(urlEncodedString, characterSet);
    }
}
