package squares.iesnules.com.squares.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import squares.iesnules.com.squares.R;

/**
 * TODO: document your custom view class.
 */
public class PlayerView extends FrameLayout {
    private ImageView mPlayerImage;
    private TextView mPlayerName;
    private TextView mPlayerScore;
    private  ImageView mShapeImage;

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
        params.setMargins(5,5,5,5);
        setLayoutParams(params);
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

}
