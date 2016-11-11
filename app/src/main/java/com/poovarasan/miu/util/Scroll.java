package com.poovarasan.miu.util;

/**
 * Created by poovarasanv on 10/11/16.
 */

import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.adapters.HeaderAdapter;

public abstract class Scroll extends RecyclerView.OnScrollListener {

    private int mPreviousTotal = 5;
    private boolean mLoading = true;
    private int mVisibleThreshold = -1;
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

    private boolean mIsOrientationHelperVertical;
    private OrientationHelper mOrientationHelper;

    private int mCurrentPage = 1;

    private HeaderAdapter mFooterAdapter;

    private RecyclerView.LayoutManager mLayoutManager;

    public Scroll() {
    }

    public Scroll(HeaderAdapter adapter) {
        this.mFooterAdapter = adapter;
    }

    public Scroll(RecyclerView.LayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public Scroll(int visibleThreshold) {
        this.mVisibleThreshold = visibleThreshold;
    }

    public Scroll(RecyclerView.LayoutManager layoutManager, int visibleThreshold) {
        this.mLayoutManager = layoutManager;
        this.mVisibleThreshold = visibleThreshold;
    }

    private int findFirstVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(0, mLayoutManager.getChildCount(), false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    private int findLastVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(recyclerView.getChildCount() - 1, -1, false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    private View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible,
                                     boolean acceptPartiallyVisible) {
        if (mLayoutManager.canScrollVertically() != mIsOrientationHelperVertical
                || mOrientationHelper == null) {
            mIsOrientationHelperVertical = mLayoutManager.canScrollVertically();
            mOrientationHelper = mIsOrientationHelperVertical
                    ? OrientationHelper.createVerticalHelper(mLayoutManager)
                    : OrientationHelper.createHorizontalHelper(mLayoutManager);
        }

        final int start = mOrientationHelper.getStartAfterPadding();
        final int end = mOrientationHelper.getEndAfterPadding();
        final int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = mLayoutManager.getChildAt(i);
            if (child != null) {
                final int childStart = mOrientationHelper.getDecoratedStart(child);
                final int childEnd = mOrientationHelper.getDecoratedEnd(child);
                if (childStart < end && childEnd > start) {
                    if (completelyVisible) {
                        if (childStart >= start && childEnd <= end) {
                            return child;
                        } else if (acceptPartiallyVisible && partiallyVisible == null) {
                            partiallyVisible = child;
                        }
                    } else {
                        return child;
                    }
                }
            }
        }
        return partiallyVisible;
    }




    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mLayoutManager == null)
            mLayoutManager = recyclerView.getLayoutManager();

        int footerItemCount = mFooterAdapter != null ? mFooterAdapter.getAdapterItemCount() : 0;

        if (mVisibleThreshold == -1)
            mVisibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView) - footerItemCount;

        mVisibleItemCount = recyclerView.getChildCount() - footerItemCount;
        mTotalItemCount = mLayoutManager.getItemCount() - footerItemCount;
        mFirstVisibleItem = findFirstVisibleItemPosition(recyclerView);

        if (mLoading) {
            if (mTotalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
        }
        if (!mLoading && (mTotalItemCount - mVisibleItemCount)
                <= (mFirstVisibleItem + mVisibleThreshold)) {

            mCurrentPage++;

            onLoadMore(mCurrentPage);

            mLoading = true;
        }
    }

    public void resetPageCount(int page) {
        mPreviousTotal = 0;
        mLoading = true;
        mCurrentPage = page;
        onLoadMore(mCurrentPage);
    }

    public void resetPageCount() {
        resetPageCount(0);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public int getTotalItemCount() {
        return mTotalItemCount;
    }

    public int getFirstVisibleItem() {
        return mFirstVisibleItem;
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public abstract void onLoadMore(int currentPage);
}