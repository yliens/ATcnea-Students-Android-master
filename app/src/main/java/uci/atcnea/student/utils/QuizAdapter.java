package uci.atcnea.student.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import uci.atcnea.student.R;
import uci.atcnea.student.activity.QuizActivity;
import uci.atcnea.student.model.ColumnLinkQuestion;
import uci.atcnea.student.model.MultipleChoiceQuestion;
import uci.atcnea.student.model.Question;
import uci.atcnea.student.model.SimpleChoiceQuestion;
import uci.atcnea.student.model.SimpleNumericQuestion;
import uci.atcnea.student.model.TrueFalseQuestion;

/**
 * Created by koyi on 23/05/16.
 */
public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private ArrayList<Question> questions = null;
    private String orientation = null;
    private Context context;
//    private QuizViewHolder mHolder;

    private boolean show_feedback = false;

    public QuizAdapter(ArrayList<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
        show_feedback = false;
        orientation = null;
    }

    public QuizAdapter(ArrayList<Question> questions, Context context, String orientation) {
        this.questions = questions;
        this.context = context;
        this.orientation = orientation;
        show_feedback = false;
    }

    @Override
    public QuizViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        QuizViewHolder qvh = new QuizViewHolder(v);
        return qvh;
    }

    @Override
    public int getItemCount() {
        return questions!=null? (orientation==null ? 0 : 1) + questions.size() :0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(QuizViewHolder holder, int position) {
        //Ocultar los layout q no se usen
        hideLayouts(holder);

        //Mostrar orientacion si existe
        if(orientation != null){
            if(position == 0){
                holder.quiz_orientation.setVisibility(View.VISIBLE);
                holder.quiz_description.loadData(orientation,"text/html","utf-8");
                tranparentColorWebView( holder.quiz_description );
                return;
            }
            position--;
        }

        //Crear las vistas de cada una de las preguntas
        Question question = questions.get(position);
        if (question instanceof MultipleChoiceQuestion) {
            fillMultipleChoiceQuestion(holder, question, position);
        } else if (question instanceof SimpleChoiceQuestion) {
            fillSimpleChoiceQuestion(holder, question, position);
        } else if (question instanceof SimpleNumericQuestion) {
            fillSimpleNumericQuestion(holder, question, position);
        } else if (question instanceof TrueFalseQuestion) {
            fillTrueFalseQuestion(holder, question, position);
        } else if (question instanceof ColumnLinkQuestion) {
            fillColumnLinkQuestion(holder, question, position);
        }

//        Log.e("MY_DEBUG", position + "111");
    }

    /**
     *
     * Oculta la vista pasada por parametros.
     * @param holder view a ocultarse.
     */
    public void hideLayouts(QuizViewHolder holder) {
        holder.quiz_orientation.setVisibility(View.GONE);
        holder.multiple_choice_layout.setVisibility(View.GONE);
        holder.simple_choice_layout.setVisibility(View.GONE);
        holder.simple_numeric_layout.setVisibility(View.GONE);
        holder.true_false_layout.setVisibility(View.GONE);
        holder.column_link_layout.setVisibility(View.GONE);
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void setShow_feedback(boolean show_feedback) {
        this.show_feedback = show_feedback;
    }

    /**
     * Dibujar en la pregunta de seleccion multiple.
     * @param holder Vista a modificarse.
     * @param q Intancia de la pregunta.
     * @param position Posicion de la pregunta en la lista.
     */
    public void fillMultipleChoiceQuestion(final QuizViewHolder holder, Question q, final int position) {
        holder.multiple_choice_layout.setVisibility(View.VISIBLE);
        final MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) q;
        holder.tv_mc.setText("Exercise " + (position + 1) + ": " + mcq.getTitle());

        //Mostrando la orientacion como HTML
        holder.tv_o_mc.loadData( mcq.getOrientation(), "text/html", "utf-8" );
        tranparentColorWebView(holder.tv_o_mc);
        //holder.tv_o_mc.setText( Html.fromHtml(mcq.getOrientation()) );

        //Mostrando el Feedback como HTML
        holder.feedback_mc.loadData( mcq.getFeedback(), "text/html", "utf-8" );
        tranparentColorWebView(holder.feedback_mc);
        //holder.feedback_mc.setText( Html.fromHtml(mcq.getFeedback()) );

        //Show Feedback
        if(show_feedback){
            holder.feedback_mc.setVisibility( View.VISIBLE );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_multiple_choice, mcq.getItems());
        holder.list_mc.setAdapter(adapter);
        holder.list_mc.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        int h = getHeight(holder.list_mc);
        if (h != -1) {
            ViewGroup.LayoutParams params = holder.list_mc.getLayoutParams();
            params.height = h;
            holder.list_mc.setLayoutParams(params);
            holder.list_mc.requestLayout();
            holder.multiple_choice_layout.requestLayout();
        }

        holder.list_mc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                SparseBooleanArray sba = holder.list_mc.getCheckedItemPositions();
                int n = holder.list_mc.getCount();
                ArrayList<Boolean> userResponse = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    boolean b = sba.get(i, false);
                    userResponse.add(b);
                }
                mcq.setUserResponse(userResponse);
                questions.set(position, mcq);
            }
        });
    }

    /**
     * Dibujar en la pregunta de seleccion simple.
     * @param holder Vista a modificarse.
     * @param q Intancia de la pregunta.
     * @param position Posicion de la pregunta en la lista.
     */
    public void fillSimpleChoiceQuestion(final QuizViewHolder holder, Question q, final int position) {
        holder.simple_choice_layout.setVisibility(View.VISIBLE);
        final SimpleChoiceQuestion scq = (SimpleChoiceQuestion) q;
        holder.tv_sc.setText("Exercise " + (position + 1) + ": " + scq.getTitle());

        //Mostrando la orientacion como HTML
        holder.tv_o_sc.loadData( scq.getOrientation(), "text/html", "utf-8" );
        tranparentColorWebView(holder.tv_o_sc);
        //holder.tv_o_sc.setText( Html.fromHtml(scq.getOrientation()) );

        //Mostrando el Feedback como HTML
        holder.feedback_sc.loadData( scq.getFeedback(), "text/html", "utf-8" );
        tranparentColorWebView(holder.feedback_sc);
        //holder.feedback_sc.setText( Html.fromHtml(scq.getFeedback()) );

        //Show Feedback
        if(show_feedback){
            holder.feedback_sc.setVisibility( View.VISIBLE );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_single_choice, scq.getItems());
        holder.list_sc.setAdapter(adapter);
        holder.list_sc.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        int h = getHeight(holder.list_sc);
        if (h != -1) {
            ViewGroup.LayoutParams params = holder.list_sc.getLayoutParams();
            params.height = h;
            holder.list_sc.setLayoutParams(params);
            holder.list_sc.requestLayout();
            holder.simple_choice_layout.requestLayout();
        }

        holder.list_sc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                SparseBooleanArray sba = holder.list_sc.getCheckedItemPositions();
                int n = holder.list_sc.getCount();
                ArrayList<Boolean> userResponse = new ArrayList<Boolean>();
                for (int i = 0; i < n; i++) {
                    boolean b = sba.get(i, false);
                    userResponse.add(b);
                }
                scq.setUserResponse(userResponse);
                questions.set(position, scq);
            }
        });
    }

    /**
     * Dibujar en la pregunta de seleccion numerica simple.
     * @param holder Vista a modificarse.
     * @param q Intancia de la pregunta.
     * @param position Posicion de la pregunta en la lista.
     */
    public void fillSimpleNumericQuestion(final QuizViewHolder holder, Question q, final int position) {
        holder.simple_numeric_layout.setVisibility(View.VISIBLE);
        final SimpleNumericQuestion snq = (SimpleNumericQuestion) q;
        holder.tv_sn.setText("Exercise " + (position + 1) + ": " + snq.getTitle());

        //Mostrando la orientacion como HTML
        holder.tv_o_sn.loadData( snq.getOrientation(), "text/html", "utf-8" );
        tranparentColorWebView(holder.tv_o_sn);
        //holder.tv_o_sn.setText( Html.fromHtml(snq.getOrientation()) );

        //Mostrando el Feedback como HTML
        holder.feedback_sn.loadData( snq.getFeedback(), "text/html", "utf-8" );
        tranparentColorWebView(holder.feedback_sn);
        //holder.feedback_sn.setText( Html.fromHtml(snq.getFeedback()) );

        //Show Feedback
        if(show_feedback){
            holder.feedback_sn.setVisibility( View.VISIBLE );
        }

        holder.et_sn.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String answ = holder.et_sn.getText().toString();
                snq.setUserResponse(Integer.parseInt(answ));
                questions.set(position, snq);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    /**
     * Dibujar en la pregunta de verdadero y falso.
     * @param holder Vista a modificarse.
     * @param q Intancia de la pregunta.
     * @param position Posicion de la pregunta en la lista.
     */
    public void fillTrueFalseQuestion(final QuizViewHolder holder, Question q, final int position) {
        holder.true_false_layout.setVisibility(View.VISIBLE);

        final TrueFalseQuestion tfq = (TrueFalseQuestion) q;
        holder.tv_tf.setText("Exercise " + (position + 1) + ": " + tfq.getTitle());

        //Mostrando la orientacion como HTML
        //holder.tv_o_tf.setText( Html.fromHtml(tfq.getOrientation()) );
        holder.tv_o_tf.loadData(tfq.getOrientation(),"text/html","utf-8");
        tranparentColorWebView(holder.tv_o_tf);

        Log.d( "TrueFalse", tfq.getOrientation() );

        //Mostrando el Feedback como HTML
        holder.feedback_tf.loadData(tfq.getFeedback(),"text/html","UTF-8");
        tranparentColorWebView(holder.feedback_tf);

        //Show Feedback
        if(show_feedback){
            holder.feedback_tf.setVisibility( View.VISIBLE );
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_single_choice, tfq.getItems());
        holder.list_tf.setAdapter(adapter);
        holder.list_tf.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        int h = getHeight(holder.list_tf);
        if (h != -1) {
            ViewGroup.LayoutParams params = holder.list_tf.getLayoutParams();
            params.height = h;
            holder.list_tf.setLayoutParams(params);
            holder.list_tf.requestLayout();
            holder.true_false_layout.requestLayout();
        }

        holder.list_tf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                int k = holder.list_tf.getCheckedItemPosition();
                tfq.setUserResponse(tfq.getItems().get(k).toLowerCase());
                questions.set(position, tfq);
            }
        });
    }

    static ColumnLinkQuestion clq;

    /**
     * Dibujar en la pregunta de enlazar las columnas.
     * @param holder Vista a modificarse.
     * @param q Intancia de la pregunta.
     * @param position Posicion de la pregunta en la lista.
     */
    public void fillColumnLinkQuestion(final QuizViewHolder holder, Question q, final int position) {
        holder.column_link_layout.setVisibility(View.VISIBLE);
        clq = (ColumnLinkQuestion) q;
        holder.tv_cl.setText("Exercise " + (position + 1) + ": " + clq.getTitle());

        //Mostrando la orientacion como HTML
        holder.tv_o_cl.loadData( clq.getOrientation(), "text/html", "utf-8" );
        tranparentColorWebView(holder.tv_o_cl);
        //holder.tv_o_cl.setText( Html.fromHtml(clq.getOrientation()) );

        //Mostrando el Feedback como HTML
        holder.feedback_cl.loadData( clq.getFeedback(), "text/html", "utf-8" );
        tranparentColorWebView(holder.feedback_cl);
        //holder.feedback_cl.setText( Html.fromHtml(clq.getFeedback()) );

        //Show Feedback
        if(show_feedback){
            holder.feedback_cl.setVisibility( View.VISIBLE );
        }

        holder.connect.animateIn();
        holder.connect.clearConnected();
        holder.connect.setListItems(clq.getItemsA(), clq.getItemsB());

        holder.connect.setOnViewDrawedListener(new ConnectPatternView.OnViewDrawedListener() {
            @Override
            public void onDrawedFinish() {
                int h = holder.connect.getMaxHeight();
                ViewGroup.LayoutParams params = holder.connect.getLayoutParams();
                params.height = h;
                holder.connect.setLayoutParams(params);
                holder.connect.requestLayout();
                holder.column_link_layout.requestLayout();
            }

            @Override
            public void onConnectionChanged() {
                ArrayList<Integer> userResponse = holder.connect.getUserResponse();
                clq.setUserResponse(userResponse);
                questions.set(position, clq);
            }
        });

        holder.connect.setOnTouchDrawListener(new ConnectPatternView.OnTouchDrawListener() {
            @Override
            public void onTouchDrawDown() {
                Log.e("MY_DEBUG", "Touch Down");
                QuizActivity.canScroll = false;
            }

            @Override
            public void onTouchDrawUP() {
                Log.e("MY_DEBUG", "Touch Up");
              QuizActivity.canScroll = true;
            }
        });


//        Paint paint = new Paint();
//        paint.setColor(Color.parseColor("#CD5C5C"));
//        Bitmap bg = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bg);
//        canvas.drawLine(50, 50, 100, 100, paint);
//        holder.drawer_layout.setBackgroundDrawable(new BitmapDrawable(bg));

    }

    private int getHeight(ListView list) {
        ListAdapter listAdapter = list.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            // Get total height of all items.
            int totalItemsHeight = numberOfItems * 48;
//            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
//                View item = listAdapter.getView(itemPos, null, list);
////                item.measure(0, 0);
////                totalItemsHeight += item.getMeasuredHeight();
//                totalItemsHeight += 48;
//
//            }
            int totalDividersHeight = list.getDividerHeight() * (numberOfItems - 1);

            return totalItemsHeight + totalDividersHeight;
        }
        return -1;
    }

    /**
     * Clase para las vistas de las preguntas, enlaza con: [quiz_item.xml]
     */
    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout multiple_choice_layout;
        public TextView tv_mc;
        public WebView tv_o_mc;
        public WebView feedback_mc;
        public ListView list_mc;

        public LinearLayout simple_choice_layout;
        public TextView tv_sc;
        public WebView tv_o_sc;
        public WebView feedback_sc;
        public ListView list_sc;

        public LinearLayout simple_numeric_layout;
        public TextView tv_sn;
        public WebView tv_o_sn;
        public WebView feedback_sn;
        public EditText et_sn;

        public LinearLayout true_false_layout;
        public TextView tv_tf;
        public WebView tv_o_tf;
        public WebView feedback_tf;
        public ListView list_tf;

        public LinearLayout column_link_layout;
        public TextView tv_cl;
        public WebView tv_o_cl;
        public WebView feedback_cl;
        public ConnectPatternView connect;

        public LinearLayout quiz_orientation;
        public WebView quiz_description;

        public QuizViewHolder(View view) {
            super(view);

            quiz_orientation = (LinearLayout) view.findViewById( R.id.quiz_orientation );
            quiz_description = (WebView) view.findViewById(R.id.quiz_description_webview);

            multiple_choice_layout = (LinearLayout) view.findViewById(R.id.multiple_choice_layout);
            tv_mc = (TextView) view.findViewById(R.id.tv_question_mc);
            tv_o_mc = (WebView) view.findViewById(R.id.tv_orientation_mc);
            list_mc = (ListView) view.findViewById(R.id.list_answer_mc);
            feedback_mc = (WebView) view.findViewById(R.id.simple_list_item_multiple_text);
            feedback_mc.setVisibility( View.GONE );

            simple_choice_layout = (LinearLayout) view.findViewById(R.id.simple_choice_layout);
            tv_sc = (TextView) view.findViewById(R.id.tv_question_sc);
            tv_o_sc = (WebView) view.findViewById(R.id.tv_orientation_sc);
            list_sc = (ListView) view.findViewById(R.id.list_answer_sc);
            feedback_sc = (WebView) view.findViewById(R.id.simple_list_item_single_text);
            feedback_sc.setVisibility( View.GONE );

            simple_numeric_layout = (LinearLayout) view.findViewById(R.id.simple_numeric_layout);
            tv_sn = (TextView) view.findViewById(R.id.tv_question_sn);
            tv_o_sn = (WebView) view.findViewById(R.id.tv_orientation_sn);
            et_sn = (EditText) view.findViewById(R.id.number_answer_sn);
            feedback_sn = (WebView) view.findViewById(R.id.number_answer_text);
            feedback_sn.setVisibility( View.GONE );

            true_false_layout = (LinearLayout) view.findViewById(R.id.true_false_layout);
            tv_tf = (TextView) view.findViewById(R.id.tv_question_tf);
            //tv_o_tf = (TextView) view.findViewById(R.id.tv_orientation_tf);
            tv_o_tf = (WebView) view.findViewById(R.id.tv_orientation_tf);
            list_tf = (ListView) view.findViewById(R.id.list_answer_tf);
            //feedback_tf = (TextView) view.findViewById(R.id.list_answer_text);
            feedback_tf = (WebView) view.findViewById(R.id.list_answer_text);
            feedback_tf.setVisibility( View.GONE );

            column_link_layout = (LinearLayout) view.findViewById(R.id.column_link_layout);
            tv_cl = (TextView) view.findViewById(R.id.tv_question_cl);
            tv_o_cl = (WebView) view.findViewById(R.id.tv_orientation_cl);
            connect = (ConnectPatternView) view.findViewById(R.id.connect);
            feedback_cl = (WebView) view.findViewById(R.id.connect_text);
            feedback_cl.setVisibility( View.GONE );
        }
    }

    /**
     * Para hacer transparente el fondo de los WebView
     * @param view Elemento que se transformara.
     */
    private static void tranparentColorWebView(WebView view){
        view.setBackgroundColor(Color.TRANSPARENT);
    }

}
