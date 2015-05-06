package com.iesnules.squares.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.iesnules.squares.R;
import com.iesnules.squares.custom_views.interfaces.BoardViewDataProvider;
import com.iesnules.squares.custom_views.interfaces.BoardViewListener;

/**
 * TODO: document your custom view class.
 */
public class BoardView extends ViewGroup /*implements View.OnClickListener*/ {
    private static final String TAG = "BoardView";

    private final int NODE_DIM = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());

    private int mSquareDim = NODE_DIM;

    private BoardViewDataProvider mDataProvider = null;
    private BoardViewListener mListener = null;

    private int mBoardRows = 1;
    private int mBoardCols = 1;

    private boolean mTouchStarted = false;
    private int mPointerID;
    private int mTouchedNode_I;
    private int mTouchedNode_J;
    private ImageView mEventMarkedEdge;

    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        applyAttributes(attrs);
    }

    public BoardView(Context context, int rows, int cols) {
        super(context);

        mBoardRows = Math.max(rows, 1);
        mBoardCols = Math.max(cols, 1);

        createAllSubviews();
    }

    private void applyAttributes(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BoardView,
                0, 0);

        try {
            mBoardRows = a.getInt(R.styleable.BoardView_boardRows, 1);
            mBoardCols = a.getInt(R.styleable.BoardView_boardCols, 1);
        } finally {
            a.recycle();
        }

        createAllSubviews();
    }

    public void setDimensions(int rows, int cols) {
        mBoardRows = Math.max(rows, 1);
        mBoardCols = Math.max(cols, 1);

        createAllSubviews();
    }

    private void createAllSubviews() {
        removeAllViews();

        /*
        ImageView node = null;
        Button edge = null;
        ImageView square = null;
        */

        int rows = 2*mBoardRows + 1;
        int cols = 2*mBoardCols + 1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i%2 == 0) { // Even rows have nodes and horizontal edges
                    if (j%2 == 0) {
                        addView(getNode());
                    }
                    else {
                        addView(getEdge());
                    }
                }
                else { // Odd rows have vertical edges and squares
                    if (j%2 == 0) {
                        addView(getEdge());
                    }
                    else {
                        addView(getSquare());
                    }
                }
            }
        }
    }

    public int getBoardRows() {
        return mBoardRows;
    }

    public int getBoardCols() {
        return mBoardCols;
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

        if (mDataProvider != null) {
            int rows = 2*mBoardRows + 1;
            int cols = 2*mBoardCols + 1;

            //int checkedEdgeColor = getResources().getColor(R.color.checked_edge_color);
            //int uncheckedEdgeColor = getResources().getColor(R.color.overlay_color);
            //int uncheckedEdgeColor = Color.TRANSPARENT;

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (i%2 == 0) { // Even rows have nodes and horizontal edges
                        if (j%2 != 0) {
                            //Button edge = (Button)getChildAt(i * cols + j);
                            ImageView edge = (ImageView)getChildAt(i * cols + j);
                            Drawable bg = null;
                            if (mDataProvider.stateOfEdgeWithCoordinates(i, j, this) != 0) {
                                bg = getResources().getDrawable(R.mipmap.edge);
                            }
                            /*
                            int color = mDataProvider.stateOfEdgeWithCoordinates(i, j, this) == 0 ? uncheckedEdgeColor : checkedEdgeColor;
                            edge.setBackgroundColor(color);
                            */
                            if (Build.VERSION.SDK_INT >= 16) {
                                edge.setBackground(bg);
                            }
                            else {
                                edge.setBackgroundDrawable(bg);
                            }
                        }
                    }
                    else { // Odd rows have vertical edges and squares
                        if (j%2 == 0) {
                            //Button edge = (Button)getChildAt(i * cols + j);
                            ImageView edge = (ImageView)getChildAt(i * cols + j);
                            Drawable bg = null;
                            if (mDataProvider.stateOfEdgeWithCoordinates(i, j, this) != 0) {
                                bg = getResources().getDrawable(R.mipmap.edge);
                            }
                            /*
                            int color = mDataProvider.stateOfEdgeWithCoordinates(i, j, this) == 0 ? uncheckedEdgeColor : checkedEdgeColor;
                            edge.setBackgroundColor(color);
                            */
                            if (Build.VERSION.SDK_INT >= 16) {
                                edge.setBackground(bg);
                            }
                            else {
                                edge.setBackgroundDrawable(bg);
                            }
                        }
                        else {
                            ImageView square = (ImageView)getChildAt(i * cols + j);
                            byte state = mDataProvider.stateOfSquareWithCoordinates(i, j, this);
                            Drawable bg = null;

                            if (mDataProvider.stateOfSquareWithCoordinates(i, j, this) != 0) {
                                bg = mDataProvider.shapeForPlayerNumber(state, this);
                            }

                            if (Build.VERSION.SDK_INT >= 16) {
                                square.setBackground(bg);
                            }
                            else {
                                square.setBackgroundDrawable(bg);
                            }
                        }
                    }
                }
            }
        }
    }

    private ImageView getNode() {
        ImageView node = new ImageView(this.getContext());
        Drawable drawable = getResources().getDrawable(R.mipmap.node);

        if (Build.VERSION.SDK_INT >= 16) {
            node.setBackground(drawable);
        }
        else {
            node.setBackgroundDrawable(drawable);
        }


        return node;
    }

    private ImageView getEdge() {
        ImageView edge = new ImageView(this.getContext());

        return edge;
    }

    private ImageView getSquare() {
        ImageView square = new ImageView(this.getContext());

        return square;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            int count = getChildCount();

            int vLeft;
            int vTop;
            int vRight;
            int vBottom;

            int rows = 2 * mBoardRows + 1;
            int cols = 2 * mBoardCols + 1;

            vTop = getPaddingTop();

            for (int i = 0; i < rows; i++) {

                vLeft = getPaddingLeft();
                vBottom = vTop + (i % 2 == 0 ? NODE_DIM : mSquareDim);

                for (int j = 0; j < cols; j++) {
                    View view = getChildAt(i * cols + j);
                    vRight = vLeft + (j % 2 == 0 ? NODE_DIM : mSquareDim);
                    view.layout(vLeft, vTop, vRight, vBottom);

                    vLeft = vRight;
                }

                vTop = vBottom;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int wSpec = resolveSize(minW, widthMeasureSpec);

        int minH = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();
        int hSpec = resolveSize(minH, heightMeasureSpec);

        // Compute squareDim & desired width&height
        int w = View.MeasureSpec.getSize(wSpec);
        int h = View.MeasureSpec.getSize(hSpec);

        int squareWidth = (w - (mBoardCols + 1)*NODE_DIM) / mBoardCols;
        int squareHeight = (h - (mBoardRows + 1)*NODE_DIM) / mBoardRows;

        mSquareDim = squareWidth > squareHeight ? squareHeight : squareWidth;

        int finalW = mBoardCols * mSquareDim + (mBoardCols + 1) * NODE_DIM;
        int finalH = mBoardRows * mSquareDim + (mBoardRows + 1) * NODE_DIM;

        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(finalW, MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(finalH, MeasureSpec.EXACTLY));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            int action = event.getActionMasked();
            int index = event.getActionIndex();

            if (action == MotionEvent.ACTION_DOWN) {
                if (!mTouchStarted) { // Only manage first finger contact...
                    mTouchStarted = true;
                    mPointerID = event.getPointerId(index);

                    mTouchedNode_J = nodeCoord(event.getX(index));
                    mTouchedNode_I = nodeCoord(event.getY(index));
                }
            }
            else if (action == MotionEvent.ACTION_MOVE) {
                if (mPointerID == event.getPointerId(index)) {
                    int newNode_J = nodeCoord(event.getX(index));
                    int newNode_I = nodeCoord(event.getY(index));

                    int edge_I = getEdgeRowCoordinateForNewNode(newNode_I, newNode_J);
                    int edge_J = getEdgeColCoordinateForNewNode(newNode_I, newNode_J);

                    ImageView edge = getEdgeWithCoordinates(edge_I, edge_J);
                    if (edge != null &&
                            mDataProvider.stateOfEdgeWithCoordinates(edge_I, edge_J, this) == 0) {
                        if (edge != mEventMarkedEdge) {
                            if (mEventMarkedEdge != null) {
                                //mEventMarkedEdge.setBackgroundColor(Color.TRANSPARENT);
                                if (Build.VERSION.SDK_INT >= 16) {
                                    mEventMarkedEdge.setBackground(null);
                                }
                                else {
                                    mEventMarkedEdge.setBackgroundDrawable(null);
                                }
                            }

                            //edge.setBackgroundColor(Color.WHITE);
                            Drawable bg = getResources().getDrawable(R.mipmap.node);
                            if (Build.VERSION.SDK_INT >= 16) {
                                edge.setBackground(bg);
                            }
                            else {
                                edge.setBackgroundDrawable(bg);
                            }
                            mEventMarkedEdge = edge;
                        }
                    }
                    else {
                        if (mEventMarkedEdge != null) {
                            //mEventMarkedEdge.setBackgroundColor(Color.TRANSPARENT);
                            if (Build.VERSION.SDK_INT >= 16) {
                                mEventMarkedEdge.setBackground(null);
                            }
                            else {
                                mEventMarkedEdge.setBackgroundDrawable(null);
                            }

                            mEventMarkedEdge = null;
                        }
                    }
                }
            }
            else if (action == MotionEvent.ACTION_UP) {
                if (mPointerID == event.getPointerId(index)) {
                    int newNode_J = nodeCoord(event.getX(index));
                    int newNode_I = nodeCoord(event.getY(index));

                    markEdge(getEdgeRowCoordinateForNewNode(newNode_I, newNode_J),
                            getEdgeColCoordinateForNewNode(newNode_I, newNode_J));

                    mTouchStarted = false;
                    mEventMarkedEdge = null;
                }
            }
            else if (action == MotionEvent.ACTION_CANCEL) {
                mTouchStarted = false;
                mEventMarkedEdge = null;
            }
        }

        return true;
    }

    private int getEdgeRowCoordinateForNewNode(int newNode_I, int newNode_J) {
        int row = -1;

        if ((mTouchedNode_I - newNode_I) == 0 && (mTouchedNode_J - newNode_J == 1)) { // West edge...
            row = 2 * mTouchedNode_I;
        }
        else if ((mTouchedNode_I - newNode_I) == 0 && (mTouchedNode_J - newNode_J == -1)) { // East edge...
            row = 2 * mTouchedNode_I;
        }
        else if ((mTouchedNode_I - newNode_I) == 1 && (mTouchedNode_J - newNode_J == 0)) { // North edge...
            row = 2 * mTouchedNode_I - 1;
        }
        else if ((mTouchedNode_I - newNode_I) == -1 && (mTouchedNode_J - newNode_J == 0)) { // South edge...
            row = 2 * mTouchedNode_I + 1;
        }

        return row;
    }

    private int getEdgeColCoordinateForNewNode(int newNode_I, int newNode_J) {
        int col = -1;

        if ((mTouchedNode_I - newNode_I) == 0 && (mTouchedNode_J - newNode_J == 1)) { // West edge...
            col = 2 * mTouchedNode_J - 1;
        }
        else if ((mTouchedNode_I - newNode_I) == 0 && (mTouchedNode_J - newNode_J == -1)) { // East edge...
            col = 2 * mTouchedNode_J + 1;
        }
        else if ((mTouchedNode_I - newNode_I) == 1 && (mTouchedNode_J - newNode_J == 0)) { // North edge...
            col = 2 * mTouchedNode_J;
        }
        else if ((mTouchedNode_I - newNode_I) == -1 && (mTouchedNode_J - newNode_J == 0)) { // South edge...
            col = 2 * mTouchedNode_J;
        }

        return col;
    }

    private int nodeCoord(float touchCoord) {
        float touchableDim = mSquareDim + NODE_DIM;
        float halfTouchableDim = touchableDim / 2;

        return (int)((touchCoord + halfTouchableDim) / touchableDim);
    }

    private void markEdge(int row, int col) {
        if (row >= 0 && col >= 0 && mDataProvider.stateOfEdgeWithCoordinates(row, col, this) == 0) {
            //provisionallyMarkEdge(row, col);
            mListener.edgeClickedWithCoordinates(row, col, this);
        }
    }

    /*
    private void provisionallyMarkEdge(int row, int col) {
        ImageView edge = getEdgeWithCoordinates(row, col);

        if (edge != null) {
            edge.setBackgroundColor(Color.WHITE);
        }
    }
    */

    private ImageView getEdgeWithCoordinates(int row, int col) {
        ImageView edge = null;
        int cols = 2*mBoardCols + 1;

        if (row >= 0 && col >= 0) {
            View view = getChildAt(row * cols + col);

            if (view instanceof ImageView) {
                edge = (ImageView)view;
            }
        }

        return edge;
    }
}

