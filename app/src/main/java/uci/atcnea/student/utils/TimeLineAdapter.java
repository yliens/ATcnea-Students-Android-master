package uci.atcnea.student.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;

/**
 * Created by guillermo on 11/10/16.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineView> {
    private String TAG = this.getClass().getName();

    @Override
    public TimeLineView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.content_item, null);
        return new TimeLineView(view, viewType);
    }

    @Override
    public void onBindViewHolder(TimeLineView holder, int position) {
        holder.mState.setText( MainApp.SumaryList.get(position).getTitle() );
        holder.mState.setChecked(MainApp.SumaryList.get(position).isState());
    }

    @Override
    public int getItemCount() {
        return (MainApp.SumaryList!=null?MainApp.SumaryList.size():0);
    }
}
