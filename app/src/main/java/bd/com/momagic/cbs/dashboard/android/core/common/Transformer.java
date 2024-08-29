package bd.com.momagic.cbs.dashboard.android.core.common;

import bd.com.momagic.cbs.dashboard.android.core.text.Encoder;

public interface Transformer<Type> {

    byte[] transform(final Type data);

    static Transformer<String> createStringTransformer() {
        return Encoder::fromUtf8;
    }
}
