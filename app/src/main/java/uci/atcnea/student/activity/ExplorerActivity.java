/*
package uci.atcnea.student.activity;

*/
/**
 * Created by koyi on 16/06/16.
 *//*

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import uci.atcnea.student.R;
import uci.atcnea.student.utils.ExplorerAdapter;

public class ExplorerActivity extends AppCompatActivity {

    Comparator<? super File> filecomparator = new Comparator<File>() {

        public int compare(File file1, File file2) {
            if (file1.isDirectory()) {
                if (file2.isDirectory()) {
                    return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                } else {
                    return -1;
                }
            } else {
                if (file2.isDirectory()) {
                    return 1;
                } else {
                    return String.valueOf(file1.getName().toLowerCase()).compareTo(file2.getName().toLowerCase());
                }
            }
        }
    };
    private RecyclerView recycler;
    private ExplorerAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private List<File> items = null;
    private List<String> path = null;
    private String defautl_path;
    private String root_path;
    private String actual_path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explorer_layout);

        defautl_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        actual_path = defautl_path;
        root_path = "/";
        recycler = (RecyclerView) findViewById(R.id.reciclador);
        recycler.setHasFixedSize(true);

        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        adapter = new ExplorerAdapter(defautl_path, getDir(defautl_path));
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int indice = recycler.getChildAdapterPosition(view) - 1;

                if (indice == -1) {
                    if (!actual_path.equals(root_path)) {
                        File f = new File(actual_path);
                        String parent = f.getParent();
                        actual_path = parent;
                        adapter.updateList(parent, getDir(parent));
                    }
                } else {
                    if (items.get(indice).isDirectory()) {
                        actual_path = path.get(indice);
                        adapter.updateList(path.get(indice), getDir(path.get(indice)));
                    } else {
                        String db_path = items.get(indice).getParent() + "/";
//                        DataBaseHelper db = new DataBaseHelper(getApplicationContext(), db_path, items.get(indice).getName(), false);
//                        openDataBase(db);
//                        boolean flag = db.checkDataBaseTables();

//                        if (flag) {
//                            SharedPreferences prefs = getSharedPreferences("I_Preference", Context.MODE_PRIVATE);
//                            final SharedPreferences.Editor editor = prefs.edit();
//                            editor.putString("db_path", db_path);
//                            editor.putString("db_name", items.get(indice).getName());
//                            editor.commit();
//
//                            Intent i = new Intent(ExplorerActivity.this, StartActivity.class);
//                            i.putExtra("OK", "OK");
//                            startActivity(i);
//                            finish();
//                        }else{
//                            String db_error = getString(R.string.db_error);
//                            new AlertDialog.Builder(ExplorerActivity.this).setTitle(db_error).setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            }).create().show();
//                        }

//                    }

                    }
                }
            }
        });
        recycler.setAdapter(adapter);

    }

//    public void openDataBase(DataBaseHelper db) {
//        try {
//            db.createDataBase();
//            db.close();
//            boolean b = db.openReadDataBase();
//            Log.e("MY_DEBIG", b + "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public List getDir(String dirPath) {
        Log.e("MY_DEBUG", dirPath);

        items = new ArrayList();
        path = new ArrayList();
        File f = new File(dirPath);
        File[] files = f.listFiles();

        Arrays.sort(files, filecomparator);

        for (int i = 0; i < files.length; i++) {
            File file = files[i];

            if (!file.isHidden() && file.canRead()) {
                String ex = file.getName();

                if (file.isDirectory()) {
                    items.add(new File(file.getAbsolutePath() + "/"));
                    path.add(file.getAbsolutePath());
                } else{ //if (ex.endsWith(".db") || ex.endsWith(".sqlite") || ex.endsWith(".sqlite3")) {
                    items.add(new File(file.getAbsolutePath()));
                    path.add(file.getAbsolutePath());
                }

            }
        }
        return items;
    }
}

*/
