package com.example.concord;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Message> list;

    MaterialCardView message_box;

    public RecyclerViewAdapter(Context context, ArrayList<Message> list) {
        this.context = context;
        this.list = list;


    }

    public void addMessage(Message msg){
        list.add(0, msg);
        notifyDataSetChanged();


    }
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        holder.username.setText(list.get(position).getUserEmail());
        holder.message.setText(list.get(position).getMessage());
        holder.dateTime.setText(list.get(position).getDateTime());

        /*
        String email = list.get(0).getUserEmail();
        String nome = MainActivity.nome;

        Log.i("Lista", String.valueOf(position));

        Log.i("username Nome", nome);
        //message_box = findViewById(R.id.caixa);
        if(email.contains(nome)){
            Log.i("message cor", "cinza");
            Log.i("username Email", email);
            message_box.setCardBackgroundColor(Color.GRAY);
        }else if(nome.contains(email)){
            Log.i("username Email", email);
            Log.i("message cor", "cinza");
            //message_box.setCardBackgroundColor(Color.rgb(0,0,0));
        } else{
            Log.i("username Email", email);
            Log.i("message cor", "vermelho");
            message_box.setCardBackgroundColor(Color.rgb(150,0,0));
        }*/

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username,message,dateTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_email);
            message = itemView.findViewById(R.id.user_message);
            dateTime = itemView.findViewById(R.id.user_message_date_time);

            message_box = itemView.findViewById(R.id.caixa);



        }
    }

}
