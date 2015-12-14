package jp.ac.it_college.std.ikemen.reachable.company;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptySupportRecyclerView extends RecyclerView {
    private View emptyView;

    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter =  getAdapter();
            if(adapter != null && getEmptyView() != null) {
                if(adapter.getItemCount() == 0) {
                    getEmptyView().setVisibility(View.VISIBLE);
                    EmptySupportRecyclerView.this.setVisibility(View.GONE);
                }
                else {
                    getEmptyView().setVisibility(View.GONE);
                    EmptySupportRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    public EmptySupportRecyclerView(Context context) {
        super(context);
    }

    public EmptySupportRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptySupportRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            adapter.registerAdapterDataObserver(getEmptyObserver());
        }

        getEmptyObserver().onChanged();
    }

    public AdapterDataObserver getEmptyObserver() {
        return emptyObserver;
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
