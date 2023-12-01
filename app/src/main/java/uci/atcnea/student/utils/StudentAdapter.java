package uci.atcnea.student.utils;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.dao.User;


/**
 * @class   StudentAdapter
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description  
 * @rf 
 */
public class StudentAdapter extends BaseAdapter {
    List<User> studentList;

    public List<User> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<User> studentList) {
        this.studentList = studentList;
    }

    public void addStudent(User student){
        studentList.add(student);
    }

    @Override
    public int getCount() {
        return studentList!=null?studentList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LinearLayout view=(LinearLayout) MainApp.getCurrentActivity().getLayoutInflater().inflate(R.layout.item_student,null);

        CircleImageView circleImageView=(CircleImageView)view.findViewById(R.id.item_student_profileIcon);
        TextView name=(TextView)view.findViewById(R.id.item_student_name);
        TextView user=(TextView)view.findViewById(R.id.item_student_user);

        circleImageView.setImageURI(Uri.parse(studentList.get(position).getImagePath()));
        name.setText(studentList.get(position).getName().getText());
        user.setText(studentList.get(position).getUsername());
        return view;
    }
}
