package uci.atcnea.student.utils;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;

/**
 * Created by guillermo on 11/10/16.
 */
public class TimeLineView extends RecyclerView.ViewHolder {
    public CheckBox mState;

    public TimeLineView(View itemView, int viewType) {
        super(itemView);

        mState = (CheckBox) itemView.findViewById(R.id.lesson_state);

    }
}
