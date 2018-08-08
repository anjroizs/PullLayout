package widgets;

/**
 * @author JongSung
 * @Description
 * @date 2018/8/1 0001
 */
public interface Draggable {
    /**
     * 是否可以下拉
     *
     * @return
     */
    boolean canPullDown();

    /**
     * 是否已经滚动到了底部
     *
     * @return
     */
    boolean isScrolledToBottom();

    /**
     * 上拉时，加载出来数据后，将DraggableView新的内容显露出一部分
     *
     * @param height 显露的高度
     * @return
     */
    void emerge(int height);
}
