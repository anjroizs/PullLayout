package widgets;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pull.js.com.pullwidget.R;

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
    private TextView headTView;
    /**
     * 底部组件
     */
    private View bottomView;
    private TextView bootomTView;
    /**
     * 中间拖拽组件,必须实现{@link Draggable}接口
     */
    private View draggableView;
    /**
     * 是否允许下拉，或者是否允许上拉
     */
    private boolean enableHead = true, enableFoot = true;
    /**
     * 上拉和下拉的总高度
     */
    private int pullDistance = 0;
    /**
     * 触发下拉事件的高度
     */
    private int pullDownEventDistance = 0;
    /**
     * 触发上拉事件的高度
     */
    private int pullUpEventDistance = 0;
    /**
     * 阻尼效果
     * 手势的下拉距离同实际layout距离比例。
     */
    private float ratio = 1;
    /**
     * 初始状态（头部和底部都隐藏)
     */
    private static final int STATUS_INIT = 1;
    /**
     * 继续下拉（头部开始下拉出来）
     */
    private static final int STATUS_START_PULL_DOWN = 2;
    /**
     * 松开即可刷新（头部全部下拉出来）
     */
    private static final int STATUS_RELEASE_PULLING_DOWN = 3;
    /**
     * 正在刷新
     */
    private static final int STATUS_PULLING_DOWN = 4;
    /**
     * 完成刷新
     */
    private static final int STATUS_PULLED_DOWN = 5;
    /**
     * 正在加载（底部全部显示）
     */
    private static final int STATUS_PULLING_UP = 6;
    /**
     * 完成加载
     */
    private static final int STATUS_PULLED_UP = 7;
    /**
     * 业务操作完成
     */
    private static final int STATUS_DONE = 8;
    /**
     * 状态
     */
    private int status = STATUS_INIT;

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

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    };
    private float preY;
    /**
     * 过滤多指操作
     */
    private boolean singlePoint = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);
        final float x = ev.getX();
        final float y = ev.getY();
//        Log.e(tag, ev.getActionMasked() + "-----pullDistance=" + pullDistance + "---y=" + y + "----preY=" + preY);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preY = y;
                singlePoint = true;
                resetEvent();
                break;
            case MotionEvent.ACTION_MOVE:
                if (singlePoint) {
                    pullDistance += (int) (y - preY) / ratio;
                    if (pullDistance > 0) {
                        //正在显示头部
                        if (pullDistance >= pullDownEventDistance) {
                            //头部被全部下拉出来
                            Log.e(tag, "头部被全部下拉出来");
                            changeStatus(STATUS_RELEASE_PULLING_DOWN);
                        } else {
                            //头部开始被拉出来
                            Log.e(tag, "头部开始被拉出来");
                            changeStatus(STATUS_START_PULL_DOWN);
                        }
                    } else {
                        //正在显示底部
                        if (pullDistance >= -pullUpEventDistance) {
                            //底部被全部上拉出来
                            Log.e(tag, "头部被全部下拉出来");
                            changeStatus(STATUS_RELEASE_PULLING_DOWN);
                        } else {
                            //头部开始被拉出来
                            Log.e(tag, "头部开始被拉出来");
                            changeStatus(STATUS_START_PULL_DOWN);
                        }
                    }
                }
                preY = y;
                singlePoint = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pullDistance += (int) (y - preY) / ratio;

                final int headViewTop = headView.getTop();
                if (pullDistance > 0) {
                    //下拉动作
                    if (headViewTop >= 0) {
                        //头部被全部下拉出来
                        Log.e(tag, "头部被全部下拉出来");
                        changeStatus(STATUS_PULLING_DOWN);
                    } else if (headViewTop > -headView.getMeasuredHeight()) {
                        //头部开始被拉出来
                        Log.e(tag, "头部开始被拉出来");
                        changeStatus(STATUS_START_PULL_DOWN);
                    }
                } else {
                    //上提动作
                    final int bottomViewBootm = headView.getBottom();

                }

                preY = y;
                /**
                 * 松开手指后，将View还原到该有的位置
                 */
                restorePosition();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                preY = y;
                singlePoint = false;
                break;
        }

        // 根据下拉距离改变比例
        ratio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (Math.abs(pullDistance))));
        requestLayout();
        return true;
    }

    private void resetEvent() {
        removeCallbacks(restoreRunnable);
    }

    private void changeStatus(int status) {
        if (this.status != status) {
            switch (status) {
                case STATUS_INIT:
                    headTView.setText("");
                    bootomTView.setText("");
                    break;
                case STATUS_START_PULL_DOWN:
                    if (this.status != STATUS_PULLING_DOWN) {
                        headTView.setText("继续下拉刷新");
                        this.status = status;
                    }
                    break;
                case STATUS_RELEASE_PULLING_DOWN:
                    if (this.status != STATUS_PULLING_DOWN) {
                        headTView.setText("松开即可刷新");
                        this.status = status;
                    }
                    break;
                case STATUS_PULLING_DOWN:
                    headTView.setText("正在刷新");
                    this.status = status;
                    break;
                case STATUS_PULLING_UP:
                    bootomTView.setText("正在加载");
                    this.status = status;
                    break;
                case STATUS_PULLED_DOWN:
                    headTView.setText("刷新完成");
                    this.status = status;
                    break;
                case STATUS_PULLED_UP:
                    bootomTView.setText("加载完成");
                    this.status = status;
                    break;
            }
        }
    }

    private float MOVE_SPEED = 8;

    Runnable restoreRunnable = new Runnable() {
        @Override
        public void run() {
            MOVE_SPEED = (float) (8 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * Math.abs(pullDistance)));
            switch (status) {
                case STATUS_PULLING_DOWN: {
                    pullDistance -= MOVE_SPEED;
                    if (pullDistance < pullDownEventDistance) {
                        pullDistance = pullDownEventDistance;
                        removeCallbacks(restoreRunnable);
                    } else {
                        postDelayed(restoreRunnable, 7);
                    }
                    requestLayout();
                }
                break;
                case STATUS_PULLING_UP: {

                }
                break;
                case STATUS_PULLED_DOWN:
                case STATUS_PULLED_UP:
                    break;
            }
        }
    };

    /**
     * 还原位置
     * 比如正在刷新时，上拉下拉松开之后，将还原到正在刷新的位置
     */
    private void restorePosition() {
        if (pullDistance == 0) {
            return;
        }
        post(restoreRunnable);
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
            headTView = headView.findViewById(R.id.headTView);
            bottomView = bottomView.findViewById(R.id.bottomTView);
            pullDownEventDistance = headTView.getMeasuredHeight();
            pullUpEventDistance = bootomTView.getMeasuredHeight();
            isFirstLayout = true;
        }
        final int paddingL = getPaddingLeft();
        final int paddingT = getPaddingTop();
        final int paddingR = getPaddingRight();
        final int paddingB = getPaddingBottom();

        int top = paddingT + pullDistance - headView.getMeasuredHeight();
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
