package squares.iesnules.com.squares.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import squares.iesnules.com.squares.R;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewDataProvider;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewListener;

/**
 * TODO: document your custom view class.
 */
public class BoardView extends FrameLayout {
    private static final String TAG = "BoardView";

    private final int NODE_DIM = 20;

    private GridLayout mGridLayout = null;

    private BoardViewDataProvider mDataProvider = null;
    private BoardViewListener mListener = null;

    private int mBoardRows;
    private int mBoardCols;

    public BoardView(Context context) {
        super(context);
    }

    public void setDataProvider(BoardViewDataProvider provider) {
        mDataProvider = provider;
    }

    public BoardViewDataProvider getDataProvider() {
        return mDataProvider;
    }

    public void setListener(BoardViewListener listener) {
        mListener = listener;
    }

    public BoardViewListener getListener() {
        return mListener;
    }

    // Redraw game board
    public void reloadBoard() {
        if (mGridLayout != null) {
            removeView(mGridLayout);
        }

        mGridLayout = new GridLayout(this.getContext());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mGridLayout.setLayoutParams(params);

        if (mDataProvider != null) {
            // Get board dimensions
            mBoardRows = mDataProvider.rowsOfBoard(this);
            mBoardCols = mDataProvider.colsOfBoard(this);


        }

        addView(mGridLayout);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.v(TAG, "Width: " + mGridLayout.getWidth() + " Height " + mGridLayout.getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.v(TAG, "Width: " + getWidth() + " Height " + getHeight());

        int currentWidth = getWidth();
        int currentHeight = getHeight();

        // Compute grid margins to get square cells square.
        if (currentWidth > 0 && currentHeight > 0 && mGridLayout != null) {
            int squareWidth = (currentWidth - (mBoardCols + 1)*NODE_DIM) / mBoardCols;
            int squareHeight = (currentHeight - (mBoardRows + 1)*NODE_DIM) / mBoardRows;

            if (squareWidth > squareHeight) {
                int margin = (squareWidth - squareHeight) / 2;

                FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams)getLayoutParams();
                params.setMargins(margin, 0, margin, 0);

                setLayoutParams((FrameLayout.LayoutParams)params);
            }
            else {
                int margin = (squareHeight - squareWidth) / 2;

                FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams)getLayoutParams();
                params.setMargins(0, margin, 0, margin);

                setLayoutParams((FrameLayout.LayoutParams)params);
            }
        }
    }
}

