package cn.timeface.circle.baby.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.timeface.circle.baby.R;

/**
 * @author wxw
 * @from 2017/2/20
 * 小黄人 一分钟了解XXX
 */
public class IntroView extends RelativeLayout {

    private static final int DURATION_IMAGE_MOVE_IN = 100;
    private static final int DURATION_IMAGE_MOVE_OUT = 100;
    private static final int DURATION_TEXT_MOVE_IN = 100;
    private static final int DURATION_TEXT_MOVE_OUT = 100;
    private static final int DURATION_IMAGE_SHAKE = 200;

    private TextView tvIntro;
    private ImageView ivImage;

    private String introText;

    private float textTranslationX; // TextView原始X轴位移值
    private float imageTranslationX; // ImageView原始X轴位移值

    private Animator.AnimatorListener animatorListener;
    private boolean animationRunning = false;
    private boolean moveIn = false; // TextView已移入屏幕

    private OnClickTextListener onClickTextListener;

    public IntroView(Context context) {
        super(context);
        init();
    }

    public IntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IntroView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_intro, this, true);

        tvIntro = (TextView) findViewById(R.id.tv_intro);
        ivImage = (ImageView) findViewById(R.id.iv_image);

        textTranslationX = tvIntro.getTranslationX();
        imageTranslationX = ivImage.getTranslationX();

        setIntroText(introText);

        tvIntro.setOnClickListener(v -> {
            if (onClickTextListener != null) {
                onClickTextListener.onClickText(v);
                if (moveIn) {
                    startMoveOutAnimation();
                }
            }
        });

        ivImage.setOnClickListener(v -> {
            if (moveIn) {
                startMoveOutAnimation();
            } else {
                startMoveInAnimation();
            }
        });
    }

    public void setIntroText(String introText) {
        if (introText != null) {
            this.introText = introText;
            tvIntro.setText(introText);
        }
    }

    public String getIntroText() {
        return introText;
    }

    public void setOnClickTextListener(OnClickTextListener onClickTextListener) {
        this.onClickTextListener = onClickTextListener;
    }

    private void startMoveInAnimation() {
        if (animationRunning) return;

        ObjectAnimator imageMoveIn = ObjectAnimator.ofFloat(ivImage, "translationX", 0);
        imageMoveIn.setDuration(DURATION_IMAGE_MOVE_IN);

        ObjectAnimator textMoveIn = ObjectAnimator.ofFloat(tvIntro, "translationX", 0);
        textMoveIn.setDuration(DURATION_TEXT_MOVE_IN);

        AnimatorSet animSet = new AnimatorSet();
        animSet.addListener(getAnimatorListener());
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.playSequentially(imageMoveIn, textMoveIn);
        animSet.start();

        moveIn = true; // 动画执行时按钮已禁用，提前赋值没毛病
    }

    private void startMoveOutAnimation() {
        if (animationRunning) return;

        ObjectAnimator imageMoveOut = ObjectAnimator.ofFloat(ivImage, "translationX", imageTranslationX);
        imageMoveOut.setDuration(DURATION_IMAGE_MOVE_OUT);

        ObjectAnimator textMoveOut = ObjectAnimator.ofFloat(tvIntro, "translationX", textTranslationX);
        textMoveOut.setDuration(DURATION_TEXT_MOVE_OUT);

        AnimatorSet animSet = new AnimatorSet();
        animSet.addListener(getAnimatorListener());
        animSet.setInterpolator(new AccelerateInterpolator());
        animSet.playSequentially(textMoveOut, imageMoveOut);
        animSet.start();

        moveIn = false; // 动画执行时按钮已禁用，提前赋值没毛病
    }

    public void startShakeAnimation() {
        if (animationRunning) return;

        ObjectAnimator moveIn = ObjectAnimator.ofFloat(ivImage, "translationX", 0);
        moveIn.setDuration(DURATION_IMAGE_MOVE_IN);
        moveIn.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator shakeHead = ObjectAnimator.ofFloat(ivImage, "rotation", 0f, 10f, 0f, -10f, 0);
        shakeHead.setStartDelay(0);
        shakeHead.setDuration(DURATION_IMAGE_SHAKE);
        shakeHead.setRepeatMode(ValueAnimator.RESTART);
        shakeHead.setRepeatCount(3);
        shakeHead.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator moveOut = ObjectAnimator.ofFloat(ivImage, "translationX", imageTranslationX);
        moveOut.setDuration(DURATION_IMAGE_MOVE_OUT);
        moveOut.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(moveIn, shakeHead, moveOut);
        animSet.addListener(getAnimatorListener());
        animSet.start();
    }

    private Animator.AnimatorListener getAnimatorListener() {
        if (animatorListener == null) {
            animatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animationRunning = true;
                    ivImage.setEnabled(false);
                    tvIntro.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    animationRunning = false;
                    ivImage.setEnabled(true);
                    tvIntro.setEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            };
        }
        return animatorListener;
    }

    /**
     * 介绍TextView 点击监听器
     */
    public interface OnClickTextListener {
        void onClickText(View v);
    }

}
