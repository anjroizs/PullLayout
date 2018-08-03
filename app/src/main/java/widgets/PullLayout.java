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
 * @Description 一个实现下拉刷新，上拉加载的布局文件；
 * 1.可自定义HeadView和BottomView
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
    private TextView bottomTView;
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
     * 正值为整体下移，负值为整体上移
     */
    private int pullDistance = 0;
    /**
     * 触发下拉事件的高度
     */
    private int pullDownEventHeight = 0;
    /**
     * 触发上拉事件的高度
     */
    private int pullUpEventHeight = 0;
    /**
     * 阻尼效果
     * 手势的下拉距离同实际layout距离比例。
     */
    private float ratio = 1;
    private static final int STATUS_UNKNOWN = 0;
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
    private int status = STATUS_UNKNOWN;

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
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preY = y;
                singlePoint = true;
                resetEvent();
                break;
            case MotionEvent.ACTION_MOVE:
                if (singlePoint) {
                    pullDistance += (int) (y - preY) / ratio;
                    //正在显示头部
                    if (pullDistance > 0) {
                        //非底部正在加载状态
                        if (status != STATUS_PULLING_UP) {
                            if (pullDistance >= pullDownEventHeight) {
                                //头部被全部下拉出来
                                Log.e(tag, "头部被全部下拉出来");
                                changeStatus(STATUS_RELEASE_PULLING_DOWN);
                            } else {
                                //头部开始被拉出来
                                Log.e(tag, "头部开始被拉出来");
                                changeStatus(STATUS_START_PULL_DOWN);
                            }
                        }
                    } else {
                        if (status != STATUS_PULLING_DOWN) {
                            //正在显示底部,如果bottomView露出了一半就可以开始加载数据了
                            if (pullDistance < -pullUpEventHeight / 2) {
                                changeStatus(STATUS_PULLING_UP);
                            }
                        }
                    }
                }
                preY = y;
                singlePoint = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pullDistance += (int) (y - preY) / ratio;
                if (pullDistance > 0) {
                    //下拉动作
                    if (status != STATUS_PULLING_UP) {
                        if (pullDistance >= pullDownEventHeight) {
                            //头部被全部下拉出来
//                        Log.e(tag, "头部被全部下拉出来");
                            changeStatus(STATUS_PULLING_DOWN);
                        } else if (pullDistance > 0) {
                            //头部开始被拉出来,松手时，将状态归置到STATUS_INIT
                            if (status != STATUS_PULLING_DOWN) {
                                changeStatus(STATUS_INIT);
                            }
                        }
                    }
                } else {
                    //上提动作
                    if (status != STATUS_PULLING_DOWN) {
                        //如果bottomView露出了一半就可以开始加载数据了
                        if (pullDistance < -pullUpEventHeight / 2) {
                            changeStatus(STATUS_PULLING_UP);
                        }
                    }
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
                    bottomTView.setText("");
                    this.status = status;
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
                    bottomTView.setText("");
                    this.status = status;
                    break;
                case STATUS_PULLING_UP:
                    bottomTView.setText("正在加载");
                    headTView.setText("");
                    this.status = status;
                    break;
                case STATUS_PULLED_DOWN:
                    headTView.setText("刷新完成");
                    this.status = status;
                    break;
                case STATUS_PULLED_UP:
                    bottomTView.setText("加载完成");
                    this.status = status;
                    break;
            }
        }
    }

    private float MOVE_SPEED = 8;

    Runnable restoreRunnable = new Runnable() {
        @Override
        public void run() {
            MOVE_SPEED = (float) (20 + 5 * Math.tan(Math.PI / 2 / getMeasuredHeight() * Math.abs(pullDistance)));
//            Log.e(tag, "Speed=" + MOVE_SPEED);
            switch (status) {
                //正在刷新时还原
                case STATUS_PULLING_DOWN: {
                    if (pullDistance < 0) {
                        pullDistance += MOVE_SPEED;
                        if (pullDistance > pullUpEventHeight) {
                            pullDistance = pullDownEventHeight;
                            removeCallbacks(restoreRunnable);
                        } else {
                            postDelayed(restoreRunnable, 3);
                        }
                    } else if (pullDistance > 0) {
                        //下移的距离小于头部事件下移高度
                        if (pullDistance < pullDownEventHeight) {
                            pullDistance += MOVE_SPEED;
                            if (pullDistance > pullDownEventHeight) {
                                pullDistance = pullDownEventHeight;
                                removeCallbacks(restoreRunnable);
                            } else {
                                postDelayed(restoreRunnable, 3);
                            }
                        } else {
                            pullDistance -= MOVE_SPEED;
                            if (pullDistance < pullDownEventHeight) {
                                pullDistance = pullDownEventHeight;
                                removeCallbacks(restoreRunnable);
                            } else {
                                postDelayed(restoreRunnable, 3);
                            }
                        }
                    }
                    requestLayout();
                }
                break;
                case STATUS_PULLING_UP: {
                    //整体View处在上拉状态中
                    if (pullDistance < 0) {
                        //上拉距离大于bottomView显示的高度
                        if (pullDistance < -pullUpEventHeight) {
                            pullDistance += MOVE_SPEED;
                            if (pullDistance > -pullUpEventHeight) {
                                pullDistance = -pullUpEventHeight;
                                removeCallbacks(restoreRunnable);
                            } else {
                                postDelayed(restoreRunnable, 3);
                            }
                        } else {
                            pullDistance -= MOVE_SPEED;
                            if (pullDistance < -pullUpEventHeight) {
                                pullDistance = -pullUpEventHeight;
                                removeCallbacks(restoreRunnable);
                            } else {
                                postDelayed(restoreRunnable, 3);
                            }
                        }
                    } else if (pullDistance > 0) {
                        //整体View处在下拉状态中
                        //下拉距离大于bottomView的显示高度,也就是bottomView一部分隐藏在底部屏幕以下
                        pullDistance -= MOVE_SPEED;
                        if (pullDistance < -pullUpEventHeight) {
                            pullDistance = -pullUpEventHeight;
                            removeCallbacks(restoreRunnable);
                        } else {
                            postDelayed(restoreRunnable, 3);
                        }
                    }
                    requestLayout();
                }
                break;
                case STATUS_INIT:
                case STATUS_DONE:
                    if (pullDistance < 0) {
                        pullDistance += MOVE_SPEED;
                        if (pullDistance > 0) {
                            pullDistance = 0;
                            removeCallbacks(restoreRunnable);
                        } else {
                            postDelayed(restoreRunnable, 3);
                        }
                    } else if (pullDistance > 0) {
                        pullDistance -= MOVE_SPEED;
                        if (pullDistance < 0) {
                            pullDistance = 0;
                            removeCallbacks(restoreRunnable);
                        } else {
                            postDelayed(restoreRunnable, 3);
                        }
                    }
                    requestLayout();

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
            bottomTView = bottomView.findViewById(R.id.bottomTView);
            pullDownEventHeight = headView.getMeasuredHeight();
            pullUpEventHeight = bottomView.getMeasuredHeight();
            isFirstLayout = false;
            changeStatus(STATUS_INIT);
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
