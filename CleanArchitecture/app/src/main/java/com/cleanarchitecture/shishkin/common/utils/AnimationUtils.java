package com.cleanarchitecture.shishkin.common.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.cleanarchitecture.shishkin.R;


public class AnimationUtils {

    public static final int FADE_DURATION = 200;
    public static final int EXPAND_OR_COLLAPSE_DURATION = 300;

    // ************************************************************************************************************************************************************************
    // * Collapse and expand animations
    // ************************************************************************************************************************************************************************

    public static Animation getExpandViewAnimation(final View view, int duration) {

        view.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1 ? RelativeLayout.LayoutParams.WRAP_CONTENT : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        return animation;
    }

    public static Animation getCollapseViewAnimation(final View view, int duration) {
        final int initialHeight = view.getMeasuredHeight();

        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        return animation;
    }

    public static void startExpandViewAnimation(final View expandableContainer) {
        if (expandableContainer.getVisibility() != View.VISIBLE) {
            final Animation animation = getExpandViewAnimation(expandableContainer, EXPAND_OR_COLLAPSE_DURATION);
            expandableContainer.startAnimation(animation);
        }
    }

    public static void startCollapseViewAnimation(final View expandableContainer) {
        if (expandableContainer.getVisibility() == View.VISIBLE) {
            final Animation animation = getCollapseViewAnimation(expandableContainer, EXPAND_OR_COLLAPSE_DURATION);
            expandableContainer.startAnimation(animation);
        }
    }

    public static void startExpandViewAnimation(final View expandableContainer, final View fadeContainer) {
        startExpandViewAnimation(expandableContainer, fadeContainer, null, EXPAND_OR_COLLAPSE_DURATION);
    }

    public static void startExpandViewAnimation(final View expandableContainer, final View fadeContainer, final int expandDuration) {
        startExpandViewAnimation(expandableContainer, fadeContainer, null, expandDuration);
    }

    public static void startExpandViewAnimation(final View expandableContainer, final View fadeContainer, final AnimationListener listener) {
        startExpandViewAnimation(expandableContainer, fadeContainer, listener, EXPAND_OR_COLLAPSE_DURATION);
    }

    public static void startExpandViewAnimation(final View expandableContainer, final View fadeContainer, final AnimationListener listener, final int expandDuration) {

        final Animation animation = getExpandViewAnimation(expandableContainer, expandDuration);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation secondAnimation = getFadeInAnimation(FADE_DURATION);
                if (listener != null) {
                    secondAnimation.setAnimationListener(listener);
                }
                fadeContainer.startAnimation(secondAnimation);
            }
        });
        expandableContainer.startAnimation(animation);
    }

    public static void startCollapseViewAnimation(final View collapsibleContainer, final View fadeContainer) {
        startCollapseViewAnimation(collapsibleContainer, fadeContainer, null, EXPAND_OR_COLLAPSE_DURATION);
    }

    public static void startCollapseViewAnimation(final View collapsibleContainer, final View fadeContainer, int collapseDuration) {
        startCollapseViewAnimation(collapsibleContainer, fadeContainer, null, collapseDuration);
    }

    public static void startCollapseViewAnimation(final View collapsibleContainer, final View fadeContainer, final AnimationListener listener) {
        startCollapseViewAnimation(collapsibleContainer, fadeContainer, listener, EXPAND_OR_COLLAPSE_DURATION);
    }

    public static void startCollapseViewAnimation(final View collapsibleContainer, final View fadeContainer, final AnimationListener listener, final int collapseDuration) {

        final Animation animation = getFadeOutAnimation(FADE_DURATION);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation secondAnimation = getCollapseViewAnimation(collapsibleContainer, collapseDuration);
                if (listener != null) {
                    secondAnimation.setAnimationListener(listener);
                }
                collapsibleContainer.startAnimation(secondAnimation);
            }
        });
        fadeContainer.startAnimation(animation);
    }

    public static void startExpandAndCollapseWithRotationImage(final Context context, final View dropDownImage, final View collapsibleContainer, final View fadeContainer) {

        if (collapsibleContainer.getVisibility() == View.GONE) {

            AnimationListener listener = new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    AnimationUtils.startExpandViewAnimation(collapsibleContainer, fadeContainer);
                }
            };
            AnimationUtils.startRotateDown(context, dropDownImage, listener);

        } else {

            AnimationListener listener = new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    AnimationUtils.startCollapseViewAnimation(collapsibleContainer, fadeContainer);
                }
            };
            AnimationUtils.startRotateUp(context, dropDownImage, listener);

        }
    }

    // ************************************************************************************************************************************************************************
    // * Fade animations
    // ************************************************************************************************************************************************************************

    public static Animation getFadeInAnimation(int durationInMilliseconds) {
        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setFillAfter(true);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(durationInMilliseconds);
        return fadeIn;
    }

    public static Animation getFadeOutAnimation(int durationInMilliseconds) {
        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setFillAfter(true);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(durationInMilliseconds);
        return fadeOut;
    }

    public static void startFadeInAnimation(final View container) {
        startFadeInAnimation(container, false);
    }

    public static void startFadeInAnimation(final View container, boolean forceFade) {
        if (forceFade || container.getVisibility() != View.VISIBLE) {
            final Animation animation = getFadeInAnimation(FADE_DURATION);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    container.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }
            });

            container.startAnimation(animation);
        }
    }

    public static void startFadeOutAnimation(final View container) {
        startFadeOutAnimation(container, false);
    }

    public static void startFadeOutAnimation(final View container, boolean forceFade) {
        if (forceFade || container.getVisibility() == View.VISIBLE) {
            final Animation animation = getFadeOutAnimation(FADE_DURATION);
            animation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    container.setVisibility(View.GONE);
                }
            });

            container.startAnimation(animation);
        }
    }

    // ************************************************************************************************************************************************************************
    // * Translation animations
    // ************************************************************************************************************************************************************************

    public static Animation getTranslationAnimation(boolean fromTheCenter, boolean toTheRight, int distance, int time) {
        Animation animation = null;
        if (fromTheCenter && toTheRight) {
            animation = new TranslateAnimation(0, distance, 0, 0);
        } else if (fromTheCenter && !toTheRight) {
            animation = new TranslateAnimation(0, -distance, 0, 0);
        } else if (!fromTheCenter && toTheRight) {
            animation = new TranslateAnimation(distance, 0, 0, 0);
        } else if (!fromTheCenter && !toTheRight) {
            animation = new TranslateAnimation(-distance, 0, 0, 0);
        }
        animation.setDuration(time);
        animation.setFillAfter(true);
        return animation;
    }

    // ************************************************************************************************************************************************************************
    // * Scale animations
    // ************************************************************************************************************************************************************************

    public static void startScaleAnimation(View view, float startScale, float endScale, long duration) {
        Animation anim = new ScaleAnimation(1f, 1f, startScale, endScale, Animation.ABSOLUTE, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(duration);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    // ************************************************************************************************************************************************************************
    // * Rotate animations
    // ************************************************************************************************************************************************************************

    public static void startRotateDown(Context context, View view, AnimationListener listener) {
        final Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.rotate_down);
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        view.startAnimation(animation);
    }

    public static void startRotateDown(Context context, View view) {
        startRotateDown(context, view, null);
    }

    public static void startRotateUp(Context context, View view, AnimationListener listener) {
        final Animation animation = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.rotate_up);
        if (listener != null) {
            animation.setAnimationListener(listener);
        }
        view.startAnimation(animation);
    }

    public static void startRotateUp(Context context, View view) {
        startRotateUp(context, view, null);
    }

}
