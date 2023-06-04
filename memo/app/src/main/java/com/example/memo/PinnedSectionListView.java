package com.example.memo;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.MeasureSpec;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 仿虎扑,带悬浮标题的PinnedSectionListView,不帶下拉刷新的自定义listview
 * 具体里面注释已经写得很详细了.有兴趣的童鞋可以看看原理.
 * 如果懒得看的,可以直接复制粘贴直接使用,无须修改这个类
 * @author max.chengdu 2015年11月29日
 *
 */
public class PinnedSectionListView extends ListView {

    /** 适配器 **/
    public static interface PinnedSectionListAdapter extends ListAdapter {
        /** 这个方法会返回一个true,当这个type是钉在最顶上的 **/
        boolean isItemViewTypePinned(int viewType);
    }

    /** 封装的被钉在顶部的部门类 **/
    static class PinnedSection {
        public View view;
        public int postion;
        public long id;
    }

    /** 这些领域被用作处理这些触摸事件 **/
    private final Rect mTouchRect = new Rect();// 绘制一个矩形区域
    private final PointF mTouchPoint = new PointF();// 二维矢量
    private int mTouchSlop;
    private View mTouchTarget;
    private MotionEvent mDownEvent;
    /** 以下这些区域用来绘制钉在顶部的部分的阴影 **/
    private GradientDrawable mShadowDrawable;// 使用渐变色来绘制图形,通常用作button或者背景图形
    private int mSectionsDistanceY;
    private int mShadowHeight;
    /** 滚动监听 **/
    OnScrollListener mDelegateOnScrollListener;
    /** 这个是shadow已经被回收了的顶部view **/
    PinnedSection mRecycleSection;
    /** 这个是顶部实例有shadow的 **/
    PinnedSection mPinnedSection;
    /** 顶部view的Y轴变化距离,我们可以用它去插入上一个顶部view **/
    int mTranslateY;
    /** 初始化滑动监听,制造了这个效果的滑动监听 **/
    private final OnScrollListener mOnScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (mDelegateOnScrollListener != null) {
                mDelegateOnScrollListener.onScrollStateChanged(view,
                        scrollState);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (mDelegateOnScrollListener != null) { // delegate
                mDelegateOnScrollListener.onScroll(view, firstVisibleItem,
                        visibleItemCount, totalItemCount);
            }
            // 获取期望的adapter或者快速下落
            ListAdapter adapter = getAdapter();// 返回当前listview的adapter
            if (adapter == null || visibleItemCount == 0) {// 非空判断
                return;
            }

            final boolean isFirstVisibleItemSection = isItemViewTypePinned(
                    adapter, adapter.getItemViewType(firstVisibleItem));
            // 如果是第一个
            if (isFirstVisibleItemSection) {
                View sectionView = getChildAt(0);
                // 当view插入到顶部的时候,就不需要阴影了
                if (sectionView.getTop() == getPaddingTop()) {
                    destroyPinnedShadow();// 销毁顶部阴影
                } else { // 顶部部门view没有被插入到顶部,确保顶部菜单有阴影
                    ensureShadowForPosition(firstVisibleItem, firstVisibleItem,
                            visibleItemCount);
                }
            } else { // 这个部门并没有在第一个可视的位置
                int sectionPosition = findCurrentSectionPosition(firstVisibleItem);
                if (sectionPosition > -1) {// 我们又这个顶部部门类的位置
                    ensureShadowForPosition(sectionPosition, firstVisibleItem,
                            visibleItemCount);
                } else {// 这里第一个可视item没有部门类,摧毁掉阴影
                    destroyPinnedShadow();
                }
            }
        }
    };
    /** 数据集对象的观察者,当数据发送改变的时候 **/
    private final DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            recreatePinnedShadow();
        };

        @Override
        public void onInvalidated() {
            recreatePinnedShadow();
        };
    };

    /** 构造函数 **/
    public PinnedSectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PinnedSectionListView(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setOnScrollListener(mOnScrollListener);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        initShadow(true);
    }

    /** 对外的公共接口 **/
    public void setShadowVisible(boolean visible) {
        initShadow(visible);
        if (mPinnedSection != null) {
            View v = mPinnedSection.view;
            invalidate(v.getLeft(), v.getTop(), v.getRight(), v.getBottom()
                    + mShadowHeight);
        }
    }

    /** 绘制顶部部门类的方法 **/
    private void initShadow(boolean visible) {
        if (visible) {
            if (mShadowDrawable == null) {// 绘制阴影
                mShadowDrawable = new GradientDrawable(Orientation.TOP_BOTTOM,
                        new int[] { Color.parseColor("#ffa0a0a0"),
                                Color.parseColor("#50a0a0a0"),
                                Color.parseColor("#00a0a0a0") });
                mShadowHeight = (int) (8 * getResources().getDisplayMetrics().density);
            }
        } else {
            if (mShadowDrawable != null) {
                mShadowDrawable = null;
                mShadowHeight = 0;
            }
        }
    }

    /** 判断第一个item是否是部门view **/
    public boolean isItemViewTypePinned(ListAdapter adapter, int itemViewType) {
        if (adapter instanceof HeaderViewListAdapter) {// 当前adapter是否是HeaderViewListAdapter的实例
            adapter = ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        }
        return ((PinnedSectionListAdapter) adapter)
                .isItemViewTypePinned(itemViewType);
    }

    /** 重新创建阴影 **/
    protected void recreatePinnedShadow() {
        destroyPinnedShadow();
        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter.getCount() > 0) {
            int firstVisiblePosition = getFirstVisiblePosition();
            int sectionPosition = findCurrentSectionPosition(firstVisiblePosition);
            if (sectionPosition == -1) {// 没有顶部的view
                return;
            }
            ensureShadowForPosition(sectionPosition, firstVisiblePosition,
                    getLastVisiblePosition() - firstVisiblePosition);
        }
    }

    /** 发现当前部门的位置 **/
    protected int findCurrentSectionPosition(int fromPosition) {
        ListAdapter adapter = getAdapter();

        if (fromPosition >= adapter.getCount()) { // 这个是没货
            return -1;
        }
        if (adapter instanceof SectionIndexer) {
            // 最快去找到顶部部门类的角标
            SectionIndexer indexer = (SectionIndexer) adapter;
            int sectionPosition = indexer.getSectionForPosition(fromPosition);
            int itemPosition = indexer.getPositionForSection(sectionPosition);
            int typeView = adapter.getItemViewType(itemPosition);
            if (isItemViewTypePinned(adapter, typeView)) {
                return itemPosition;
            }
        }

        // 去寻找下一个部门类在全部里面遍历,这是个比较慢的方法
        for (int position = fromPosition; position >= 0; position--) {
            int viewType = adapter.getItemViewType(position);
            if (isItemViewTypePinned(adapter, viewType)) {
                return position;
            }
        }
        return -1;
    }

    /** 确保在给出的位置都有准确的阴影 **/
    protected void ensureShadowForPosition(int sectionPosition,
                                           int firstVisibleItem, int visibleItemCount) {
        // 没有必要创建阴影,我们只有一个可视的item
        if (visibleItemCount < 2) {
            destroyPinnedShadow();
            return;
        }
        if (mPinnedSection != null && mPinnedSection.postion != sectionPosition) {// 如果需要,使阴影无效
            destroyPinnedShadow();
        }
        if (mPinnedSection == null) {// 如果为空,就创建阴影
            createPinnedShadow(sectionPosition);
        }

        // 如果需要,更具下一个部门类把阴影排列整齐
        int nextPosition = sectionPosition + 1;
        if (nextPosition < getCount()) {
            int nextSectionPosition = findFirstVisibleSectionPosition(
                    nextPosition, visibleItemCount
                            - (nextPosition - firstVisibleItem));
            if (nextSectionPosition > -1) {
                View nextSectionView = getChildAt(nextSectionPosition
                        - firstVisibleItem);
                final int bottom = mPinnedSection.view.getBottom()
                        + getPaddingTop();
                mSectionsDistanceY = nextSectionView.getTop() - bottom;
                if (mSectionsDistanceY < 0) {
                    // 下一个部门要与这个阴影重叠的时候,就顶上去
                    mTranslateY = mSectionsDistanceY;
                } else {
                    // 下一个部门并没有与当前这个顶部重复,继续往上滑
                    mTranslateY = 0;
                }
            } else {
                // 如果没有其他部门类可见,就插到顶部
                mTranslateY = 0;
                mSectionsDistanceY = Integer.MAX_VALUE;
            }
        }

    }

    /** 找到第一个可视的部门的位置 **/
    private int findFirstVisibleSectionPosition(int firstVisibleItem,
                                                int visibleItemCount) {
        ListAdapter adapter = getAdapter();

        int adapterDataCount = adapter.getCount();
        if (getLastVisiblePosition() >= adapterDataCount) { // 这个是没有
            return -1;
        }
        if (firstVisibleItem + visibleItemCount >= adapterDataCount) { // 为了防止越界,把这个增加到阻止脚标
            visibleItemCount = adapterDataCount - firstVisibleItem;
        }

        for (int childIndex = 0; childIndex < visibleItemCount; childIndex++) {
            int position = firstVisibleItem + childIndex;
            int viewType = adapter.getItemViewType(position);
            if (isItemViewTypePinned(adapter, viewType)) {
                return position;
            }
        }

        return -1;
    }

    /** 在特定的位置为顶部view创建阴影(只要不在第一个) **/
    private void createPinnedShadow(int position) {
        // 先回收shadow
        PinnedSection pinnedShadow = mRecycleSection;
        mRecycleSection = null;

        // 创建新一个shadow,如果需要
        if (pinnedShadow == null) {
            pinnedShadow = new PinnedSection();
        }
        View pinnedView = getAdapter().getView(position, pinnedShadow.view,
                PinnedSectionListView.this);

        // 获取layout parameters
        LayoutParams layoutParams = (LayoutParams) pinnedView.getLayoutParams();
        if (layoutParams == null) {// 为空,创建系统默认的参数
            layoutParams = (LayoutParams) generateDefaultLayoutParams();
            pinnedView.setLayoutParams(layoutParams);
        }

        int heightMode = MeasureSpec.getMode(layoutParams.height);// MeasureSpec表示一个组件的大小,还有大小模式
        int heightSize = MeasureSpec.getSize(layoutParams.height);

        if (heightMode == MeasureSpec.UNSPECIFIED) {// 未指定大小赋值模式
            heightMode = MeasureSpec.EXACTLY;
        }

        int maxHeight = getHeight() - getListPaddingTop()
                - getListPaddingBottom();// 最大高度
        if (heightSize > maxHeight) {// 超过最大高度
            heightSize = maxHeight;
        }

        // 测量layout
        int ws = MeasureSpec.makeMeasureSpec(getWidth() - getListPaddingLeft()
                - getListPaddingRight(), MeasureSpec.EXACTLY);
        int hs = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        pinnedView.measure(ws, hs);
        pinnedView.layout(0, 0, pinnedView.getMeasuredWidth(),
                pinnedView.getMeasuredHeight());
        mTranslateY = 0; // 此时到顶部的高度为0

        // 初始化顶部view阴影
        pinnedShadow.view = pinnedView;
        pinnedShadow.postion = position;
        pinnedShadow.id = getAdapter().getItemId(position);

        // 保存shadow
        mPinnedSection = pinnedShadow;

    }

    /** 摧毁当前封装的顶部view的阴影 **/
    protected void destroyPinnedShadow() {
        if (mPinnedSection != null) {
            // 保持阴影被销毁状态一段时间
            mRecycleSection = mPinnedSection;
            mPinnedSection = null;
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        if (listener == mOnScrollListener) {
            super.setOnScrollListener(listener);
        } else {
            mDelegateOnScrollListener = listener;
        }
    }

    /** 恢复activity的状态 **/
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        post(new Runnable() {
            @Override
            public void run() { // restore pinned view after configuration
                // change
                recreatePinnedShadow();
            }
        });
    }

    @Override
    public void setAdapter(ListAdapter adapter) {

        // 使adapter在debug的模式下生效
        if (BuildConfig.DEBUG && adapter != null) {
            if (!(adapter instanceof PinnedSectionListAdapter))
                throw new IllegalArgumentException(
                        "Does your adapter implement PinnedSectionListAdapter?");
            if (adapter.getViewTypeCount() < 2)
                throw new IllegalArgumentException(
                        "Does your adapter handle at least two types"
                                + " of views in getViewTypeCount() method: items and sections?");
        }

        // 注销以前的观察者在旧的adapter里面,在新的adapter里面注册新的
        ListAdapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mDataSetObserver);
        }
        if (adapter != null) {
            adapter.registerDataSetObserver(mDataSetObserver);
        }

        // 销毁阴影,如果adapter发生了改变
        if (oldAdapter != adapter) {
            destroyPinnedShadow();
        }

        super.setAdapter(adapter);
    }

    /** 在view给其孩子设置尺寸和位置时被调用 **/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mPinnedSection != null) {
            int parentWidth = r - l - getPaddingLeft() - getPaddingRight();
            int shadowWidth = mPinnedSection.view.getWidth();
            if (parentWidth != shadowWidth) {
                recreatePinnedShadow();
            }
        }
    }

    /** 绘制子组件 **/
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPinnedSection != null) {
            // 准备好可变因素
            int pLeft = getListPaddingLeft();
            int pTop = getListPaddingTop();
            View view = mPinnedSection.view;

            // 绘制子组件
            canvas.save();

            int clipHeight = view.getHeight()
                    + (mShadowDrawable == null ? 0 : Math.min(mShadowHeight,
                    mSectionsDistanceY));
            canvas.clipRect(pLeft, pTop, pLeft + view.getWidth(), pTop
                    + clipHeight);// 画矩形

            canvas.translate(pLeft, pTop + mTranslateY);
            drawChild(canvas, mPinnedSection.view, getDrawingTime());
            drawChild(canvas, mPinnedSection.view, getDrawingTime());

            if (mShadowDrawable != null && mSectionsDistanceY > 0) {
                mShadowDrawable.setBounds(mPinnedSection.view.getLeft(),
                        mPinnedSection.view.getBottom(),
                        mPinnedSection.view.getRight(),
                        mPinnedSection.view.getBottom() + mShadowHeight);// 它是指定一个矩形区域，然后通过draw(Canvas)画的时候，就只在这个矩形区域内画图
                mShadowDrawable.draw(canvas);
            }

            canvas.restore();
        }
    }

    /** 用户滑动监听事件 **/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        final int action = ev.getAction();

        if (action == MotionEvent.ACTION_DOWN && mTouchTarget == null
                && mPinnedSection != null
                && isPinnedViewTouched(mPinnedSection.view, x, y)) { // 创建触点目标

            // 用户触摸到顶部部门类的view
            mTouchTarget = mPinnedSection.view;
            mTouchPoint.x = x;
            mTouchPoint.y = y;

            // 复制下来方便最后使用
            mDownEvent = MotionEvent.obtain(ev);
        }
        if (mTouchTarget != null) {
            if (isPinnedViewTouched(mTouchTarget, x, y)) { // 向前的事件在顶部view上面
                mTouchTarget.dispatchTouchEvent(ev);
            }

            if (action == MotionEvent.ACTION_UP) { // 点击顶部view的click事件
                super.dispatchTouchEvent(ev);
                performPinnedItemClick();
                clearTouchTarget();

            } else if (action == MotionEvent.ACTION_CANCEL) { // 取消
                clearTouchTarget();

            } else if (action == MotionEvent.ACTION_MOVE) {
                if (Math.abs(y - mTouchPoint.y) > mTouchSlop) {

                    // 取消点击顺序
                    MotionEvent event = MotionEvent.obtain(ev);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    mTouchTarget.dispatchTouchEvent(event);
                    event.recycle();

                    // 提供正确的顺给父类
                    super.dispatchTouchEvent(mDownEvent);
                    super.dispatchTouchEvent(ev);
                    clearTouchTarget();

                }
            }

            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean performPinnedItemClick() {
        if (mPinnedSection == null) {
            return false;
        }

        OnItemClickListener listener = getOnItemClickListener();
        if (listener != null && getAdapter().isEnabled(mPinnedSection.postion)) {
            View view = mPinnedSection.view;
            playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            listener.onItemClick(this, view, mPinnedSection.postion,
                    mPinnedSection.id);
            return true;
        }
        return false;
    }

    /** 回收触摸监听 **/
    private void clearTouchTarget() {
        mTouchTarget = null;
        if (mDownEvent != null) {
            mDownEvent.recycle();
            mDownEvent = null;
        }
    }

    /** 判断顶部的view是否被触摸到了 **/
    private boolean isPinnedViewTouched(View view, float x, float y) {
        view.getHitRect(mTouchRect);
        // 我们没有增加到top的padding为了让他行为一致
        mTouchRect.top += mTranslateY;

        mTouchRect.bottom += mTranslateY + getPaddingTop();
        mTouchRect.left += getPaddingLeft();
        mTouchRect.right -= getPaddingRight();
        return mTouchRect.contains((int) x, (int) y);
    }

    //自适应scrollview,避免冲突
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
