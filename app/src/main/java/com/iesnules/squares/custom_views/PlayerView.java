package com.iesnules.squares.custom_views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iesnules.squares.R;

/**
 * TODO: document your custom view class.
 */
public class PlayerView extends FrameLayout /*implements Animator.AnimatorListener*/ {
    private final int VIEW_MARGIN = 5;

    private ImageView mPlayerImage;
    private ImageView mOverPlayerImage;
    private TextView mPlayerName;
    private TextView mPlayerScore;
    private  ImageView mShapeImage;

    /*
    private ObjectAnimator mAccelerateAnimation;
    private ObjectAnimator mRotateAnimation;
    private ObjectAnimator mBrakeAnimation;
    */

    private boolean mPlayerInTurn;
    private boolean mIsParticipant;

    private Drawable mOverPlayerInTurnDrawable;
    private Drawable mOverPlayerNoTurnDrawable;

    public PlayerView(Context context) {
        super(context);
        init(null, 0);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        inflater.inflate(R.layout.player_view, this);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PlayerView, defStyle, 0);

        a.recycle();

        mPlayerImage = (ImageView)findViewById(R.id.playerImage);
        mOverPlayerImage = (ImageView)findViewById(R.id.overPlayerImage);
        mPlayerName = (TextView)findViewById(R.id.playerName);
        mPlayerScore = (TextView)findViewById(R.id.playerScore);
        mShapeImage = (ImageView)findViewById(R.id.shapeImage);

        /*
        // Set player image Clipping
        mPlayerImage.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        mPlayerImage.setClipToOutline(true);
        */

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        params.weight = 1;
        params.setMargins(VIEW_MARGIN, VIEW_MARGIN, VIEW_MARGIN, VIEW_MARGIN);
        setLayoutParams(params);

        /*
        mAccelerateAnimation = ObjectAnimator.ofFloat(mShapeImage, "rotation", 0, 360);
        mAccelerateAnimation.setInterpolator(new AccelerateInterpolator());
        mAccelerateAnimation.setDuration(1700);
        mAccelerateAnimation.setRepeatCount(0);
        mAccelerateAnimation.addListener(this);

        mRotateAnimation = ObjectAnimator.ofFloat(mShapeImage, "rotation", 0, 360);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(ValueAnimator.INFINITE);
        mRotateAnimation.addListener(this);

        mBrakeAnimation = ObjectAnimator.ofFloat(mShapeImage, "rotation", 0, 360);
        mBrakeAnimation.setInterpolator(new DecelerateInterpolator());
        mBrakeAnimation.setDuration(1000);
        mBrakeAnimation.setRepeatCount(0);
        mBrakeAnimation.addListener(this);
        */

        mPlayerInTurn = false;
        mIsParticipant = false;
    }
    /*
    public Drawable getPlayerImage() {
        return mPlayerImage.getDrawable();
    }

    public void setPlayerImage(Drawable image) {
        mPlayerImage.setImageDrawable(image);
    }
    */

    public boolean getIsParticipant() {
        return mIsParticipant;
    }

    public void setIsParticipant(boolean isParticipant) {
        mIsParticipant = isParticipant;
    }

    public ImageView getPlayerImage() {
        return mPlayerImage;
    }

    public String getPlayerName() {
        return mPlayerName.getText().toString();
    }

    public void setPlayerName(String text) {
        mPlayerName.setText(text);
    }


    public String getPlayerScore()  {
        return mPlayerScore.getText().toString();
    }

    public void setPlayerScore(String text){
        mPlayerScore.setText(text);
    }


    public Drawable getShapeImage(){
        return mShapeImage.getDrawable();
    }

    public void setShapeImage(Drawable image){
        if (Build.VERSION.SDK_INT >= 16) {
            mShapeImage.setBackground(image);
        }
        else {
            mShapeImage.setBackgroundDrawable(image);
        }
    }

    public void setOverPlayerInTurnDrawable(Drawable drawable) {
        mOverPlayerInTurnDrawable = drawable;
        updateOverPlayerImage();
    }

    public Drawable getOverPlayerInTurnDrawable() {
        return mOverPlayerInTurnDrawable;
    }

    public Drawable getOverPlayerNoTurnDrawable() {
        return mOverPlayerNoTurnDrawable;
    }

    public void setOverPlayerNoTurnDrawable(Drawable drawable) {
        mOverPlayerNoTurnDrawable = drawable;
        updateOverPlayerImage();
    }

    public void updateOverPlayerImage() {
        if (mPlayerInTurn) {
            if (Build.VERSION.SDK_INT >= 16) {
                mOverPlayerImage.setBackground(mOverPlayerInTurnDrawable);
            }
            else {
                mOverPlayerImage.setBackgroundDrawable(mOverPlayerInTurnDrawable);
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= 16) {
                mOverPlayerImage.setBackground(mOverPlayerNoTurnDrawable);
            }
            else {
                mOverPlayerImage.setBackgroundDrawable(mOverPlayerNoTurnDrawable);
            }
        }
    }

    public boolean getPlayerInTurn() {
        return mPlayerInTurn;
    }

    public void setPlayerInTurn(Boolean turn) {
        mPlayerInTurn = turn;
        updateOverPlayerImage();
        /*
        if (turn) {
            mAccelerateAnimation.start();
        }
        else {
            mRotateAnimation.cancel();
        }
        */
    }

    /*
    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == mAccelerateAnimation && mPlayerInTurn) {
            mRotateAnimation.start();
        }
        else if (animation == mRotateAnimation) {
            mBrakeAnimation.setFloatValues(mShapeImage.getRotation(), 360);
            //mBrakeAnimation.setDuration((long)((1 - mShapeImage.getRotation() / 360) * 1000));
            mBrakeAnimation.start();
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }
    */
}
