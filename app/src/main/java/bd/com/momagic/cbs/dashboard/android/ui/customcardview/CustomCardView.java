package bd.com.momagic.cbs.dashboard.android.ui.customcardview;

import android.animation.ValueAnimator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import bd.com.momagic.cbs.dashboard.android.R;
import bd.com.momagic.cbs.dashboard.android.core.concurrency.ThreadSafeExecutor;
import bd.com.momagic.cbs.dashboard.android.core.concurrency.ThreadSafeLong;
import bd.com.momagic.cbs.dashboard.android.core.threading.BackgroundTaskExecutor;
import bd.com.momagic.cbs.dashboard.android.core.utilities.NumberUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.StringUtilities;
import bd.com.momagic.cbs.dashboard.android.core.utilities.UiUtilities;
import lombok.Getter;

public class CustomCardView extends LinearLayout implements Runnable {

    private volatile boolean resetAlert = false;
    @Getter
    private int customCardViewBackgroundColor;
    @Getter
    private int customCardViewWarningBackgroundColor;
    @Getter
    private int customCardViewDangerBackgroundColor;
    @Getter
    private int customCardViewTextColor;
    @Getter
    private String customCardViewTopText;
    @Getter
    private String customCardViewCenterTextPrefix;
    @Getter
    private String customCardViewCenterTextSuffix;
    private final ThreadSafeLong customCardViewCenterTextCurrentNumericValue = new ThreadSafeLong(0L);
    private final ThreadSafeLong customCardViewCenterTextNumericValue = new ThreadSafeLong(0L);
    private static final BackgroundTaskExecutor backgroundTaskExecutor = BackgroundTaskExecutor.createInstance(true);

    @Getter
    private String customCardViewCenterText;
    @Getter
    private String customCardViewBottomText;

    private final Activity activity;
    private final TextView textViewTop;
    private final TextView textViewCenter;
    private final TextView textViewBottom;
    private final CardView internalCardView;

    private final ValueAnimator textViewCenterValueAnimator = ValueAnimator.ofInt(0, 0);
    private final ValueAnimator internalCardViewBackgroundColorAnimator = ValueAnimator.ofArgb(0, 0);

    private static final Lock customCardViewListLock = new ReentrantLock(false);
    private static final List<CustomCardView> customCardViewList = new ArrayList<>();

    /*private final TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] {
            new ColorDrawable(Color.BLUE), new ColorDrawable(Color.RED)
    });*/

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

