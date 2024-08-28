package bd.com.momagic.cbs.dashboard.android.core.text;

import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;

import java.nio.charset.StandardCharsets;
import android.util.Base64;

public final class Encoder {

    private static final char[] UPPER_CASED_HEXADECIMAL_SYMBOLS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F',
    };

    private static final char[] LOWER_CASED_HEXADECIMAL_SYMBOLS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f',
    };

    public static String toUtf8(byte[] bytes) {
        return toUtf8(bytes, 0, bytes.length);
    }

    public static String toUtf8(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, StandardCharsets.UTF_8);
    }

    public static byte[] fromUtf8(String encodedText) {
        // if provided encoded text is null or empty,
        // we shall return an array of length zero (0)...
        if (StringUtilities.isNullOrEmpty(encodedText)) { return new byte[0]; }

        // gets byte array...
        return encodedText.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts an array of bytes to hex/base16 string.
     * @implNote This algorithm is taken from StackOverflow (answered by Crystark).
     * You may check the following URL for more details.
     * https://stackoverflow.com/questions/2817752/how-can-i-convert-a-byte-array-to-hexadecimal-in-java
     * @param bytes Array of bytes to be converted.
     * @return Lower-cased hex/base16 string.
     */
    public static String toBase16(byte[] bytes) {
        return toBase16(bytes, false);
    }

    /**
     * Converts an array of bytes to hex/base16 string.
     * @implNote This algorithm is taken from StackOverflow (answered by Crystark).
     * You may check the following URL for more details.
     * https://stackoverflow.com/questions/2817752/how-can-i-convert-a-byte-array-to-hexadecimal-in-java
     * @param bytes Array of bytes to be converted.
     * @param upperCased If set to true, the output of the hexadecimal symbols
     *                   shall be upper-cased. Otherwise, the symbols shall be
     *                   lower-cased.
     * @return Hex/Base16 string.
     */
    public static String toBase16(byte[] bytes, boolean upperCased) {
        // selecting hexadecimal symbols based on the flag...
        final char[] hexadecimalSymbols = upperCased
                ? UPPER_CASED_HEXADECIMAL_SYMBOLS
                : LOWER_CASED_HEXADECIMAL_SYMBOLS;
        // this array of characters shall hold the base-16 representation of the bytes.
        // the length of the array shall be twice the number of bytes...
        final char[] bytesAsHexadecimalSymbols = new char[bytes.length * 2];

        for (int i = 0, j = 0; i < bytes.length; ++i, j += 2) {
            // selecting byte value at index 'i'...
            final byte byteValue = bytes[i];
            final char hexadecimalSymbolA = hexadecimalSymbols[(byteValue & 0xF0) >> 4];
            final char hexadecimalSymbolB = hexadecimalSymbols[byteValue & 0x0F];

            // sets both hexadecimal symbols to the character array...
            bytesAsHexadecimalSymbols[j] = hexadecimalSymbolA;
            bytesAsHexadecimalSymbols[j + 1] = hexadecimalSymbolB;
        }

        // creates a string from the array of characters...
        final String base16Text = new String(bytesAsHexadecimalSymbols);

        // returns hex/base16 string...
        return base16Text;
    }

    /**
     * Gets the numerical value (decimal equivalent) of the hex/base-16 symbol.
     * e.g. '1' = 1, 'A'/'a' = 10, 'F'/'f' = 15.
     * @implNote To make it performant, some checks were not made.
     * So, this method is not very secure due to the lack of some
     * bound checks. Non-hex symbols shall return erroneous values.
     * @param base16Symbol Hex/Base-16 symbol of which the numeric value
     *                     shall be retrieved. e.g. '1', 'A', 'f'.
     * @return The numerical value of the hex/base-16 symbol.
     */
    private static int getValueOfBase16Symbol(char base16Symbol) {
        // ascii value of the base-16 symbol...
        int asciiValue = base16Symbol;

        // if the symbol is less than or equal to '9' (as character),
        // we shall subtract the ascii value of '0' to get the numeric value
        // of the symbol...
        if (asciiValue < 58) { return asciiValue - 48; }        // <-- 48 is the ascii value of '0' and 57 is the ascii value of '9'...
        // if the symbol is less than or equal to 'F', we shall subtract
        // the ascii value of 'A' to get the numeric value of the symbol...
        // NOTE: WE ARE RETURNING (asciiValue - 65 + 10) = (asciiValue - 55)
        // BECAUSE, 65 IS THE ASCII VALUE OF 'A' AND 'A' IN HEXADECIMAL IS
        // EQUIVALENT TO 10 IN DECIMAL NUMBER SYSTEM...
        if (asciiValue < 71) { return asciiValue - 55; }        // <-- 70 is the ascii value of 'F'...

        // otherwise, we shall subtract the ascii value of 'A'
        // to get the numeric value of the symbol...
        // NOTE: WE ARE RETURNING (asciiValue - 97 + 10) = (asciiValue - 87)
        // BECAUSE, 97 IS THE ASCII VALUE OF 'a' AND 'a' IN HEXADECIMAL IS
        // EQUIVALENT TO 10 IN DECIMAL NUMBER SYSTEM...
        return asciiValue - 87;                                 // <-- 97 is the ascii value of 'a'...
    }

    /**
     * Decodes the encoded text.
     * @param encodedText Encoded text to be decoded.
     * @return Returns the decoded content.
     */
    public static byte[] fromBase16(String encodedText) {
        // converts the encoded text into an array of characters...
        final char[] encodedTextAsCharacters = encodedText.toCharArray();
        // gets the length of the encoded text...
        final int encodedTextLength = encodedTextAsCharacters.length;
        // the length of the byte array shall be half the length
        // of the hex/base16 encoded text...
        final int byteArrayLength = encodedTextLength / 2;
        // initializing a byte array...
        final byte[] bytes = new byte[byteArrayLength];

        for (int i = 0, j = 0; i < encodedTextLength; i += 2, ++j) {
            final char base16SymbolA = encodedTextAsCharacters[i];
            final char base16SymbolB = encodedTextAsCharacters[i + 1];
            final int numericValueA = getValueOfBase16Symbol(base16SymbolA);
            final int numericValueB = getValueOfBase16Symbol(base16SymbolB);
            final byte byteValue = (byte)((numericValueA << 4) + numericValueB);

            bytes[j] =  byteValue;
        }

        // returns the decoded bytes...
        return bytes;
    }

    /**
     * Converts an array of bytes to base64 string.
     * @param bytes Array of bytes to be converted.
     * @param urlSafe If set to true, the output of the base64 encoding shall
     *                be URL safe.
     * @param paddingEnabled If set to true, the output of the base64 encoding
     *                   shall contain padding symbols. Otherwise, the symbols
     *                   shall be omitted.
     * @return Base64 string.
     */
    public static String toBase64(byte[] bytes, boolean urlSafe, boolean paddingEnabled) {
        int flags = Base64.DEFAULT;

        if (urlSafe) { flags |= Base64.URL_SAFE; }
        if (paddingEnabled) { flags |= Base64.NO_PADDING; }

        // returns the base64 text...
        return Base64.encodeToString(bytes, flags);
    }

    /**
     * Converts an array of bytes to regular (not URL-safe) base64 string.
     * @param bytes Array of bytes to be converted.
     * @param paddingEnabled If set to true, the output of the base64 encoding
     *                   shall contain padding symbols. Otherwise, the symbols
     *                   shall be omitted.
     * @return Regular (not URL-safe) base64 string.
     */
    public static String toBase64(byte[] bytes, boolean paddingEnabled) {
        return toBase64(bytes, false, paddingEnabled);
    }

    /**
     * Converts an array of bytes to regular (not URL-safe) base64 string
     * with padding enabled.
     * @param bytes Array of bytes to be converted.
     * @return Regular (not URL-safe) base64 string.
     */
    public static String toBase64(byte[] bytes) {
        return toBase64(bytes, true);
    }

    /**
     * Converts an array of bytes to URL-safe base64 string.
     * @param bytes Array of bytes to be converted.
     * @param paddingEnabled If set to true, the output of the base64 encoding
     *                   shall contain padding symbols. Otherwise, the symbols
     *                   shall be omitted.
     * @return URL-safe base64 string.
     */
    public static String toUrlSafeBase64(byte[] bytes, boolean paddingEnabled) {
        return toBase64(bytes, true, paddingEnabled);
    }

    /**
     * Converts an array of bytes to URL-safe base64 string
     * with padding enabled.
     * @param bytes Array of bytes to be converted.
     * @return URL-safe base64 string.
     */
    public static String toUrlSafeBase64(byte[] bytes) {
        return toUrlSafeBase64(bytes, true);
    }

    /**
     * Decodes the base64 encoded text.
     * @param encodedText Base64 encoded text to be decoded.
     * @param urlSafe If set to true, the encoded base64 text
     *                will be considered as URL safe.
     * @return Returns the decoded content.
     */
    public static byte[] fromBase64(String encodedText, boolean urlSafe) {
        return Base64.decode(encodedText, urlSafe ? Base64.URL_SAFE : Base64.DEFAULT);
    }

    /**
     * Decodes the regular (not URL-safe) base64 encoded text.
     * @param encodedText Regular (not URL-safe) base64 encoded text to be decoded.
     * @return Returns the decoded content.
     */
    public static byte[] fromBase64(String encodedText) {
        return fromBase64(encodedText, false);
    }

    /**
     * Decodes the URL-safe base64 encoded text.
     * @param encodedText URL-safe base64 encoded text to be decoded.
     * @return Returns the decoded content.
     */
    public static byte[] fromUrlSafeBase64(String encodedText) {
        return fromBase64(encodedText, true);
    }

    /**
     * Encodes the given bytes into the specified encoding.
     * @implNote Uses the default options for the specified
     * encoding mechanism.
     * @param bytes Array of bytes to be encoded.
     * @param encoding Encoding to be used.
     * @return The encoded text.
     */
    public static String encode(byte[] bytes, Encoding encoding) {
        // encodes the bytes using the encoding provided...
        switch (encoding)
        {
            case UTF_8:
                return toUtf8(bytes);
            case HEX:
            case BASE_16:
                return toBase16(bytes);
            case BASE_64:
                return toBase64(bytes);
            case URL_SAFE_BASE_64:
                return toUrlSafeBase64(bytes);
            default:
                return StringUtilities.getEmptyString();
        }
    }

    /**
     * Decodes the encoded text into bytes using the specified encoding.
     * @param encodedText Text to be decoded.
     * @param encoding Encoding that was used to encode the text.
     * @return Returns the decoded content.
     */
    public static byte[] decode(String encodedText, Encoding encoding) {
        // decodes the encoded text using the encoding provided...
        switch (encoding)
        {
            case UTF_8:
                return fromUtf8(encodedText);
            case HEX:
            case BASE_16:
                return fromBase16(encodedText);
            case BASE_64:
                return fromBase64(encodedText);
            case URL_SAFE_BASE_64:
                return fromUrlSafeBase64(encodedText);
            default:
                return new byte[0];
        }
    }
}
