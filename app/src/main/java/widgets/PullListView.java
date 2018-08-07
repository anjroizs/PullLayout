package widgets;

import android.content.Context;
import android.util.AttributeSet;
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
}
