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

/**
 * @author JongSung
 * @Description 一个实现下拉刷新，上拉加载的布局文件；
 * 1.可自定义HeadView和BottomView
 * @date 2018/8/1 0001
 */
public class PullLayout extends ViewGroup {
    public static final String tag = "PullLayout";
    /**
     * 业务是否处理成功
     */
    public static final int SUCCESS = 1;
    public static final int FAILED = 2;
    public static final int FAILED_SHOW = 3;

    /**
     * 头部组件
     */
    private View headView;
    /**
     * 底部组件
     */
    private View footView;
    private TextView footTView;
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
    /**
     * 状态
     */
    private int status = PullLayoutImp.STATUS_UNKNOWN;

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

    public void enableFlingToLoadUp(boolean enableFlingToLoadUp) {
        ((Draggable) draggableView).enableFlingToLoadUp(enableFlingToLoadUp);
    }

    public final void finished(int result) {
        int status = PullLayoutImp.STATUS_INIT;
        if (this.status == PullLayoutImp.STATUS_PULLING_DOWN) {
            if (result == SUCCESS) {
                status = PullLayoutImp.STATUS_PULLED_DOWN_SUCCESS;
            } else if (result == FAILED) {
                status = PullLayoutImp.STATUS_PULLED_UP_FAILED;
            } else if (result == FAILED_SHOW) {
                status = PullLayoutImp.STATUS_PULLED_UP_FAILED_SHOW;
            }
        } else if (this.status == PullLayoutImp.STATUS_PULLING_UP) {
            status = PullLayoutImp.STATUS_PULLED_UP_SUCCESS;
            if (result == SUCCESS) {
                status = PullLayoutImp.STATUS_PULLED_UP_SUCCESS;
            } else if (result == FAILED) {
                status = PullLayoutImp.STATUS_PULLED_UP_FAILED;
            }
        }
        changeStatus(status);
        restorePosition();

    }

    private float preY;
    /**
     * 过滤多指操作
     */
    private boolean singlePoint = true;
    private boolean isDispatched = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        Log.e(tag, "dispatchTouchEvent y=" + y);
        isDispatched = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preY = y;
                singlePoint = true;
                resetEvent();
                break;
            case MotionEvent.ACTION_MOVE:
                if (singlePoint && (enableHead || enableFoot)) {
                    calculateDistance(y);
                    //正在显示头部
                    if (pullDistance > 0) {
                        if (((Draggable) draggableView).canPullDown()) {
                            //非底部正在加载状态
                            if (status != PullLayoutImp.STATUS_PULLING_UP) {
                                if (pullDistance >= pullDownEventHeight) {
                                    //头部被全部下拉出来
                                    Log.e(tag, "头部被全部下拉出来" + ((Draggable) draggableView).canPullDown());
                                    changeStatus(PullLayoutImp.STATUS_RELEASE_PULLING_DOWN);
                                } else {
                                    //头部开始被拉出来
                                    Log.e(tag, "头部开始被拉出来" + ((Draggable) draggableView).canPullDown());
                                    changeStatus(PullLayoutImp.STATUS_START_PULL_DOWN);
                                }
                                requestLayout();
                                isDispatched = true;
                                ev.setAction(MotionEvent.ACTION_CANCEL);
                            }
                        }
                    } else {
                        if (status != PullLayoutImp.STATUS_PULLING_DOWN) {
                            if (pullDistance < 0 && ((Draggable) draggableView).isScrolledToBottom()) {
                                //正在显示底部,如果footView露出了一半就可以开始加载数据了
//                                if (pullDistance < -pullUpEventHeight / 2) {
                                changeStatus(PullLayoutImp.STATUS_PULLING_UP);
//                                } else {
//                                    changeStatus(PullLayoutImp.STATUS_RELEASE_PULLING_UP);
//                                }
                                requestLayout();
                                isDispatched = true;
                                ev.setAction(MotionEvent.ACTION_CANCEL);
                            }

                        }
                    }
                }
                if (!isDispatched) {
                    pullDistance = 0;
                }
                singlePoint = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                calculateDistance(y);
                if (pullDistance > 0) {
                    //下拉动作
                    if (status != PullLayoutImp.STATUS_PULLING_UP) {
                        if (pullDistance >= pullDownEventHeight) {
                            //头部被全部下拉出来
                            Log.e(tag, "头部被全部下拉出来" + ((Draggable) draggableView).canPullDown());
                            if (((Draggable) draggableView).canPullDown()) {
                                changeStatus(PullLayoutImp.STATUS_PULLING_DOWN);
                                restorePosition();
                            }
                        } else if (pullDistance > 0) {
                            //头部开始被拉出来,松手时，将状态归置到PullLayoutImp.STATUS_INIT
                            Log.e(tag, "头部开始被拉出来" + ((Draggable) draggableView).canPullDown());
                            if (status != PullLayoutImp.STATUS_PULLING_DOWN) {
                                if (((Draggable) draggableView).canPullDown()) {
                                    changeStatus(PullLayoutImp.STATUS_INIT);
                                    restorePosition();
                                }
                            }
                        }
                    }
                } else {
                    //上提动作
                    if (status != PullLayoutImp.STATUS_PULLING_DOWN) {
                        if (((Draggable) draggableView).isScrolledToBottom() && pullDistance < 0) {
                            changeStatus(PullLayoutImp.STATUS_PULLING_UP);
                            restorePosition();
                        }
                    }
                }
                /**
                 * 松开手指后，将View还原到该有的位置
                 */
//                restorePosition();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                preY = y;
                singlePoint = false;
                requestLayout();
                break;
        }

