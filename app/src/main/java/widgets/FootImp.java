package widgets;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/9 0009
 */
public interface FootImp extends PullLayoutImp {
    /**
     * 初始化 或者重置
     */
    void reset();

    /**
     * 此时抬起手即底部可以开始加载
     */
    void releaseToLoad();

    /**
     * 底部正在加载
     */
    void loading();

    /**
     * 加载成功
     */
    void loadSuccess();

    /**
     * 加载失败
     */
    void loadFailed();

    /**
     * 加载失败并展示
     */
    void loadFailedShow();
}
