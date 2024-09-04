package bd.com.momagic.cbs.dashboard.android.core.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

public final class UiUtilities {

    public static Activity getActivity(final Context context) {
        if (!(context instanceof ContextWrapper)) { return null; }
        if (context instanceof Activity) { return (Activity) context; }

        final ContextWrapper contextWrapper = (ContextWrapper) context;
        final Context baseContext = contextWrapper.getBaseContext();

        return getActivity(baseContext);
    }
}
