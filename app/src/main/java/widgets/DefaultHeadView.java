package widgets;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import pull.js.com.pullwidget.R;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/8 0008
 */
public class DefaultHeadView extends FrameLayout implements HeadImp {

    public DefaultHeadView(Context context) {
        super(context);
        init(context);
    }

    public DefaultHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DefaultHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DefaultHeadView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private TextView headTView;

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.default_head_view_layout, null);
        headTView = view.findViewById(R.id.defaultHeadTView);
        addView(view);
    }

    @Override
    public void reset() {
        headTView.setText("");
    }

    @Override
    public void startPullDown() {
        headTView.setText("继续下拉刷新");
    }

    @Override
    public void releaseToLoad() {
        headTView.setText("松开即可刷新");
    }

    @Override
    public void loading() {
        headTView.setText("正在刷新");
    }

    @Override
    public void loadSuccess() {
        headTView.setText("刷新完成成功");
    }

    @Override
    public void loadFailed() {
        headTView.setText("刷新完成失败");

    }

    @Override
    public void changeStatus(int status) {

    }
}
