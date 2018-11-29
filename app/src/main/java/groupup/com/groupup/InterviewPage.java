package groupup.com.groupup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InterviewPage extends AppCompatActivity {

    private Button send_msg;
    private EditText input_msg;
    private TextView chat_text;

    private String user_name, room_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_page);

        send_msg = (Button) findViewById(R.id.buttonSend);
        input_msg = (EditText) findViewById(R.id.msgSend);
        chat_text = (TextView) findViewById(R.id.textView);

        setTitle(" Waitlist Chatroom ");


    }
}
