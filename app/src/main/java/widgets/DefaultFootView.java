package widgets;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import pull.js.com.pullwidget.R;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/9 0009
 */
public class DefaultFootView extends FrameLayout implements FootImp {
    private TextView footTView;

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.default_foot_view_layout, null);
        footTView = view.findViewById(R.id.defaultFootTView);
        addView(view);
    }

    @Override
    public void reset() {
        footTView.setText("");
    }

    @Override
    public void releaseToLoad() {
        footTView.setText("松开加载");
    }

    @Override
    public void loading() {
        footTView.setText("正在加载");
    }

    @Override
    public void loadSuccess() {
        footTView.setText("");
    }

    @Override
    public void loadFailed() {
        footTView.setText("");
    }

    @Override
    public void loadFailedShow() {
        footTView.setText("加载完成失败SHOW");
    }


    public DefaultFootView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DefaultFootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DefaultFootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DefaultFootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void changeStatus(int status) {

    }
}
