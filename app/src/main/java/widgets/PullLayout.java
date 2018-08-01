package widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/1 0001
 */
public class PullLayout extends ViewGroup {
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
     *
     */
    private boolean enableHead = true, enableFoot = true;

    public PullLayout(Context context) {
        super(context);
    }

    public PullLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean isNotFirstLayout = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isNotFirstLayout) {

        } else {
            if (getChildCount() != 3) {
                throw new RuntimeException("The PullLayout must have 3 children witch include headChild,draggableChild and bottomChild.");
            }
            headView = getChildAt(0);
            draggableView = getChildAt(1);
            if (!(draggableView instanceof Draggable)) {
                throw new RuntimeException("The draggableChild must implement the interface named Draggable.");
            }
            bottomView = getChildAt(2);
            isNotFirstLayout = true;
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
}
