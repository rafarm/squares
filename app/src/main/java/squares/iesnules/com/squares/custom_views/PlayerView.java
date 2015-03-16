package squares.iesnules.com.squares.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import squares.iesnules.com.squares.R;

/**
 * TODO: document your custom view class.
 */
public class PlayerView extends LinearLayout {
    private ImageView mPlayerImage;
    private TextView mPlayerName;

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
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PlayerView, defStyle, 0);

        // TODO: Process AtributeSet

        a.recycle();

        mPlayerImage = (ImageView)findViewById(R.id.playerImage);
        mPlayerName = (TextView)findViewById(R.id.playerName);
    }

    public Drawable getPlayerImage() {
        return mPlayerImage.getDrawable():
    }

    public void setPlayerImage(Drawable image) {
        mPlayerImage.setImageDrawable(image);
    }
    public Text getPlayerName(){
        return mPlayerName.getText();
    }
    public void setPlayerName(Text text);
       mPlayerName.set
}
