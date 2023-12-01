package uci.atcnea.student.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;
import main.command.QuizConnection;
import uci.atcnea.core.networking.SendMessageService;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.R;
import uci.atcnea.student.events.Events;
import uci.atcnea.student.events.GlobalBus;
import uci.atcnea.student.model.Question;
import uci.atcnea.student.model.Quiz;
import uci.atcnea.student.utils.ATcneaUtil;
import uci.atcnea.student.utils.QuizAdapter;
import uci.atcnea.student.utils.TaskHelper;

public class QuizActivity extends AppCompatActivity {

    private TextView name;
    private TextView evaluation;//quiz_avaluation
    private CircleImageView user_image;
    //private TextView description;
    private WebView description;
    private TextView attempts;
    private RecyclerView recycler;
    private LinearLayout loadingLayout;
//    private LinearLayout questionResultLayout;
    private RelativeLayout relativeSend;
    private RelativeLayout relativeExit;
//    private ImageView img_result;
//    private TextView text_result;
    private DilatingDotsProgressBar loading;
    private ImageButton btn_send;
    private ImageButton btn_exit;
    private ImageButton btn_end_attempt;//btn_end_attempt
    private Quiz quiz;

    private QuizAdapter quizAdapter;

    private LinkedList<  ArrayList<Question>  > questions;

    //Imagen de background del tiempo
    public static ImageView time_background;

    public static boolean canScroll = true;

    private static Thread main_thread_for_time = null;

    // Para el tiempo
    private int realTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        MainApp.setCurrentActivity(this);

        registerReceiver(result, new IntentFilter("QUIZ_RESULT"));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Iniciando listado de cuestionarios
        questions = new LinkedList< ArrayList<Question> >();

        //Referencias a los componentes
//        questionResultLayout = (LinearLayout) findViewById(R.id.question_result_layout);
//        questionResultLayout.setVisibility(View.GONE);
        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        loadingLayout.setVisibility(View.GONE);
        loading = (DilatingDotsProgressBar) findViewById(R.id.progress);
//        img_result = (ImageView) findViewById(R.id.img_result);
        btn_end_attempt = (ImageButton) findViewById(R.id.btn_end_attempt);
//        text_result = (TextView) findViewById(R.id.text_result);
        quiz = ATcneaUtil.quiz;
        name = (TextView) findViewById(R.id.quiz_name);
        evaluation = (TextView) findViewById(R.id.quiz_evaluation);

        //Evaluacion por defecto es GONE
        evaluation.setVisibility( View.GONE );

        //Imagen de fondo del tiempo
        time_background = (ImageView)findViewById(R.id.quiz_time_background);

        //Imagen del usuario
        user_image = (CircleImageView) findViewById(R.id.quiz_user_image );
        user_image.setImageURI(Uri.parse(MainApp.getCurrentUser().getImagePath()));

        //Valores iniciales de los componentes
        description = (WebView) findViewById(R.id.quiz_description);
        attempts = (TextView) findViewById(R.id.quiz_attempts);
        name.setText("Name: " + quiz.getName());

        //Descripcion usando formato HTML
        //description.loadData( quiz.getDescription(), "text/html", "utf-8" );
        //description.setBackgroundColor( Color.TRANSPARENT );
        //description.setText( "Description: " + Html.fromHtml(quiz.getDescription()) );

