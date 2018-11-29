package groupup.com.groupup;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import groupup.com.groupup.Database.DatabaseManager;
import groupup.com.groupup.Database.GetDataListener;
import groupup.com.groupup.Database.GroupKeys;
import groupup.com.groupup.Database.UserKeys;

public class GroupCommunicationPage extends AppCompatActivity {

    private Button send_msg;
    private EditText input_msg;
    private TextView chat_text;

    private String user_name, room_name, temp_key;
    private DatabaseReference chatroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_communication_page);

        send_msg = (Button) findViewById(R.id.buttonSend);
        input_msg = (EditText) findViewById(R.id.msgSend);
        chat_text = (TextView) findViewById(R.id.textView);

        room_name =  getIntent().getStringExtra("GROUP_NAME");
        user_name = "TEMP";
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseManager db = DatabaseManager.getInstance();
        db.getUserWithIdentifier(UserKeys.ID, userID, new GetDataListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                User user = data.getValue(User.class);
                user_name = user.getName();
            }

            @Override
            public void onFailure(DatabaseError error) {

            }
        });
        setTitle(" Room - " + room_name);

        chatroom = FirebaseDatabase.getInstance().getReference("chatrooms").child(room_name);

        send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                String myMessage = input_msg.getText().toString();
                if(!myMessage.equals("") && myMessage.length() > 200)
                {
                    Map<String, Object> map = new HashMap<String, Object>();
                    temp_key = chatroom.push().getKey();
                    chatroom.updateChildren(map);

                    DatabaseReference message = chatroom.child(temp_key);
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("name", user_name);
                    map2.put("msg", input_msg.getText().toString());

                    message.updateChildren(map2);
                    input_msg.setText("");
                }

            }
        });

        chatroom.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String chat_msg, chat_user_name;
    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while(i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();

            chat_text.append(chat_user_name + " : " + chat_msg + " \n");
        }
    }
}
