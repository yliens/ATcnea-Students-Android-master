package uci.atcnea.student.chat;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import uci.atcnea.student.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @class   RV_Chat_Adapter
 * @version 1.0
 * @date 11/07/16
 * @author Adrian Arencibia Herrera
 * Copyright (c) 2016-2017 FORTES, UCI.
 *
 * @description
 * @rf Chat
 */
public class RV_Chat_Adapter extends RecyclerView.Adapter<RV_Chat_Adapter.MSGViewHolder> {


    public List<Msg> msg = new LinkedList<>();
    Context myContext;

    public RV_Chat_Adapter(Context myContext) {
        this.myContext = myContext;
    }

    @Override
    public MSGViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
            MSGViewHolder msgViewHolder = new MSGViewHolder(v);
            return msgViewHolder;
        }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
            MSGViewHolderOther msgViewHolder = new MSGViewHolderOther(v);
            return msgViewHolder;
        }
    }


    @Override
    public void onBindViewHolder(MSGViewHolder holder, int position) {


        if (msg.get(position) instanceof Msg_user) {
//            RelativeLayout.LayoutParams paramsTime =
//                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                            RelativeLayout.LayoutParams.WRAP_CONTENT);
//            paramsTime.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//            paramsTime.addRule(RelativeLayout.BELOW, R.id.card_msg);
//            holder.timestamp.setLayoutParams(paramsTime);
//
//
//            RelativeLayout.LayoutParams params =
//                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//                            RelativeLayout.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//            holder.cv.setLayoutParams(params);
//            holder.cv.setCardBackgroundColor(myContext.getResources().getColor(R.color.bg_msg_user));

            holder.message.setText(msg.get(position).getMSG());




        } else if (msg.get(position) instanceof Msg_other) {

            holder.message.setText(msg.get(position).getMSG());



        } else if (msg.get(position) instanceof Msg_teacher) {

            holder.cv.setCardBackgroundColor(myContext.getResources().getColor(R.color.bg_msg_teacher));
            holder.message.setText(msg.get(position).getMSG());


        }

        holder.timestamp.setText(msg.get(position).getTime());

        Typeface face= Typeface.createFromAsset(myContext.getAssets(), "fonts/icomoon.ttf");
        holder.message.setTypeface(face);

    }

    @Override
    public int getItemViewType(int position) {
        if (msg.get(position) instanceof Msg_user) {
            return 0;

        } else
            return 1;


    }

    @Override
    public int getItemCount() {
        return msg.size();
    }

    public static class MSGViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView message;
        TextView timestamp;
        RelativeLayout myView;


        MSGViewHolder(View itemView) {
            super(itemView);
            myView = (RelativeLayout) itemView;
            cv = (CardView) itemView.findViewById(R.id.card_msg);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }


    }
    public static class MSGViewHolderOther extends MSGViewHolder {
        CardView cv;
        TextView message;
        TextView timestamp;
        RelativeLayout myView;


        MSGViewHolderOther(View itemView) {
            super(itemView);
            myView = (RelativeLayout) itemView;
            cv = (CardView) itemView.findViewById(R.id.card_msg);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);

        }


    }

    public void add(Msg item) {
        msg.add(item);
        notifyItemInserted(msg.size() - 1);
        notifyDataSetChanged();
    }

    public void remove(Msg item) {
        int position = msg.indexOf(item);
        msg.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }


}
