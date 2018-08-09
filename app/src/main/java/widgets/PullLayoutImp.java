package widgets;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/9 0009
 */
public interface PullLayoutImp {
    int STATUS_UNKNOWN = 0;
    /**
     * 初始状态（头部和底部都隐藏)
     */
    int STATUS_INIT = 1;
    /**
     * 继续下拉（头部开始下拉出来）
     */
    int STATUS_START_PULL_DOWN = 2;
    /**
     * 松开即可刷新（头部全部下拉出来）
     */
    int STATUS_RELEASE_PULLING_DOWN = 3;
    /**
     * 正在刷新
     */
    int STATUS_PULLING_DOWN = 4;
    /**
     * 完成刷新
     */
    int STATUS_PULLED_DOWN_SUCCESS = 5;
    /**
     * 完成刷新
     */
    int STATUS_PULLED_DOWN_FAILED = 6;
    /**
     * 松开即可加载
     */
    int STATUS_RELEASE_PULLING_UP = 7;
    /**
     * 正在加载（底部全部显示）
     */
    int STATUS_PULLING_UP = 8;
    /**
     * 完成加载
     */
    int STATUS_PULLED_UP_SUCCESS = 9;
    /**
     * 完成加载
     */
    int STATUS_PULLED_UP_FAILED = 10;
    /**
     * 完成加载，失败后是否显示失败原因
     */
    int STATUS_PULLED_UP_FAILED_SHOW = 11;
    /**
     * 业务操作完成
     */
    int STATUS_DONE = 12;

    void changeStatus(int status);
}
