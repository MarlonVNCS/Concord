package com.example.concord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public static String nome;
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Message> list;
    TextInputLayout message;
    FloatingActionButton send;
    DatabaseReference db;
    FirebaseAuth auth;
    FirebaseUser user;

    //String nome;
    Boolean msg_valid;
    TextView texto;
    FloatingActionButton copy;

    MaterialCardView message_box;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.fab_send);
        message = findViewById(R.id.message);
        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();

        message_box = findViewById(R.id.caixa);
        texto = findViewById(R.id.user_message);
        copy = findViewById(R.id.copy_button);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        user = auth.getCurrentUser();
        String uId = user.getUid();
        final String[] uEmail = {user.getEmail()};
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());

        nome = setNome(uEmail);

        //message_box = itemView.findViewById(R.id.caixa);

        db = FirebaseDatabase.getInstance().getReference();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message.getEditText().getText().toString();
                msg_valid = checkMsg(msg);
                if (!msg_valid){
                    Toast.makeText(MainActivity.this, "Escreve alguma coisa porra", Toast.LENGTH_SHORT).show();
                } else {
                    //Log.i("mensage",msg);
                    //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    db.child("Messages").push().setValue(new Message(nome, msg, timeStamp)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            message.getEditText().setText("");
                        }
                    });
                }}
        });

        adapter = new RecyclerViewAdapter(this, list);
        LinearLayoutManager llm = new LinearLayoutManager(this,RecyclerView.VERTICAL,true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        /*
        copy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int id = view.getId();

                //ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //ClipData clip = ClipData.newPlainText("EditText",.getText().toString());
                //clipboard.setPrimaryClip(clip);

               Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show();
            }});*/
    }

    //meu codigo
    public void exit(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginSignup.class));
        finish();
    }

    public void copy_text(View view){
        texto = view.findViewById(R.id.user_message);


        texto.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", texto.getText().toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "Mensagem copiada", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        receiveMessages();
    }

    public String setNome(final String[] uEmail){
        String email = uEmail[0];
        nome = "";
        for (int i = 0; i < email.length(); i++) {
            if (email.substring(i,i+1).equals("@")){
                break;
            } else{
                nome = nome + email.substring(i, i+1);
                Log.i("Email",email.substring(i, i+1));
                if(i == 0){
                    nome = nome.toUpperCase();
                    Log.i("Nome",nome);
                }
            }

        }
        if(nome == "Admviado" || nome.contains("admviado") || nome.contains("Admviado"))
            nome = "Lucas pegou a irmã do Eduardo";
        else if (nome.toLowerCase().contains("adm") && nome.toLowerCase().contains("viado"))
            nome = "Lucas pegou a irmã do Eduardo";

        if(nome == "Teste0" || nome.contains("teste0") || nome.contains("Teste0"))
            nome = "ADM trabalhando";

        return nome;
    }

    public Boolean checkMsg(String msg){
        msg_valid = true;
        if (msg.length() < 1){
            msg_valid = false;
        }

        for (int i = 0; i < msg.length(); i++) {
            if(!msg.substring(i, i+1).equals(" ")){
                msg_valid = true;
                break;
            } else{
                msg_valid = false;
            }
        }

        return msg_valid;
    }

    private void receiveMessages(){
        db.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Message message = snap.getValue(Message.class);
                    adapter.addMessage(message);

                    String email = message.getUserEmail();

                    /*
                    message_box = findViewById(R.id.caixa);

                    Log.i("message", message.getUserEmail());
                    if(email.contains(nome)){
                        Log.i("message cor", "cinza");
                        message_box.setCardBackgroundColor(Color.rgb(0,0,0));
                    }else if(nome.contains(email)){
                        Log.i("message cor", "cinza");
                        message_box.setCardBackgroundColor(Color.rgb(0,0,0));
                    } else{
                        Log.i("message cor", "vermelho");
                        message_box.setCardBackgroundColor(Color.rgb(255,0,0));
                    }
                    */

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}