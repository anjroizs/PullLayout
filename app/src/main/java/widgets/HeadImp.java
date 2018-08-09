package widgets;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/2 0002
 */
public interface HeadImp extends PullLayoutImp {
    /**
     * 初始化 或者重置
     */
    void reset();

    /**
     * 开始下拉的过程，还没有到达松手刷新状态
     */
    void startPullDown();

    /**
     * 此时抬起手即头部可以开始加载
     */
    void releaseToLoad();

    /**
     * 头部正在加载
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
}