//        // 根据下拉距离改变比例
//        ratio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (Math.abs(pullDistance))));
        super.dispatchTouchEvent(ev);
        return true;
    }


    private void resetEvent() {
        removeCallbacks(restoreRunnable);
    }

    private void calculateDistance(float y) {
        pullDistance += (int) (y - preY) / ratio;
        preY = y;
        Log.e(tag, "calculateDistance y=" + y + "---preY=" + preY + "--pullDistance=" + pullDistance);
        // 根据下拉距离改变比例
        ratio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight() * (Math.abs(pullDistance))));
    }

    private void changeStatus(int status) {
        if (this.status != status) {
            switch (status) {
                case PullLayoutImp.STATUS_INIT:
                    ((HeadImp) headView).reset();
                    ((FootImp) footView).reset();
                    this.status = status;
                    break;
                case PullLayoutImp.STATUS_START_PULL_DOWN:
                    if (this.status != PullLayoutImp.STATUS_PULLING_DOWN) {
                        ((HeadImp) headView).startPullDown();
                        this.status = status;
                    }
                    break;
                case PullLayoutImp.STATUS_RELEASE_PULLING_DOWN:
                    if (this.status != PullLayoutImp.STATUS_PULLING_DOWN) {
                        ((HeadImp) headView).releaseToLoad();
                        this.status = status;
                    }
                    break;
                case PullLayoutImp.STATUS_PULLING_DOWN:
                    ((HeadImp) headView).loading();
                    ((FootImp) footView).reset();
                    if (onPullListener != null) {
                        onPullListener.onPullDown();
                    }
                    this.status = status;
                    break;
                case PullLayoutImp.STATUS_PULLING_UP:
                    ((FootImp) footView).loading();
                    ((HeadImp) headView).reset();
                    if (onPullListener != null) {
                        onPullListener.onPullUp();
                    }
                    this.status = status;
                    break;
                case PullLayoutImp.STATUS_RELEASE_PULLING_UP:
                    ((FootImp) footView).releaseToLoad();
                    ((HeadImp) headView).reset();
                    this.status = status;
                    break;
                case PullLayoutImp.STATUS_PULLED_DOWN_SUCCESS:
                    ((HeadImp) headView).loadSuccess();
                    this.status = status;
                    break;
                case PullLayoutImp.STATUS_PULLED_DOWN_FAILED:
                    ((HeadImp) headView).loadFailed();
                    this.status = status;
                    break;
                case PullLayoutImp.STATUS_PULLED_UP_SUCCESS:
                    ((FootImp) footView).loadSuccess();
                    ((Draggable) draggableView).emerge(pullUpEventHeight);
                    this.status = status;
                case PullLayoutImp.STATUS_PULLED_UP_FAILED:
                    ((FootImp) footView).loadFailed();
                    this.status = status;
                    break;
                case PullLayoutImp.STATUS_PULLED_UP_FAILED_SHOW:
                    ((FootImp) footView).loadFailedShow();
                    this.status = status;
                    break;
            }
            ((PullLayoutImp) headView).changeStatus(status);
            ((PullLayoutImp) footView).changeStatus(status);
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
                case PullLayoutImp.STATUS_PULLING_DOWN: {
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
                case PullLayoutImp.STATUS_PULLING_UP: {
                    //整体View处在上拉状态中
                    if (pullDistance < 0) {
                        //上拉距离大于footView显示的高度
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
                        //下拉距离大于footView的显示高度,也就是footView一部分隐藏在底部屏幕以下
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
                case PullLayoutImp.STATUS_INIT:
                case PullLayoutImp.STATUS_DONE:
                case PullLayoutImp.STATUS_PULLED_DOWN_SUCCESS:
                case PullLayoutImp.STATUS_PULLED_DOWN_FAILED:
                case PullLayoutImp.STATUS_PULLED_UP_FAILED:
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
                case PullLayoutImp.STATUS_PULLED_UP_SUCCESS:
                    pullDistance = 0;
                    removeCallbacks(restoreRunnable);
                    requestLayout();
                    break;
                case PullLayoutImp.STATUS_PULLED_UP_FAILED_SHOW:
                    pullDistance = -pullUpEventHeight;
                    removeCallbacks(restoreRunnable);
                    requestLayout();
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
                throw new RuntimeException("The PullLayout must have 3 children witch include headChild,draggableChild and footChild.");
            }
            headView = getChildAt(0);
            if (!(headView instanceof HeadImp)) {
                throw new RuntimeException("The HeadChild must implement the interface named HeadImp");
            }
            draggableView = getChildAt(1);
            if (!(draggableView instanceof Draggable)) {
                throw new RuntimeException("The draggableChild must implement the interface named Draggable.");
            }
            footView = getChildAt(2);
            ((Draggable) draggableView).init(this);
            pullDownEventHeight = headView.getMeasuredHeight();
            pullUpEventHeight = footView.getMeasuredHeight();
            isFirstLayout = false;
            changeStatus(PullLayoutImp.STATUS_INIT);
            enableFlingToLoadUp(true);
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
        bottom = top + footView.getMeasuredHeight();
        footView.layout(paddingL, top, footView.getMeasuredWidth() - paddingR, bottom);
    }

    public final void loadWhenFlingToBottom() {
        if (status != PullLayoutImp.STATUS_PULLING_DOWN
                || status != PullLayoutImp.STATUS_PULLING_UP) {
            resetEvent();
            changeStatus(PullLayoutImp.STATUS_PULLING_UP);
            if (pullDistance == 0) {
                pullDistance = -1;
            }
            restorePosition();
        }
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

    private OnPullListener onPullListener;

    public void setOnPullListener(OnPullListener onPullListener) {
        this.onPullListener = onPullListener;
    }

    public interface OnPullListener {
        void onPullDown();

        void onPullUp();
    }
}
