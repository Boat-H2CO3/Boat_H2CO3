package org.koishi.launcher.h2co3.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;


public class SwipeDismissTouchListener implements View.OnTouchListener {
    private final int mSlop;
    private final long mAnimationTime;

    // Fixed properties
    private final View mView;
    private final DismissCallbacks mCallbacks;
    private int mViewHeight = 1; // 1 and not 0 to prevent dividing by zero

    // Transient properties
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private VelocityTracker mVelocityTracker;
    private float mTranslationY;

    /**
     * Constructs a new swipe-to-dismiss touch listener for the given view.
     *
     * @param view      The view to make dismissable.
     * @param callbacks The callback to trigger when the user has indicated that would like to
     *                  dismiss this view.
     */
    public SwipeDismissTouchListener(View view, DismissCallbacks callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        mSlop = vc.getScaledTouchSlop();
        mAnimationTime = view.getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);
        mView = view;
        mCallbacks = callbacks;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // offset because the view is translated during swipe
        motionEvent.offsetLocation(0, mTranslationY);

        boolean returnTrue = false;

        if (mViewHeight < 2) {
            mViewHeight = mView.getHeight();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN -> {
                // TODO: ensure this is a finger, and set a flag
                mDownX = motionEvent.getRawX();
                mDownY = motionEvent.getRawY();
                if (mCallbacks.canDismiss()) {
                    mVelocityTracker = VelocityTracker.obtain();
                    mVelocityTracker.addMovement(motionEvent);
                }
                return true;
            }
            case MotionEvent.ACTION_UP -> {
                if (mVelocityTracker == null) {
                    break;
                }

                float deltaY = motionEvent.getRawY() - mDownY;
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);

                boolean dismiss = false;
                boolean dismissDown = false;
                if (deltaY > (float) mViewHeight / 2 && mSwiping) {
                    dismiss = true;
                    dismissDown = deltaY > 0;
                    returnTrue = true;
                } else if (Math.abs(deltaY) > 0) {
                    returnTrue = true;
                }
                if (dismiss) {
                    // dismiss
                    mView.animate()
                            .translationY(dismissDown ? mViewHeight : 0)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    performDismiss();
                                }
                            });

                } else if (mSwiping) {
                    // cancel

                    mView.animate()
                            .translationY(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mTranslationY = 0;
                mDownX = 0;
                mDownY = 0;
                mSwiping = false;
            }
            case MotionEvent.ACTION_CANCEL -> {
                if (mVelocityTracker == null) {
                    break;
                }
                mView.animate()
                        .translationY(0)
                        .alpha(1)
                        .setDuration(mAnimationTime)
                        .setListener(null);
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                mTranslationY = 0;
                mDownX = 0;
                mDownY = 0;
                mSwiping = false;
            }
            case MotionEvent.ACTION_MOVE -> {
                if (mVelocityTracker == null) {
                    break;
                }
                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                float deltaY = motionEvent.getRawY() - mDownY;
                if (Math.abs(deltaY) > mSlop && Math.abs(deltaX) < Math.abs(deltaY) / 2) {
                    mSwiping = true;
                    mSwipingSlop = (deltaY > 0 ? mSlop : 0);
                    mView.getParent().requestDisallowInterceptTouchEvent(true);


                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mView.onTouchEvent(cancelEvent);
                    cancelEvent.recycle();
                }

                if (mSwiping) {
                    mTranslationY = deltaY;
                    if (deltaY - mSwipingSlop < 0)
                        mView.setTranslationY(0);
                    else mView.setTranslationY(deltaY - mSwipingSlop);
                    return true;
                }
            }
        }
        return returnTrue;
    }

    private void performDismiss() {
        // Animate the dismissed view to zero-height and then fire the dismiss callback.
        // This triggers layout on each animation frame; in the future we may want to do something
        // smarter and more performant.

        final ViewGroup.LayoutParams lp = mView.getLayoutParams();
        final int originalHeight = mView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCallbacks.onDismiss(mView);
                // Reset view presentation
                mView.setAlpha(1f);
                mView.setTranslationX(0);
                lp.height = originalHeight;
                mView.setLayoutParams(lp);
            }
        });

        animator.addUpdateListener(valueAnimator -> {
            lp.height = (Integer) valueAnimator.getAnimatedValue();
            mView.setLayoutParams(lp);
        });

        animator.start();
    }

    /**
     * The callback interface used by {@link SwipeDismissTouchListener} to inform its client
     * about a successful dismissal of the view for which it was created.
     */
    public interface DismissCallbacks {
        /**
         * Called to determine whether the view can be dismissed.
         */
        boolean canDismiss();

        /**
         * Called when the user has indicated they she would like to dismiss the view.
         *
         * @param view The originating {@link View} to be dismissed.
         */
        void onDismiss(View view);
    }
}
