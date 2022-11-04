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

//Voz
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.ChannelMediaOptions;


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

    //Voz
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO
            };

    private boolean checkSelfPermission()
    {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) !=  PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "11286fbd9bad4255ad81a8bc1ae74720";
    // Fill the channel name.
    private String channelName = "Canal";
    // Fill the temp token generated on Agora Console.
    private String token = "007eJxTYHD9MXHvq7xJUUH3onp49q9If6PRU7F1o9fSGY2MAg4fD5YrMBgaGlmYpSWlWCYlppgYmZomplgYJlokJRsmppqbmBsZHP+WktwQyMgwIzODiZEBAkF8VgbnxLzEHAYGAO8yIWA=";
    // An integer that identifies the local user.
    private int uid = 0;
    // Track the status of your connection
    private boolean isJoined = false;

    // Agora engine instance
    private RtcEngine agoraEngine;
    // UI elements
    private TextView infoText;
    private Button joinLeaveButton;

    private void setupVoiceSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(()->infoText.setText("Remote user joined: " + uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            // Successfully joined a channel
            isJoined = true;
            showMessage("Joined Channel " + channel);
            runOnUiThread(()->infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline " + uid + " " + reason);
            if (isJoined) runOnUiThread(()->infoText.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onLeaveChannel(RtcStats 	stats) {
            // Listen for the local user leaving the channel
            runOnUiThread(()->infoText.setText("Press the button to join a channel"));
            isJoined = false;
        }
    };

    private void joinChannel() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.autoSubscribeAudio = true;
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channelName, uid, options);
    }

    public void joinLeaveChannel(View view) {
        if (isJoined) {
            agoraEngine.leaveChannel();
            joinLeaveButton.setText("Join");
        } else {
            joinChannel();
            joinLeaveButton.setText("Leave");
        }
    }

    // Acabou voz


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Voz

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

        setupVoiceSDKEngine();

        // Set up access to the UI elements
        joinLeaveButton = findViewById(R.id.joinLeaveButton);
        infoText = findViewById(R.id.infoText);

        //Acabou voz

        //super.onCreate(savedInstanceState);
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
                    Toast.makeText(MainActivity.this, "Escreve alguma coisa...", Toast.LENGTH_SHORT).show();
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

    //Voz

    protected void onDestroy() {
        agoraEngine.leaveChannel();
        super.onDestroy();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }

    //Acabou voz

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