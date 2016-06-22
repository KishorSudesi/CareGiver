/*
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Douglas Nassif Roma Junior
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hdfc.simpleTooltip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hdfc.caregiver.R;

@SuppressWarnings("SameParameterValue")
public class SimpleTooltip implements PopupWindow.OnDismissListener {

    // Default Resources
    private static final int mDefaultPopupWindowStyleRes = android.R.attr.popupWindowStyle;
    private static final int mDefaultTextAppearanceRes = R.style.simpletooltip_default;
    private static final int mDefaultBackgroundColorRes = R.color.simpletooltip_background;
    private static final int mDefaultTextColorRes = R.color.colorWhite;
    private static final int mDefaultArrowColorRes = R.color.simpletooltip_arrow;
    private static final int mDefaultMarginRes = R.dimen.simpletooltip_margin;
    private static final int mDefaultPaddingRes = R.dimen.simpletooltip_padding;
    private static final int mDefaultArrowWidthRes = R.dimen.simpletooltip_arrow_width;
    private static final int mDefaultArrowHeightRes = R.dimen.simpletooltip_arrow_height;

    private final Context mContext;
    private OnDismissListener mOnDismissListener;
    private OnShowListener mOnShowListener;
    private PopupWindow mPopupWindow;
    private final int mGravity;
    private final boolean mDismissOnInsideTouch;
    private final boolean mDismissOnOutsideTouch;
    private final boolean mModal;
    private final View mContentView;
    private View mContentLayout;
    @IdRes
    private final int mTextViewId;
    private final CharSequence mText;
    private final View mAnchorView;
    private View mOverlay;
    private ViewGroup mRootView;
    private final boolean mShowArrow;
    private ImageView mArrowView;
    private final Drawable mArrowDrawable;
    private AnimatorSet mAnimator;
    private final float mMargin;
    private final float mPadding;
    private final float mArrowWidth;
    private final float mArrowHeight;
    private boolean dismissed = false;


    private SimpleTooltip(Builder builder) {
        mContext = builder.context;
        mGravity = builder.gravity;
        mDismissOnInsideTouch = builder.dismissOnInsideTouch;
        mDismissOnOutsideTouch = builder.dismissOnOutsideTouch;
        mModal = builder.modal;
        mContentView = builder.contentView;
        mTextViewId = builder.textViewId;
        mText = builder.text;
        mAnchorView = builder.anchorView;
        mShowArrow = builder.showArrow;
        mArrowWidth = builder.arrowWidth;
        mArrowHeight = builder.arrowHeight;
        mArrowDrawable = builder.arrowDrawable;
        mMargin = builder.margin;
        mPadding = builder.padding;
        mRootView = (ViewGroup) mAnchorView.getRootView();

        init();
    }

    private void init() {
        configPopupWindow();
        configContentView();
    }

    private void configPopupWindow() {
        mPopupWindow = new PopupWindow(mContext, null, mDefaultPopupWindowStyleRes);
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setClippingEnabled(false);
    }


    public void show() {
        verifyDismissed();

        mContentLayout.getViewTreeObserver().addOnGlobalLayoutListener(mLocationLayoutListener);
        mContentLayout.getViewTreeObserver().addOnGlobalLayoutListener(mAutoDismissLayoutListener);

        mRootView.post(new Runnable() {
            @Override
            public void run() {
                mPopupWindow.showAtLocation(mRootView, Gravity.NO_GRAVITY, mRootView.getWidth(), mRootView.getHeight());
            }
        });
    }

    private void verifyDismissed() {
        if (dismissed) {
            throw new IllegalArgumentException("Tooltip has ben dismissed.");
        }
    }

    private PointF calculePopupLocation() {
        PointF location = new PointF();

        final RectF anchorRect = SimpleTooltipUtils.calculeRectInWindow(mAnchorView);
        final PointF anchorCenter = new PointF(anchorRect.centerX(), anchorRect.centerY());

        switch (mGravity) {
            case Gravity.START:
                location.x = anchorRect.left - mPopupWindow.getContentView().getWidth() - mMargin;
                location.y = anchorCenter.y - mPopupWindow.getContentView().getHeight() / 2f;
                break;
            case Gravity.END:
                location.x = anchorRect.right + mMargin;
                location.y = anchorCenter.y - mPopupWindow.getContentView().getHeight() / 2f;
                break;
            case Gravity.TOP:
                location.x = anchorCenter.x - mPopupWindow.getContentView().getWidth() / 2f;
                location.y = anchorRect.top - mPopupWindow.getContentView().getHeight() - mMargin;
                break;
            case Gravity.BOTTOM:
                location.x = anchorCenter.x - mPopupWindow.getContentView().getWidth() / 2f;
                location.y = anchorRect.bottom;
                break;
            default:
                throw new IllegalArgumentException("Gravity must have be START, END, TOP or BOTTOM.");
        }

        return location;
    }

    private void configContentView() {
        if (mContentView instanceof TextView) {
            TextView tv = (TextView) mContentView;
            tv.setText(mText);
        } else {
            TextView tv = (TextView) mContentView.findViewById(mTextViewId);
            if (tv != null)
                tv.setText(mText);
        }

        mContentView.setPadding((int) mPadding, (int) mPadding, (int) mPadding, (int) mPadding);

        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(mGravity == Gravity.START || mGravity == Gravity.END ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);

        if (mShowArrow) {
            mArrowView = new ImageView(mContext);
            mArrowView.setImageDrawable(mArrowDrawable);
            LinearLayout.LayoutParams arrowLayoutParams;
            if (mGravity == Gravity.TOP || mGravity == Gravity.BOTTOM) {
                arrowLayoutParams = new LinearLayout.LayoutParams((int) mArrowWidth, (int) mArrowHeight, 0);
            } else {
                arrowLayoutParams = new LinearLayout.LayoutParams((int) mArrowHeight, (int) mArrowWidth, 0);
            }
            arrowLayoutParams.gravity = Gravity.CENTER;
            mArrowView.setLayoutParams(arrowLayoutParams);

            if (mGravity == Gravity.TOP || mGravity == Gravity.START) {
                linearLayout.addView(mContentView);
                linearLayout.addView(mArrowView);
            } else {
                linearLayout.addView(mArrowView);
                linearLayout.addView(mContentView);
            }
        } else {
            linearLayout.addView(mContentView);
        }

        LinearLayout.LayoutParams contentViewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        contentViewParams.gravity = Gravity.CENTER;
        mContentView.setLayoutParams(contentViewParams);

        if (mDismissOnInsideTouch || mDismissOnOutsideTouch)
            mContentView.setOnTouchListener(mPopupWindowTouchListener);

        mContentLayout = linearLayout;
        mContentLayout.setVisibility(View.INVISIBLE);
        mPopupWindow.setContentView(mContentLayout);
    }

    public void dismiss() {
        if (dismissed)
            return;

        dismissed = true;
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public boolean isShowing() {
        return mPopupWindow != null && mPopupWindow.isShowing();
    }

    public <T extends View> T findViewById(int id) {
        //noinspection unchecked
        return (T) mContentLayout.findViewById(id);
    }

    @Override
    public void onDismiss() {
        dismissed = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (mAnimator != null) {
                mAnimator.removeAllListeners();
                mAnimator.end();
                mAnimator.cancel();
                mAnimator = null;
            }
        }

        if (mRootView != null && mOverlay != null) {
            mRootView.removeView(mOverlay);
        }
        mRootView = null;
        mOverlay = null;

        if (mOnDismissListener != null)
            mOnDismissListener.onDismiss(this);
        mOnDismissListener = null;

        mPopupWindow = null;
    }

    private final View.OnTouchListener mPopupWindowTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getX() > 0 && event.getX() < v.getWidth() &&
                    event.getY() > 0 && event.getY() < v.getHeight()) {
                if (mDismissOnInsideTouch) {
                    dismiss();
                    return mModal;
                }
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return mModal;
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener mLocationLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (dismissed) {
                SimpleTooltipUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), this);
                return;
            }
            SimpleTooltipUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), this);
            mPopupWindow.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mArrowLayoutListener);
            PointF location = calculePopupLocation();
            mPopupWindow.setClippingEnabled(true);
            mPopupWindow.update((int) location.x, (int) location.y, mPopupWindow.getWidth(), mPopupWindow.getHeight());
            mPopupWindow.getContentView().requestLayout();
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener mArrowLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            SimpleTooltipUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), this);
            if (dismissed)
                return;

            mPopupWindow.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mAnimationLayoutListener);
            mPopupWindow.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(mShowLayoutListener);
            if (mShowArrow) {
                RectF achorRect = SimpleTooltipUtils.calculeRectOnScreen(mAnchorView);
                RectF contentViewRect = SimpleTooltipUtils.calculeRectOnScreen(mContentLayout);
                float x, y;
                if (mGravity == Gravity.BOTTOM || mGravity == Gravity.TOP) {
                    x = mContentLayout.getPaddingLeft() + SimpleTooltipUtils.pxFromDp(2);
                    float centerX = (contentViewRect.width() / 2f) - (mArrowView.getWidth() / 2f);
                    float newX = centerX - (contentViewRect.centerX() - achorRect.centerX());
                    if (newX > x) {
                        if (newX + mArrowView.getWidth() + x > contentViewRect.width()) {
                            x = contentViewRect.width() - mArrowView.getWidth() - x;
                        } else {
                            x = newX;
                        }
                    }
                    y = mArrowView.getTop();
                    y = y + (mGravity == Gravity.TOP ? -1 : +1);
                } else {
                    y = mContentLayout.getPaddingTop() + SimpleTooltipUtils.pxFromDp(2);
                    float centerY = (contentViewRect.height() / 2f) - (mArrowView.getHeight() / 2f);
                    float newY = centerY - (contentViewRect.centerY() - achorRect.centerY());
                    if (newY > y) {
                        if (newY + mArrowView.getHeight() + y > contentViewRect.height()) {
                            y = contentViewRect.height() - mArrowView.getHeight() - y;
                        } else {
                            y = newY;
                        }
                    }
                    x = mArrowView.getLeft();
                    x = x + (mGravity == Gravity.START ? -1 : +1);
                }
                SimpleTooltipUtils.setX(mArrowView, (int) x);
                SimpleTooltipUtils.setY(mArrowView, (int) y);
            }
            mPopupWindow.getContentView().requestLayout();
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener mShowLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            SimpleTooltipUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), this);
            if (dismissed)
                return;

            if (mOnShowListener != null)
                mOnShowListener.onShow(SimpleTooltip.this);
            mOnShowListener = null;

            mContentLayout.setVisibility(View.VISIBLE);
        }
    };

    private final ViewTreeObserver.OnGlobalLayoutListener mAnimationLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            SimpleTooltipUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), this);
            if (dismissed)
                return;

        }
    };


    /**
     * <div class="pt">Listener utilizado para chamar o <tt>SimpleTooltip#dismiss()</tt> quando a <tt>View</tt> root é encerrada sem que a tooltip seja fechada.
     * Pode ocorrer quando a tooltip é utilizada dentro de Dialogs.</div>
     */
    private final ViewTreeObserver.OnGlobalLayoutListener mAutoDismissLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (dismissed) {
                SimpleTooltipUtils.removeOnGlobalLayoutListener(mPopupWindow.getContentView(), this);
                return;
            }

            if (!mRootView.isShown())
                dismiss();
        }
    };

    public interface OnDismissListener {
        void onDismiss(SimpleTooltip tooltip);
    }

    public interface OnShowListener {
        void onShow(SimpleTooltip tooltip);
    }

    public static class Builder {

        private final Context context;
        private boolean dismissOnInsideTouch = true;
        private boolean dismissOnOutsideTouch = true;
        private boolean modal = false;
        private View contentView;
        @IdRes
        private int textViewId = android.R.id.text1;
        private CharSequence text = "";
        private View anchorView;
        private int gravity = Gravity.BOTTOM;
        private boolean showArrow = true;
        private Drawable arrowDrawable;
        private float margin = -1;
        private float padding = -1;
        private int backgroundColor;
        private int textColor;
        private int arrowColor;
        private float arrowHeight;
        private float arrowWidth;

        public Builder(Context context) {
            this.context = context;
        }

        public SimpleTooltip build() throws IllegalArgumentException {
            validateArguments();
            if (backgroundColor == 0) {
                backgroundColor = SimpleTooltipUtils.getColor(context, mDefaultBackgroundColorRes);
            }
            if (textColor == 0) {
                textColor = SimpleTooltipUtils.getColor(context, mDefaultTextColorRes);
            }
            if (contentView == null) {
                TextView tv = new TextView(context);
                SimpleTooltipUtils.setTextAppearance(tv, mDefaultTextAppearanceRes);
                tv.setBackgroundColor(backgroundColor);
                tv.setTextColor(textColor);
                contentView = tv;
            }
            if (arrowColor == 0) {
                arrowColor = SimpleTooltipUtils.getColor(context, mDefaultArrowColorRes);
            }
            if (arrowDrawable == null) {
                int arrowDirection = SimpleTooltipUtils.tooltipGravityToArrowDirection(gravity);
                arrowDrawable = new ArrowDrawable(arrowColor, arrowDirection);
            }
            if (margin < 0) {
                margin = context.getResources().getDimension(mDefaultMarginRes);
            }
            if (padding < 0) {
                padding = context.getResources().getDimension(mDefaultPaddingRes);
            }
            if (showArrow) {
                if (arrowWidth == 0)
                    arrowWidth = context.getResources().getDimension(mDefaultArrowWidthRes);
                if (arrowHeight == 0)
                    arrowHeight = context.getResources().getDimension(mDefaultArrowHeightRes);
            }
            return new SimpleTooltip(this);
        }

        private void validateArguments() throws IllegalArgumentException {
            if (context == null) {
                throw new IllegalArgumentException("Context not specified.");
            }
            if (anchorView == null) {
                throw new IllegalArgumentException("Anchor view not specified.");
            }
        }

        public Builder text(CharSequence text) {
            this.text = text;
            return this;
        }

        public Builder anchorView(View anchorView) {
            this.anchorView = anchorView;
            return this;
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder padding(float padding) {
            this.padding = padding;
            return this;
        }

        public Builder padding(@DimenRes int paddingRes) {
            this.padding = context.getResources().getDimension(paddingRes);
            return this;
        }

        public Builder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }
    }
}
