package bd.com.momagic.cbs.dashboard.android.ui.customcardview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import bd.com.momagic.cbs.dashboard.android.R;
import bd.com.momagic.cbs.dashboard.android.core.utilities.MiscellaneousUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.NumberUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.UiUtilities;

public class CustomCardView extends LinearLayout {

    private final Activity activity;
    private final TextView textViewTop;
    private final TextView textViewCenter;
    private final TextView textViewBottom;

    private final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 0);

    public CustomCardView(Context context) {
        this(context, null);
    }

    public CustomCardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_card_view, this, true);

        activity = UiUtilities.getActivity(getContext());
        textViewTop = findViewById(R.id.textViewTop);
        textViewCenter = findViewById(R.id.textViewCenter);
        textViewBottom = findViewById(R.id.textViewBottom);

        valueAnimator.setDuration(3_000L);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(animation -> {
            System.out.println("++++++++++++++ INSIDE ANIMATION....");

            textViewCenter.setText(String.valueOf(animation.getAnimatedValue()));
            textViewCenter.invalidate();
            invalidate();
        });
    }

    public void setTextTop(final String text) {
        textViewTop.setText(text);
    }

    public void setTextCenter(final String text) {
        final int currentValue = NumberUtilities.tryParseInteger(textViewCenter.getText().toString(), 0);
        final int newValue = NumberUtilities.tryParseInteger(text, 0);

        activity.runOnUiThread(() -> {
            valueAnimator.setIntValues(currentValue, newValue);
            valueAnimator.start();
        });
        // textViewCenter.setText(text);
    }

    public void setTextBottom(final String text) {
        textViewBottom.setText(text);
    }
}
