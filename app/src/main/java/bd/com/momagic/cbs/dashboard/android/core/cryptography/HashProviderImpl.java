package bd.com.momagic.cbs.dashboard.android.core.cryptography;

import bd.com.momagic.cbs.dashboard.android.core.text.Encoder;
import bd.com.momagic.cbs.dashboard.android.core.text.Encoding;
import bd.com.momagic.cbs.dashboard.android.core.utilities.CollectionUtilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

class HashProviderImpl implements HashProvider {

    HashProviderImpl() { }

    @Override
    public byte[] computeHash(byte[] bytes, HashAlgorithm algorithm) throws Exception {
        // creates an instance of message digest...
        final MessageDigest messageDigest = createMessageDigest(algorithm);
        // updates the digest...
        messageDigest.update(bytes);

        // computes the hash and performs any final operations necessary (e.g. padding)...
        return messageDigest.digest();
    }

    @Override
    public byte[] computeHash(String message, HashAlgorithm algorithm) throws Exception {
        // converts the message into an array of bytes...
        final byte[] messageAsByteArray = message.getBytes(StandardCharsets.UTF_8);

        // computes hash of the message...
        return computeHash(messageAsByteArray, algorithm);
    }

    @Override
    public String computeHash(String message, HashAlgorithm algorithm, Encoding encoding) throws Exception {
        // computes hash of the message...
        final byte[] computedHash = computeHash(message, algorithm);

        // encodes the computed hash using the specified encoding and returns the encoded hash...
        return Encoder.encode(computedHash, encoding);
    }

    @Override
    public boolean isMatched(byte[] bytes, byte[] preComputedHashAsBytes, HashAlgorithm algorithm) throws Exception {
        // computes hash of the message...
        final byte[] computedHash = computeHash(bytes, algorithm);
        // checks if the recently computed hash is equal to the pre-computed hash...
        final boolean matched = CollectionUtilities.sequenceEqual(computedHash, preComputedHashAsBytes);

        return matched;
    }

    @Override
    public boolean isMatched(String message, byte[] preComputedHashAsBytes, HashAlgorithm algorithm) throws Exception {
        // computes hash of the message...
        final byte[] computedHash = computeHash(message, algorithm);
        // checks if the recently computed hash is equal to the pre-computed hash...
        final boolean matched = CollectionUtilities.sequenceEqual(computedHash, preComputedHashAsBytes);

        return matched;
    }

    @Override
    public boolean isMatched(String message, String preComputedHash,
                             HashAlgorithm algorithm, Encoding preComputedHashEncoding) throws Exception {
        // computes hash of the message...
        final byte[] computedHash = computeHash(message, algorithm);
        // decodes the pre-computed hash...
        final byte[] preComputedHashAsBytes = Encoder.decode(preComputedHash, preComputedHashEncoding);
        // checks if the recently computed hash is equal to the pre-computed hash...
        final boolean matched = CollectionUtilities.sequenceEqual(computedHash, preComputedHashAsBytes);

        return matched;
    }

    private static MessageDigest createMessageDigest(HashAlgorithm algorithm) throws Exception {
        // creates a new instance of message digest by algorithm name...
        final MessageDigest messageDigest = MessageDigest.getInstance(algorithm.getName());

        // returns the message digest...
        return messageDigest;
    }
}
