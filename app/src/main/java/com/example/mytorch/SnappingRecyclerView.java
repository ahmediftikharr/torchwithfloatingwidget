package com.example.mytorch;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SnappingRecyclerView extends RecyclerView {
    private static final int MINIMUM_SCROLL_EVENT_OFFSET_MS = 20;
    private ChildViewMetrics _childViewMetrics;
    private long _lastScrollTime;
    private OnViewSelectedListener _listener;
    private Orientation _orientation;
    private boolean _scaleViews;
    /* access modifiers changed from: private */
    public int _scrollState;
    /* access modifiers changed from: private */
    public boolean _scrolling;
    private int _selectedPosition;
    /* access modifiers changed from: private */
    public boolean _userScrolling;
    private Handler mHandler;
    public LinearLayoutManager mLinearLayoutManager;
    private boolean scrolling;

    private static class ChildViewMetrics {
        private Orientation _orientation;

        public ChildViewMetrics(Orientation orientation) {
            this._orientation = orientation;
        }

        public int size(View view) {
            if (this._orientation == Orientation.VERTICAL) {
                return view.getHeight();
            }
            return view.getWidth();
        }

        public float location(View view) {
            if (this._orientation == Orientation.VERTICAL) {
                return view.getY();
            }
            return view.getX();
        }

        public float center(View view) {
            return location(view) + ((float) (size(view) / 2));
        }
    }

    public interface OnViewSelectedListener {
        void onSelected(View view, int i);
    }

    public enum Orientation {
        HORIZONTAL(0),
        VERTICAL(1);
        
        int value;

        private Orientation(int i) {
            this.value = i;
        }

        public int intValue() {
            return this.value;
        }
    }

    public SnappingRecyclerView(Context context) {
        this(context, null);
    }

    public SnappingRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SnappingRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this._userScrolling = false;
        this._scrolling = false;
        this._scrollState = 0;
        this._lastScrollTime = 0;
        this.mHandler = new Handler();
        this._scaleViews = false;
        this._orientation = Orientation.HORIZONTAL;
        init();
    }

    private void init() {
        setHasFixedSize(true);
        setOrientation(this._orientation);
        enableSnapping();
    }

    public void onChildAttachedToWindow(View view) {
        super.onChildAttachedToWindow(view);
        if (!this.scrolling && this._scrollState == 0) {
            this.scrolling = true;
            scrollToView(getCenterView());
            updateViews();
        }
    }

    public boolean fling(int i, int i2) {
        double d = (double) i;
        Double.isNaN(d);
        return super.fling((int) (d * 0.05d), i2);
    }

    private void enableSnapping() {
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (VERSION.SDK_INT < 16) {
                    SnappingRecyclerView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    SnappingRecyclerView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        addOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                SnappingRecyclerView.this.updateViews();
                super.onScrolled(recyclerView, i, i2);
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 1) {
                    if (!SnappingRecyclerView.this._scrolling) {
                        SnappingRecyclerView.this._userScrolling = true;
                    }
                } else if (i == 0) {
                    if (SnappingRecyclerView.this._userScrolling) {
                        SnappingRecyclerView snappingRecyclerView = SnappingRecyclerView.this;
                        snappingRecyclerView.scrollToView(snappingRecyclerView.getCenterView());
                    }
                    SnappingRecyclerView.this._userScrolling = false;
                    SnappingRecyclerView.this._scrolling = false;
                    if (SnappingRecyclerView.this.getCenterView() != null) {
                        SnappingRecyclerView snappingRecyclerView2 = SnappingRecyclerView.this;
                        if (snappingRecyclerView2.getPercentageFromCenter(snappingRecyclerView2.getCenterView()) > 0.0f) {
                            SnappingRecyclerView snappingRecyclerView3 = SnappingRecyclerView.this;
                            snappingRecyclerView3.scrollToView(snappingRecyclerView3.getCenterView());
                        }
                    }
                    SnappingRecyclerView.this.notifyListener();
                } else if (i == 2) {
                    SnappingRecyclerView.this._scrolling = true;
                }
                SnappingRecyclerView.this._scrollState = i;
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyListener() {
        View centerView = getCenterView();
        int childAdapterPosition = getChildAdapterPosition(centerView);
        OnViewSelectedListener onViewSelectedListener = this._listener;
        if (!(onViewSelectedListener == null || childAdapterPosition == this._selectedPosition)) {
            onViewSelectedListener.onSelected(centerView, childAdapterPosition);
        }
        this._selectedPosition = childAdapterPosition;
    }

    public void setOrientation(Orientation orientation) {
        this._orientation = orientation;
        this._childViewMetrics = new ChildViewMetrics(this._orientation);
        this.mLinearLayoutManager = new LinearLayoutManager(getContext(), this._orientation.intValue(), false);
        setLayoutManager(this.mLinearLayoutManager);
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return this.mLinearLayoutManager;
    }

    public void setOnViewSelectedListener(OnViewSelectedListener onViewSelectedListener) {
        this._listener = onViewSelectedListener;
    }

    public void enableViewScaling(boolean z) {
        this._scaleViews = z;
    }

    /* access modifiers changed from: private */
    public void updateViews() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            setMarginsForChild(childAt);
            if (this._scaleViews) {
                float percentageFromCenter = 1.0f - (getPercentageFromCenter(childAt) * 0.7f);
                childAt.setScaleX(percentageFromCenter);
                childAt.setScaleY(percentageFromCenter);
            }
        }
    }

    private void setMarginsForChild(View view) {
        int i;
        int i2;
        int i3;
        int itemCount = getLayoutManager().getItemCount() - 1;
        int childAdapterPosition = getChildAdapterPosition(view);
        int i4 = 0;
        if (this._orientation == Orientation.VERTICAL) {
            int centerLocation = childAdapterPosition == 0 ? getCenterLocation() : 0;
            i2 = childAdapterPosition == itemCount ? getCenterLocation() : 0;
            i4 = centerLocation;
            i3 = 0;
            i = 0;
        } else {
            i = childAdapterPosition == 0 ? getCenterLocation() : 0;
            i3 = childAdapterPosition == itemCount ? getCenterLocation() : 0;
            i2 = 0;
        }
        if (this._orientation == Orientation.HORIZONTAL && VERSION.SDK_INT >= 17) {
            ((MarginLayoutParams) view.getLayoutParams()).setMarginStart(i);
            ((MarginLayoutParams) view.getLayoutParams()).setMarginEnd(i3);
        }
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            ((MarginLayoutParams) view.getLayoutParams()).setMargins(i3, i4, i, i2);
        } else {
            ((MarginLayoutParams) view.getLayoutParams()).setMargins(i, i4, i3, i2);
        }
        if (VERSION.SDK_INT >= 18 && !view.isInLayout()) {
            view.requestLayout();
        }
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        long currentTimeMillis = System.currentTimeMillis();
        if (this._scrolling && this._scrollState == 1 && currentTimeMillis - this._lastScrollTime < 20) {
            this._userScrolling = true;
        }
        this._lastScrollTime = currentTimeMillis;
        View childClosestToLocation = getChildClosestToLocation((int) (this._orientation == Orientation.VERTICAL ? motionEvent.getY() : motionEvent.getX()));
        if (this._userScrolling || motionEvent.getAction() != 1 || childClosestToLocation == getCenterView()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        scrollToView(childClosestToLocation);
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (getChildClosestToLocation((int) (this._orientation == Orientation.VERTICAL ? motionEvent.getY() : motionEvent.getX())) != getCenterView()) {
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void scrollToPosition(int i) {
        this._childViewMetrics.size(getChildAt(0));
        smoothScrollBy(this._childViewMetrics.size(getChildAt(0)) * i);
    }

    private View getChildClosestToLocation(int i) {
        View view = null;
        if (getChildCount() <= 0) {
            return null;
        }
        int i2 = 9999;
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt = getChildAt(i3);
            int center = ((int) this._childViewMetrics.center(childAt)) - i;
            if (Math.abs(center) < Math.abs(i2)) {
                view = childAt;
                i2 = center;
            }
        }
        return view;
    }

    private boolean isChildCorrectlyCentered(View view) {
        int center = (int) this._childViewMetrics.center(view);
        return center > getCenterLocation() + -10 && center < getCenterLocation() + 10;
    }

    /* access modifiers changed from: private */
    public View getCenterView() {
        return getChildClosestToLocation(getCenterLocation());
    }

    /* access modifiers changed from: private */
    public void scrollToView(View view) {
        if (view != null) {
            stopScroll();
            int scrollDistance = getScrollDistance(view);
            if (scrollDistance != 0) {
                smoothScrollBy(scrollDistance);
            }
        }
    }

    private int getScrollDistance(View view) {
        return ((int) this._childViewMetrics.center(view)) - getCenterLocation();
    }

    /* access modifiers changed from: private */
    public float getPercentageFromCenter(View view) {
        float centerLocation = (float) getCenterLocation();
        float center = this._childViewMetrics.center(view);
        return (Math.max(centerLocation, center) - Math.min(centerLocation, center)) / (centerLocation + ((float) this._childViewMetrics.size(view)));
    }

    private int getCenterLocation() {
        if (this._orientation == Orientation.VERTICAL) {
            return getMeasuredHeight() / 2;
        }
        return getMeasuredWidth() / 2;
    }

    public void smoothScrollBy(int i) {
        if (this._orientation == Orientation.VERTICAL) {
            super.smoothScrollBy(0, i);
        } else {
            super.smoothScrollBy(i, 0);
        }
    }

    public void scrollBy(int i) {
        if (this._orientation == Orientation.VERTICAL) {
            super.scrollBy(0, i);
        } else {
            super.scrollBy(i, 0);
        }
    }

    private void scrollTo(int i) {
        scrollBy(i - getScrollOffset());
    }

    public int getScrollOffset() {
        if (this._orientation == Orientation.VERTICAL) {
            return computeVerticalScrollOffset();
        }
        return computeHorizontalScrollOffset();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mHandler.removeCallbacksAndMessages(null);
    }
}
