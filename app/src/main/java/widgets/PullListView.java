package widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/1 0001
 */
public class PullListView extends ListView implements Draggable {
    public PullListView(Context context) {
        super(context);
    }

    public PullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private PullLayout pullLayout;

    @Override
    public void init(PullLayout pullLayout) {
        this.pullLayout = pullLayout;
    }

    @Override
    public boolean canPullDown() {
        if (getFirstVisiblePosition() == 0 && getChildAt(0) != null && getChildAt(0).getTop() >= 0 || getCount() == 0) {
            // 滑到ListView的顶部了
            return true;
        } else
            return false;
    }

    @Override
    public boolean isScrolledToBottom() {
        return getLastVisiblePosition() == getCount() - 1;
    }

    public void emerge(int height) {
        smoothScrollBy(height, 0);
    }

    @Override
    public void enableFlingToLoadUp(boolean flingToLoadUp) {
        if (flingToLoadUp) {
            setOnScrollListener(new MyScrollListener());
        } else {
            setOnScrollListener(null);
        }
    }

    private class MyScrollListener implements OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastItem = firstVisibleItem + visibleItemCount;
            if (lastItem == totalItemCount) {
                View bottomView = view.getChildAt(view.getChildCount() - 1);
                if (bottomView != null) {
                    if (bottomView.getBottom() == view.getBottom()) {
                        pullLayout.loadWhenFlingToBottom();
                    }
                }
            }
        }
    }
}
