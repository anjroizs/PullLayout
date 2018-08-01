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
        return false;
    }

    @Override
    public boolean isScrolledToBottom() {
        return false;
    }
}
