package uci.atcnea.student.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.holder.ImageHolder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.activity.MainActivity;
import main.model.Lesson;

/**
 * @author Adrian Arencibia Herrera
 *         Copyright (c) 2016-2017 FORTES, UCI.
 * @version 1.0
 * @class LessonAdapter
 * @date 11/07/16
 * @description
 * @rf
 */
public class LessonAdapter extends BaseAdapter {

    private List<Lesson> lessons;
    private boolean enabled[];
    private Context context;

    public LessonAdapter(Context context) {
        this.context = context;
        lessons = new LinkedList<>();
    }

    public void offItem(int position){
        enabled[position] = false;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void UpdateEnableList(){
        enabled = new boolean[lessons.size()];
        Arrays.fill(enabled, true);
    }

    @Override
    public int getCount() {
        return lessons.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        return enabled[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        RelativeLayout aulaView = (RelativeLayout) ((MainActivity) context).getLayoutInflater().inflate(R.layout.aula_view, null);
        if (aulaView != null) {
            TextView textViewName = (TextView) aulaView.findViewById(R.id.textViewAulaName);
            textViewName.setText(lessons.get(position).getLessonName());
            textViewName.setVisibility(View.VISIBLE);

            TextView textViewTeacher = (TextView) aulaView.findViewById(R.id.textViewTeacher);
            textViewTeacher.setText(lessons.get(position).getTeacherName());
            textViewTeacher.setVisibility(View.VISIBLE);

            LinearLayout background = (LinearLayout) aulaView.findViewById(R.id.background_lesson);
            if (lessons.get(position).getLessonColor() != null)
                background.setBackgroundColor(Color.parseColor(lessons.get(position).getLessonColor()));

            // ImageView imageView=(ImageView)aulaView.findViewById(R.id.imageView2);
            //imageView.setImageDrawable((MainApp.getCurrentUser().getIcon()).getIcon());


        }


        return aulaView;
    }
}