        customCardViewList.add(this);

        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_card_view, this, true);

        activity = UiUtilities.getActivity(getContext());
        textViewTop = findViewById(R.id.textViewTop);
        textViewCenter = findViewById(R.id.textViewCenter);
        textViewBottom = findViewById(R.id.textViewBottom);
        internalCardView = findViewById(R.id.internal_card_view);

        final int defaultBackgroundColor = getResources().getColor(R.color.white);
        final int defaultTextColor = getResources().getColor(R.color.black);
        int backgroundColor = defaultBackgroundColor;
        int warningBackgroundColor = defaultBackgroundColor;
        int dangerBackgroundColor = defaultBackgroundColor;
        int textColor = defaultTextColor;
        String topText = StringUtilities.getEmptyString();
        long centerTextNumericValue = 0L;
        String centerTextPrefix = StringUtilities.getEmptyString();
        String centerTextSuffix = StringUtilities.getEmptyString();
        String centerText = StringUtilities.getEmptyString();
        String bottomText = StringUtilities.getEmptyString();

        if (attrs != null && !isInEditMode()) {
            try (final TypedArray styledAttributes = getContext()
                    .getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.CustomCardView, 0, 0)) {
                backgroundColor = styledAttributes.getColor(
                        R.styleable.CustomCardView_customCardViewBackgroundColor,
                        defaultBackgroundColor);
                warningBackgroundColor = styledAttributes.getColor(
                        R.styleable.CustomCardView_customCardViewWarningBackgroundColor,
                        defaultBackgroundColor);
                dangerBackgroundColor = styledAttributes.getColor(
                        R.styleable.CustomCardView_customCardViewDangerBackgroundColor,
                        defaultBackgroundColor);
                textColor = styledAttributes.getColor(
                        R.styleable.CustomCardView_customCardViewTextColor,
                        defaultTextColor);
                topText = StringUtilities.getDefaultIfNullOrEmpty(styledAttributes.getString(
                        R.styleable.CustomCardView_customCardViewTopText),
                        StringUtilities.getEmptyString(), false);
                centerTextNumericValue = styledAttributes.getInteger(
                        R.styleable.CustomCardView_customCardViewCenterTextNumericValue, 0);
                centerTextPrefix = StringUtilities.getDefaultIfNullOrEmpty(styledAttributes.getString(
                        R.styleable.CustomCardView_customCardViewCenterTextPrefix),
                        StringUtilities.getEmptyString(), false);
                centerTextSuffix = StringUtilities.getDefaultIfNullOrEmpty(styledAttributes.getString(
                        R.styleable.CustomCardView_customCardViewCenterTextSuffix),
                        StringUtilities.getEmptyString(), false);
                centerText = StringUtilities.getDefaultIfNullOrEmpty(styledAttributes.getString(
                        R.styleable.CustomCardView_customCardViewCenterText),
                        StringUtilities.getEmptyString(), false);
                bottomText = StringUtilities.getDefaultIfNullOrEmpty(styledAttributes.getString(
                        R.styleable.CustomCardView_customCardViewBottomText),
                        StringUtilities.getEmptyString(), false);
            }
        }

        setCustomCardViewBackgroundColor(backgroundColor);
        setCustomCardViewWarningBackgroundColor(warningBackgroundColor);
        setCustomCardViewDangerBackgroundColor(dangerBackgroundColor);
        setCustomCardViewTextColor(textColor);
        setCustomCardViewTopText(topText);
        setCustomCardViewCenterTextNumericValue(centerTextNumericValue);
        setCustomCardViewCenterTextPrefix(centerTextPrefix);
        setCustomCardViewCenterTextSuffix(centerTextSuffix);
        setCustomCardViewCenterText(centerText);
        setCustomCardViewBottomText(bottomText);

        textViewCenterValueAnimator.setDuration(1_000L);
        textViewCenterValueAnimator.setRepeatCount(0);
        textViewCenterValueAnimator.addUpdateListener(animation -> {
            final int animatedValue = (int) animation.getAnimatedValue();
            final String formattedValue = NumberUtilities.format(animatedValue);

            setCustomCardViewCenterText(getCustomCardViewCenterTextPrefix()
                    + formattedValue
                    + getCustomCardViewCenterTextSuffix());
        });

        internalCardViewBackgroundColorAnimator.setDuration(850L);
        internalCardViewBackgroundColorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        internalCardViewBackgroundColorAnimator.addUpdateListener(valueAnimator -> {
            final int animatedColor = (int) valueAnimator.getAnimatedValue();

            // if reset alert request is received...
            if (resetAlert && animatedColor == customCardViewBackgroundColor) {
                // we'll reset the flag...
                resetAlert = false;

                // and stop the animation...
                valueAnimator.end();
            }

            // setting the background color...
            internalCardView.setCardBackgroundColor(animatedColor);
        });

        // this shall add the task to the queue...
        backgroundTaskExecutor.execute(this);
        // trans.startTransition(5000);
    }

    public long getCustomCardViewCenterTextNumericValue() {
        return customCardViewCenterTextNumericValue.get();
    }

    private void addToList(final CustomCardView cardView) {
        ThreadSafeExecutor.execute(customCardViewListLock,
                () -> customCardViewList.add(cardView));
    }

    private List<CustomCardView> copyList() {
        return ThreadSafeExecutor.execute(customCardViewListLock,
                () -> new ArrayList<>(customCardViewList));
    }

    public void setCustomCardViewBackgroundColor(@ColorInt final int color) {
        customCardViewBackgroundColor = color;

        activity.runOnUiThread(()
                -> internalCardView.setCardBackgroundColor(customCardViewBackgroundColor));
    }

    public void setCustomCardViewWarningBackgroundColor(@ColorInt final int color) {
        customCardViewWarningBackgroundColor = color;
    }

    public void setCustomCardViewDangerBackgroundColor(@ColorInt final int color) {
        customCardViewDangerBackgroundColor = color;
    }

    public void showWarningAlert() {
        activity.runOnUiThread(() -> {
            internalCardViewBackgroundColorAnimator.end();
            internalCardViewBackgroundColorAnimator.setIntValues(
                    customCardViewBackgroundColor,
                    customCardViewWarningBackgroundColor);
            internalCardViewBackgroundColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
            internalCardViewBackgroundColorAnimator.start();
        });
    }

    public void showDangerAlert() {
        activity.runOnUiThread(() -> {
            internalCardViewBackgroundColorAnimator.end();
            internalCardViewBackgroundColorAnimator.setIntValues(
                    customCardViewBackgroundColor,
                    customCardViewDangerBackgroundColor);
            internalCardViewBackgroundColorAnimator.setRepeatCount(ValueAnimator.INFINITE);
            internalCardViewBackgroundColorAnimator.start();
        });
    }

    public void resetAlert() {
        resetAlert = true;
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

    public void setCustomCardViewCenterTextNumericValue(final long numericValue) {
        customCardViewCenterTextNumericValue.set(numericValue);
    }

    public void setCustomCardViewCenterTextPrefix(final String text) {
        customCardViewCenterTextPrefix = text;

        setCustomCardViewCenterText(getCustomCardViewCenterTextPrefix()
                + getCustomCardViewCenterTextNumericValue()
                + getCustomCardViewCenterTextSuffix());
    }

    public void setCustomCardViewCenterTextSuffix(final String text) {
        customCardViewCenterTextSuffix = text;

        setCustomCardViewCenterText(getCustomCardViewCenterTextPrefix()
                + getCustomCardViewCenterTextNumericValue()
                + getCustomCardViewCenterTextSuffix());
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

    @Override
    public void run() {
        final List<CustomCardView> customCardViewList = copyList();

        for (final CustomCardView cardView : customCardViewList) {
            final int currentValue = (int) cardView.customCardViewCenterTextCurrentNumericValue.get();
            final int newValue = (int) cardView.getCustomCardViewCenterTextNumericValue();
            cardView.customCardViewCenterTextCurrentNumericValue.set(newValue);

            activity.runOnUiThread(() -> {
                cardView.textViewCenterValueAnimator.end();
                cardView.textViewCenterValueAnimator.setIntValues(currentValue, newValue);
                cardView.textViewCenterValueAnimator.start();
            });
        }
    }

    /*public static void startBackgroundThread() {
        if (backgroundThread != null || customCardViewList.isEmpty()) { return; }

        final CustomCardView cardView = customCardViewList.get(0);
        backgroundThread = new Thread(cardView);
        backgroundThread.setPriority(Thread.MIN_PRIORITY);
        backgroundThread.setDaemon(false);
        backgroundThread.start();
    }*/
}
