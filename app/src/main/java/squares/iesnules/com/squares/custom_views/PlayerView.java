package squares.iesnules.com.squares.custom_views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import squares.iesnules.com.squares.R;

/**
 * TODO: document your custom view class.
 */
public class PlayerView extends FrameLayout implements Animator.AnimatorListener {
    private final int VIEW_MARGIN = 10;

    private ImageView mPlayerImage;
    private TextView mPlayerName;
    private TextView mPlayerScore;
    private  ImageView mShapeImage;

    private ObjectAnimator mAccelerateAnimation;
    private ObjectAnimator mRotateAnimation;
    private ObjectAnimator mBrakeAnimation;

    private boolean mPlayerInTurn;

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

        // TODO: Process AtributeSet

        a.recycle();

        mPlayerImage = (ImageView) findViewById(R.id.playerImage);
        mPlayerName = (TextView) findViewById(R.id.playerName);
        mPlayerScore = (TextView) findViewById(R.id.playerScore);
        mShapeImage = (ImageView) findViewById(R.id.shapeImage);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        params.weight = 1;
        params.setMargins(VIEW_MARGIN, VIEW_MARGIN, VIEW_MARGIN, VIEW_MARGIN);
        setLayoutParams(params);

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

        mPlayerInTurn = false;
    }

    public Drawable getPlayerImage() {
        return mPlayerImage.getDrawable();
    }

    public void setPlayerImage(Drawable image) {
        mPlayerImage.setImageDrawable(image);
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
        mShapeImage.setImageDrawable(image);
    }

    public boolean getPlayerInTurn() {
        return mPlayerInTurn;
    }

    public void setPlayerInTurn(Boolean turn) {
        mPlayerInTurn = turn;

        if (turn) {
            mAccelerateAnimation.start();
        }
        else {
            mRotateAnimation.cancel();
        }
    }

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
}
