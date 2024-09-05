package bd.com.momagic.cbs.dashboard.android.ui.customcardview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import bd.com.momagic.cbs.dashboard.android.R;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.UiUtilities;
import lombok.Getter;

public class CustomCardView extends LinearLayout {

    @Getter
    private int customCardViewBackgroundColor;
    @Getter
    private int customCardViewTextColor;
    @Getter
    private String customCardViewTopText;
    @Getter
    private String customCardViewCenterText;
    @Getter
    private String customCardViewBottomText;

    private final Activity activity;
    private final TextView textViewTop;
    private final TextView textViewCenter;
    private final TextView textViewBottom;
    private final CardView internalCardView;

    // private final ValueAnimator textViewCenterValueAnimator = ValueAnimator.ofInt(0, 0);

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
        internalCardView = findViewById(R.id.internal_card_view);

        /*textViewCenterValueAnimator.setDuration(3_000L);
        textViewCenterValueAnimator.setRepeatCount(0);
        textViewCenterValueAnimator.addUpdateListener(animation -> {
            textViewCenter.setText(String.valueOf(animation.getAnimatedValue()));
        });*/

        final int defaultBackgroundColor = getResources().getColor(R.color.white);
        final int defaultTextColor = getResources().getColor(R.color.black);
        int backgroundColor = defaultBackgroundColor;
        int textColor = defaultTextColor;
        String topText = StringUtilities.getEmptyString();
        String centerText = StringUtilities.getEmptyString();
        String bottomText = StringUtilities.getEmptyString();

        if (attrs != null && !isInEditMode()) {
            try (final TypedArray styledAttributes = getContext()
                    .getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.CustomCardView, 0, 0)) {
                backgroundColor = styledAttributes.getColor(
                        R.styleable.CustomCardView_customCardViewBackgroundColor,
                        defaultBackgroundColor);
                textColor = styledAttributes.getColor(
                        R.styleable.CustomCardView_customCardViewTextColor,
                        defaultTextColor);
                topText = styledAttributes.getString(R.styleable.CustomCardView_customCardViewTopText);
                centerText = styledAttributes.getString(R.styleable.CustomCardView_customCardViewCenterText);
                bottomText = styledAttributes.getString(R.styleable.CustomCardView_customCardViewBottomText);
            }
        }

        setCustomCardViewBackgroundColor(backgroundColor);
        setCustomCardViewTextColor(textColor);
        setCustomCardViewTopText(topText);
        setCustomCardViewCenterText(centerText);
        setCustomCardViewBottomText(bottomText);
    }

    public void setCustomCardViewBackgroundColor(@ColorInt final int customCardViewBackgroundColor) {
        this.customCardViewBackgroundColor = customCardViewBackgroundColor;

        activity.runOnUiThread(()
                -> internalCardView.setCardBackgroundColor(this.customCardViewBackgroundColor));
    }

    public void setCustomCardViewTextColor(@ColorInt int customCardViewTextColor) {
        this.customCardViewTextColor = customCardViewTextColor;

        activity.runOnUiThread(() -> {
            textViewTop.setTextColor(this.customCardViewTextColor);
            textViewCenter.setTextColor(this.customCardViewTextColor);
            textViewBottom.setTextColor(this.customCardViewTextColor);
        });
    }

    public void setCustomCardViewTopText(final String text) {
        customCardViewTopText = text;

        activity.runOnUiThread(() -> textViewTop.setText(customCardViewTopText));
    }

    public void setCustomCardViewCenterText(final String text) {
        customCardViewCenterText = text;

        activity.runOnUiThread(() -> textViewCenter.setText(customCardViewCenterText));

        // final int currentValue = NumberUtilities.tryParseInteger(textViewCenter.getText().toString(), 0);
        // final int newValue = NumberUtilities.tryParseInteger(text, 0);

        /*activity.runOnUiThread(() -> {
            textViewCenterValueAnimator.setIntValues(currentValue, newValue);
            textViewCenterValueAnimator.start();
        });*/
    }

    public void setCustomCardViewBottomText(final String text) {
        customCardViewBottomText = text;

        activity.runOnUiThread(() -> textViewBottom.setText(customCardViewBottomText));
    }
}
