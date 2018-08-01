package widgets;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/1 0001
 */
public class PullLayout extends ViewGroup {
    public static final String tag = "PullLayout";
    /**
     * 头部组件
     */
    private View headView;
    /**
     * 底部组件
     */
    private View bottomView;
    /**
     * 中间拖拽组件
     */
    private View draggableView;
    /**
     * 是否允许下拉，或者是否允许上拉
     */
    private boolean enableHead = true, enableFoot = true;
    /**
     * 下拉的高度
     */
    private int pullDownDistance = 0;


    public PullLayout(Context context) {
        super(context);
        init(context);
    }

    public PullLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
    }

    private float preY;

    private boolean singlePoint = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        final float x = ev.getX();
        final float y = ev.getY();
        Log.e(tag, ev.getActionMasked() + "");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preY = y;
                singlePoint = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (singlePoint) {
                    pullDownDistance += (int) (y - preY);
                    preY = y;
                }
                singlePoint = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pullDownDistance += (int) (y - preY);
                preY = y;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                singlePoint = false;
                break;
        }
        requestLayout();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean isFirstLayout = true;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isFirstLayout) {
            if (getChildCount() != 3) {
                throw new RuntimeException("The PullLayout must have 3 children witch include headChild,draggableChild and bottomChild.");
            }
            headView = getChildAt(0);
            draggableView = getChildAt(1);
            if (!(draggableView instanceof Draggable)) {
                throw new RuntimeException("The draggableChild must implement the interface named Draggable.");
            }
            bottomView = getChildAt(2);
            isFirstLayout = true;
        }
        final int paddingL = getPaddingLeft();
        final int paddingT = getPaddingTop();
        final int paddingR = getPaddingRight();
        final int paddingB = getPaddingBottom();

        int top = paddingT + pullDownDistance - headView.getMeasuredHeight();
        int bottom = top + headView.getMeasuredHeight();
        headView.layout(paddingL, top, headView.getMeasuredWidth() - paddingR, bottom);

        top = bottom;
        bottom = top + draggableView.getMeasuredHeight();
        draggableView.layout(paddingL, top, draggableView.getMeasuredWidth() - paddingR, bottom);

        top = bottom;
        bottom = top + bottomView.getMeasuredHeight();
        bottomView.layout(paddingL, top, bottomView.getMeasuredWidth() - paddingR, bottom);
    }

    /**
     * 头部和底部加载是否可用
     *
     * @param enableHead
     * @param enableFoot
     */
    public final void enable(boolean enableHead, boolean enableFoot) {
        this.enableHead = enableHead;
        this.enableFoot = enableFoot;
    }
}