        //Valores iniciales del Recycler
        recycler = (RecyclerView) findViewById(R.id.recycler_view_quiz);
        recycler.setHasFixedSize(true);
        recycler.setVisibility(View.VISIBLE);
        recycler.clearOnScrollListeners();
        recycler.setLayoutManager( new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return canScroll;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        } );
        quizAdapter = new QuizAdapter( null , this, quiz.getDescription());
        recycler.setAdapter( quizAdapter );


        final TextView tv_time = (TextView) findViewById(R.id.tv_time);
        final ProgressBar pb = (ProgressBar) findViewById(R.id.progBar);
        pb.getProgressDrawable().setColorFilter(Color.parseColor("#ff9800"), android.graphics.PorterDuff.Mode.SRC_IN);
        final int totalTime = quiz.getTime() * 60;
        final int max = totalTime * 5;
        pb.setMax(max);

        tv_time.setText("0/" + totalTime + "s");

        //Contenedores de los botones flotantes
        relativeSend = (RelativeLayout) findViewById(R.id.relative_send);
        relativeSend.setVisibility(View.VISIBLE);
        relativeExit = (RelativeLayout) findViewById(R.id.relative_exit);
        relativeExit.setVisibility(View.GONE);

        //Botones flotantes para send y exit
        //Evento del boton enviar
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATcneaUtil.FLAG_INTERACTIVE_QUESTION_RUN = false;
                Log.e("MY_DEBUG", "change flag to false");
                sendQuizResponse();
            }
        });

        //Evento de boton end attempt
        btn_end_attempt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.set( questions.size() - 1, ((QuizAdapter)recycler.getAdapter()).getQuestions());
                init_quiz(false);
            }
        });

        //Evento del boton exit
        btn_exit = (ImageButton) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Para enviar prueba
        // FLAG_INTERACTIVE_QUESTION_SEND = true;

        if(savedInstanceState == null)
            init_quiz(false);
        else {
            realTime = savedInstanceState.getInt("REALTIME", 0);
            init_quiz(savedInstanceState.getBoolean("RUNNING", false));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("RUNNING", main_thread_for_time != null);
        outState.putInt("REALTIME", realTime);

        super.onSaveInstanceState(outState);
    }

    public void init_quiz(final boolean saveIntance){

        //Crear nuevo listado de preguntas
        if(!saveIntance) {
            questions.add( quiz.getQuestions().get(questions.size()) );
        }

        //Activar o desactivar botones send/end_attempt
        if(questions.size() == quiz.getAttempts()){
//            btn_send.setEnabled(true);
//            btn_end_attempt.setEnabled(false);
            btn_send.setVisibility(View.VISIBLE);
            btn_end_attempt.setVisibility(View.GONE);
        }else{
//            btn_send.setEnabled(false);
//            btn_end_attempt.setEnabled(true);
            btn_send.setVisibility(View.GONE);
            btn_end_attempt.setVisibility(View.VISIBLE);
        }

        Log.d("init_quiz",questions.size()+"");

        //Valores del Recycler para reiniciar o dar valores a las preguntas
        ((QuizAdapter)recycler.getAdapter()).setQuestions( questions.getLast() );

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recycler.getAdapter().notifyDataSetChanged();

                //Numero de intentos permitidos en el examen
                attempts.setText(getString(R.string.attempts_default_text) + questions.size() + "/" + quiz.getAttempts());
            }
        });


        //Valores globales para el tiempo en transcurso del cuestionario
        final TextView hours = (TextView) findViewById(R.id.hours);
        final TextView hours_dots = (TextView) findViewById(R.id.hour_dots);
        final TextView seconds = (TextView) findViewById(R.id.seconds);
        final TextView minutes = (TextView) findViewById(R.id.minutes);

        final Handler handler = new Handler();

        //Calculo del tiempo inicial del cuestionario
        if(!saveIntance)
            realTime = quiz.getTime() * 60;

        //System.out.println("TIMEE" + saveIntance + " " + realTime);


        final int hh = (realTime / 3600);
        final int mm = (realTime - (3600 * hh)) / 60;
        final int ss = realTime - (3600 * hh) - (60 * mm);

        //Mostrando tiempo en pantalla
        String cero = "";
        if (hh < 10) cero = "0";
        hours.setText(cero + hh);
        cero = "";
        if (mm < 10) cero = "0";
        minutes.setText(cero + mm);
        cero = "";
        if (ss < 10) cero = "0";
        seconds.setText(cero + ss);

        hours.setText("" + hh);
        minutes.setText("" + mm);
        seconds.setText("" + ss);

        //Ocultar valores que no proceden
        if (hh == 0) {
            hours.setVisibility(View.GONE);
            hours_dots.setVisibility(View.GONE);
        }

        //Para detener intancia previa del tiempo
        if(main_thread_for_time != null){
            main_thread_for_time.interrupt();
        }

        //Hilo para llevar el tiempo del questionario
        main_thread_for_time = new Thread() {

            private int h = hh;
            private int m = mm;
            private int s = ss;

            public boolean FLAG_INTERACTIVE_QUESTION_RUN = true;

            /**
             * Detener la instancia de tiempo
             */
            @Override
            public void interrupt() {
                FLAG_INTERACTIVE_QUESTION_RUN = false;
                //super.interrupt();
            }

            @Override
            public void run() {
                while ((h != 0 || m != 0 || s != 0) && FLAG_INTERACTIVE_QUESTION_RUN) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateTime();
                }
                if (FLAG_INTERACTIVE_QUESTION_RUN) {
                    if (questions.size() >= quiz.getAttempts() && !saveIntance) {
                        Log.d("ERRORRR", "ENVIE 1");
                        sendQuizResponse();
                    } else if(!saveIntance){
                        Log.d("ERRORRR", "ENVIE 2");
                        questions.set(questions.size() - 1, ((QuizAdapter) recycler.getAdapter()).getQuestions());
                        init_quiz(false);
                    }
                }
            }

            /**
             * Para actualizar los valores del tiempo
             */
            public void updateTime() {
                realTime--;
                h = (realTime / 3600);
                m = (realTime - (3600 * h)) / 60;
                s = realTime - (3600 * h) - (60 * m);
                updateView();
            }

            /**
             * Para actualizar la vista
             */
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

        };
        //Comenzar el reloj
        main_thread_for_time.start();

        this.handler = new Handler();

    }

    private Handler handler;

    private void sendQuizResponse() {

        //Detener instancia de tiempo actual
        main_thread_for_time.interrupt();

        //Ocultar elementos para pasar a esperar resultado del cuestionario
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_send.setEnabled(false);

//                relativeSend.setVisibility(View.GONE);
//                relativeExit.setVisibility(View.VISIBLE);

                recycler.setVisibility(View.GONE);
//                questionResultLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                loading.showNow();
            }
        });
        try {//Construir JSON de prueba para enviar al profesor

            questions.set( questions.size() - 1, ((QuizAdapter)recycler.getAdapter()).getQuestions());
            JSONArray json = new JSONArray();
            //Encabesado del test
            JSONObject ids = new JSONObject();
            ids.put("estudiante", quiz.getStudent_id_server() );
            ids.put("test", quiz.getId());
            json.put(ids);

            //Array de intentos
            JSONArray attempts = new JSONArray();

            for(int j=0;j<quiz.getAttempts();j++) {

                //Array de respuestas de las preguntas
                JSONArray items = new JSONArray();

                for (int i = 0; i < questions.get(j).size(); i++) {
                    //Creando objeto de la pregunta
                    JSONObject question_item = new JSONObject();

                    //id de la pregunta
                    question_item.put("id", questions.get(j).get(i).getId());

                    //items de la pregunta
                    JSONArray responseJson = questions.get(j).get(i).getUserResponseJson();

                    //Agregar items al objeto de la pregunta
                    question_item.put("responses", responseJson);

                    //Agregar objeto de pregunta al JSON
                    items.put(question_item);

                }

                //Agregar intenro al array
                attempts.put(items);

            }

            //Agregar intentos de preguntas al JSON
            json.put(attempts);

            Log.e("Responce", "JSON: " + json.toString());

            //Enviar JSON con respuesta del test
            sendQuizConnectionCmd(json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MY_DEBUG", "Error creating/sending JSON");
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loading.isActivated()){
                    relativeSend.setVisibility(View.GONE);
                    relativeExit.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.VISIBLE);
                    loadingLayout.setVisibility(View.GONE);
                    loading.hideNow();
                }
            }
        }, 2000);
    }

    /**
     *
     * Enviar JSON de respuesta de la prueba.
     * @param json JSON a enviarse hacia el servidor como respuesta de la prueba.
     */
    private void sendQuizConnectionCmd(String json) {

        SendMessageService service = new SendMessageService(MainApp.getCurrentServer());
        QuizConnection qc = new QuizConnection(json, QuizConnection.QuizType.RESPONSE);
        service.setCommand(qc);
        service.setWaitForResponse(false);
        TaskHelper.execute(service);

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(result);
        super.onDestroy();
    }

    /**
     * Evento de recibir la respuesta del resultado de la prueba realizada.
     */
    private final BroadcastReceiver result = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("RESULT");

            Log.d("BroadcastReceiver",result);

            //Valores finales del resultado del questionario
            evaluation.setVisibility( View.VISIBLE );
            evaluation.setText( getString(R.string.quiz_evaluation) + ": " + result );

            //Estado de los objetos en la escena
            relativeSend.setVisibility(View.GONE);
            relativeExit.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.GONE);
            loading.hideNow();
//            questionResultLayout.setVisibility(View.GONE);

            //Para actualizar mostrando los feedback
            ((QuizAdapter)recycler.getAdapter()).setShow_feedback(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    quizAdapter.setShow_feedback(true);
                    quizAdapter.notifyDataSetChanged();
                }
            });

        }
    };

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        // Toast.makeText(this, "RESUME", Toast.LENGTH_SHORT).show();

        MainApp.setCurrentActivity(this);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void eventDisconnect(Events.EventDisconect event) {

        //Desconectar los socket

        //Ejecutar acciones
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }
}
