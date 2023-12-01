package uci.atcnea.student.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import main.command.SendInteractiveQuestion;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.interfaces.InteractiveQuestionBase;
import uci.atcnea.student.model.InteractiveQuestion;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.TaskHelper;

/**
 * Created by koyi on 26/05/16.
 */
public class IQSimpleChoiceFragment extends Fragment implements InteractiveQuestionBase{

    private InteractiveQuestion question;

    ImageButton btn_send;
    ImageButton btn_exit;
    LinearLayout questionLayout;
    LinearLayout questionResultLayout;
    LinearLayout loadingLayout;
    //ImageView img_result;
    TextView text_result;
    RelativeLayout relativeSend;
    RelativeLayout relativeRead;
    RelativeLayout relativeExit;
    DilatingDotsProgressBar loading;


    public IQSimpleChoiceFragment() {
        this.question = ATcneaUtil.question;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exam_simple_selection_answer, null);

        getActivity().registerReceiver(result, new IntentFilter("INTERACTIVE_QUESTION_RESULT"));

        // EXIT INTERACTIVE QUESTION HELP
        ImageView image_exit = (ImageView) view.findViewById(R.id.ic_interactive_question);
        image_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATcneaUtil.FLAG_INTERACTIVE_QUESTION_RUN = false;
                getActivity().finish();
            }
        });

        questionLayout = (LinearLayout) view.findViewById(R.id.question_layout);
        questionLayout.setVisibility(View.VISIBLE);
        questionResultLayout = (LinearLayout) view.findViewById(R.id.question_result_layout);
        questionResultLayout.setVisibility(View.GONE);
        loadingLayout = (LinearLayout) view.findViewById(R.id.loading_layout);
        loadingLayout.setVisibility(View.GONE);
        loading = (DilatingDotsProgressBar) view.findViewById(R.id.progress);
        //img_result = (ImageView) view.findViewById(R.id.img_result);
        text_result = (TextView) view.findViewById(R.id.text_result);

        final Handler handler = new Handler();
        TextView tv_question = (TextView) view.findViewById(R.id.tv_question);
        tv_question.setText(question.getDescription());

        final TextView hours = (TextView) view.findViewById(R.id.hours);
        final TextView hours_dots = (TextView) view.findViewById(R.id.hour_dots);
        final TextView seconds = (TextView) view.findViewById(R.id.seconds);
        final TextView minutes = (TextView) view.findViewById(R.id.minutes);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_single_choice, question.getItems());
        final ListView list = (ListView) view.findViewById(R.id.list_answer);
        list.setAdapter(adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        relativeSend = (RelativeLayout) view.findViewById(R.id.relative_send);
        relativeSend.setVisibility(View.GONE);
        relativeRead = (RelativeLayout) view.findViewById(R.id.relative_read);
        relativeRead.setVisibility(View.VISIBLE);
        relativeExit = (RelativeLayout) view.findViewById(R.id.relative_exit);
        relativeExit.setVisibility(View.GONE);

        btn_send = (ImageButton) view.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATcneaUtil.FLAG_INTERACTIVE_QUESTION_RUN = false;
                Log.e("MY_DEBUG", "change flag to false");
                SparseBooleanArray sba = list.getCheckedItemPositions();
                int n = list.getCount();
                sendMultipleOrSimpleChoiceAnswer(sba, n);
            }
        });

        btn_exit = (ImageButton) view.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        int readTime = question.getTimeRead();
        final int hh = (int) (readTime / 3600);
        readTime = readTime - (3600 * hh);
        final int mm = (int) (readTime / 60);
        readTime = readTime - (60 * mm);
        final int ss = readTime;
        hours.setText("" + hh);
        minutes.setText("" + mm);
        seconds.setText("" + ss);

        if (hh == 0) {
            hours.setVisibility(View.GONE);
            hours_dots.setVisibility(View.GONE);
        }

        ATcneaUtil.FLAG_INTERACTIVE_QUESTION_RUN = true;
        new Thread(new Runnable() {
            int h = hh;
            int m = mm;
            int s = ss;
            boolean reading = true;

            public void run() {

                while ((h != 0 || m != 0 || s != 0) && ATcneaUtil.FLAG_INTERACTIVE_QUESTION_RUN) {
                    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                    updateTime();
                }
                if (reading)
                    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                if (ATcneaUtil.FLAG_INTERACTIVE_QUESTION_RUN && !reading) {
                    SparseBooleanArray sba = list.getCheckedItemPositions();
                    int n = list.getCount();
                    sendMultipleOrSimpleChoiceAnswer(sba, n);
                }
                if (reading) {
                    reading = false;
                    handler.post(new Runnable() {
                        public void run() {
                            relativeRead.setVisibility(View.GONE);
                            relativeExit.setVisibility(View.GONE);
                            relativeSend.setVisibility(View.VISIBLE);
                        }
                    });
                    int answerTime = question.getTimeAnsw();
                    h = (int) (answerTime / 3600);
                    answerTime = answerTime - (3600 * hh);
                    m = (int) (answerTime / 60);
                    answerTime = answerTime - (60 * mm);
                    s = answerTime;
                    updateView();
                    run();
                }
            }

            public void updateTime() {
                s--;
                if (s < 0) {
                    s = 60;
                    m--;
                    if (m < 0) {
                        m = 60;
                        h--;
                    }
                }
                updateView();
            }

            public void updateView() {
                handler.post(new Runnable() {
                    public void run() {
                        String cero = "";
                        if (h < 10) cero = "0";
                        hours.setText(cero + h);
                        cero = "";
                        if (m < 10) cero = "0";
                        minutes.setText(cero + m);
                        cero = "";
                        if (s < 10) cero = "0";
                        seconds.setText(cero + s);
                    }
                });
            }
        }).start();

        this.handler = new Handler();

        return view;
    }
    private Handler handler;

    private void sendMultipleOrSimpleChoiceAnswer(SparseBooleanArray sba, int n) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_send.setEnabled(false);
                questionLayout.setVisibility(View.GONE);
                questionResultLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                loading.showNow();
            }
        });

        String json = "{\"student\":" + question.getStudent_server_id() + ",";
        json += "\"answers\":[";
        for (int i = 0; i < n; i++) {
            boolean b = sba.get(i, false);
            String response = "{\"response\":" + b + "}";
            if (i < n - 1) response += ",";
            json += response;
        }
        json += "]}";

        Log.e("MY_DEBUG", json);

        sendInteractiveQuestionCmd(json);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loading.isActivated()){
                    relativeSend.setVisibility(View.GONE);
                    relativeRead.setVisibility(View.GONE);
                    relativeExit.setVisibility(View.VISIBLE);
                    questionLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    loading.hideNow();
                }
            }
        }, 2000);

    }

    @Override
    public void sendInteractiveQuestionCmd(String json) {

        SendMessageService service = new SendMessageService(MainApp.getCurrentServer());

        SendInteractiveQuestion siq = new SendInteractiveQuestion(json, SendInteractiveQuestion.TypeInteractiveQuestion.IQ_RESPONSE);
        service.setCommand(siq);
        service.setWaitForResponse(false);
        TaskHelper.execute(service);
    }


    @Override
    public void onDestroyView() {
        getActivity().unregisterReceiver(result);
        super.onDestroyView();
    }

    private final BroadcastReceiver result = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("IQ_EVALUATION")){
                //Resultado de la evaluacion
                String evaluation = intent.getStringExtra("IQ_EVALUATION");

                //Resultado de la pregunta interactiva
                text_result.setText( evaluation );

                questionResultLayout.setVisibility(View.VISIBLE);
            }else{
                // Cuando es rechasada la evaluacion de 1er intento
                String title = MainApp.getIntance().getResources().getString( R.string.dialogFileTitle );
                String text = MainApp.getIntance().getResources().getString( R.string.interactive_question_refuce );;
                ATcneaUtil.showInformationDialog(title, text, true);
            }
            relativeSend.setVisibility(View.GONE);
            relativeRead.setVisibility(View.GONE);
            relativeExit.setVisibility(View.VISIBLE);
            questionLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
            loading.hideNow();
        }
    };

    @Override
    public void ClearFragment() {
        ATcneaUtil.FLAG_INTERACTIVE_QUESTION_RUN = false;
    }
}
