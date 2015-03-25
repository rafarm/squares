package squares.iesnules.com.squares.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import squares.iesnules.com.squares.R;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewDataProvider;
import squares.iesnules.com.squares.custom_views.interfaces.BoardViewListener;

/**
 * TODO: document your custom view class.
 */
public class BoardView extends FrameLayout {
    private static final String TAG = "BoardView";

    private final int NODE_DIM = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

    private GridLayout mGridLayout = null;

    private BoardViewDataProvider mDataProvider = null;
    private BoardViewListener mListener = null;

    private int mBoardRows;
    private int mBoardCols;

    private int mSquareDim = NODE_DIM;

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
        mGridLayout.setBackgroundColor(Color.BLUE);

        if (mDataProvider != null) {
            // Get board dimensions
            mBoardRows = mDataProvider.rowsOfBoard(this);
            mBoardCols = mDataProvider.colsOfBoard(this);

            ImageView node = null;
            Button edge = null;
            ImageView square = null;

            for (int i = 0; i < (2*mBoardRows + 1); i++) {
                for (int j = 0; j < (2*mBoardCols + 1); j++) {
                    if (i%2 == 0) { // Even rows have nodes and horizontal edges
                        if (j%2 == 0) {
                            mGridLayout.addView(getNode(i, j));
                        }
                        else {
                            mGridLayout.addView(getHorizontalEdge(i, j));
                        }
                    }
                    else { // Odd rows have vertical edges and squares
                        if (j%2 == 0) {
                            mGridLayout.addView(getVerticalEdge(i, j));
                        }
                        else {
                            mGridLayout.addView(getSquare(i, j));
                        }
                    }
                }
            }
        }

        addView(mGridLayout);
    }

    private ImageView getNode(int row, int col) {
        ImageView node = new ImageView(this.getContext());

        node.setBackgroundDrawable(getResources().getDrawable(R.mipmap.node));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col));
        //params.setGravity(Gravity.CENTER);
        params.height = NODE_DIM;
        params.width = NODE_DIM;
        node.setLayoutParams(params);

        return node;
    }

    private Button getHorizontalEdge(int row, int col) {
        Button edge = getEdge(row, col);

        GridLayout.LayoutParams params = (GridLayout.LayoutParams)edge.getLayoutParams();
        //params.setGravity(Gravity.FILL_HORIZONTAL);
        params.width = mSquareDim;
        params.height = NODE_DIM;
        edge.setLayoutParams(params);

        return edge;
    }

    private Button getVerticalEdge(int row, int col) {
        Button edge = getEdge(row, col);

        GridLayout.LayoutParams params = (GridLayout.LayoutParams)edge.getLayoutParams();
        //params.setGravity(Gravity.FILL_VERTICAL);
        params.width = NODE_DIM;
        params.height = mSquareDim;
        edge.setLayoutParams(params);

        return edge;
    }

    private Button getEdge(int row, int col) {
        Button edge = new Button(this.getContext());

        edge.setBackgroundDrawable(getResources().getDrawable(R.mipmap.node));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col));
        edge.setLayoutParams(params);

        return edge;
    }

    private ImageView getSquare(int row, int col) {
        ImageView square = new ImageView(this.getContext());

        //square.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_launcher));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col));
        //params.setGravity(Gravity.FILL);
        params.width = mSquareDim;
        params.height = mSquareDim;
        square.setLayoutParams(params);

        return square;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {


        super.onLayout(changed, left, top, right, bottom);
        //Log.v(TAG, "Width: " + mGridLayout.getWidth() + " Height " + mGridLayout.getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Log.v(TAG, "Width: " + getWidth() + " Height " + getHeight());

        int currentWidth = getWidth();
        int currentHeight = getHeight();

        // Compute grid margins to get square cells square.
        if (currentWidth > 0 && currentHeight > 0 && mGridLayout != null) {
            int squareWidth = (currentWidth - (mBoardCols + 1)*NODE_DIM) / mBoardCols;
            int squareHeight = (currentHeight - (mBoardRows + 1)*NODE_DIM) / mBoardRows;

            if (squareWidth > squareHeight) {
                int margin = (squareWidth - squareHeight)*mBoardCols / 2;

                FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams)getLayoutParams();
                params.setMargins(margin, 0, margin, 0);

                setLayoutParams((FrameLayout.LayoutParams)params);

                mSquareDim = squareHeight;
            }
            else {
                int margin = (squareHeight - squareWidth)*mBoardRows / 2;

                FrameLayout.MarginLayoutParams params = (FrameLayout.MarginLayoutParams)getLayoutParams();
                params.setMargins(0, margin, 0, margin);

                mSquareDim = squareWidth;

                setLayoutParams((FrameLayout.LayoutParams)params);
            }

            int count = mGridLayout.getChildCount();

            Log.v(TAG, "onLayout >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            for (int i = 0; i < count; i++) {
                View view = mGridLayout.getChildAt(i);
                GridLayout.LayoutParams params = (GridLayout.LayoutParams)view.getLayoutParams();

                int row = i/(2*mBoardCols + 1);
                int col = i%(2*mBoardCols + 1);

                if (row%2 == 0) { // Even rows have nodes and horizontal edges
                    if (col%2 != 0) {
                        params.width = mSquareDim;
                        Log.v(TAG, "Horizontal Edge width: " + params.width);
                        Log.v(TAG, "Horizontal Edge height: " + params.height);
                    }
                }
                else { // Odd rows have vertical edges and squares
                    if (col%2 == 0) {
                        params.height = mSquareDim;
                        Log.v(TAG, "Vertical Edge width: " + params.width);
                        Log.v(TAG, "Vertical Edge height: " + params.height);
                    }
                    else {
                        params.width = mSquareDim;
                        params.height = mSquareDim;
                        Log.v(TAG, "Square width: " + params.width);
                        Log.v(TAG, "Square height: " + params.height);
                    }
                }

                view.setLayoutParams(params);
            }
            Log.v(TAG, "onLayout <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }
}

