package org.bitnp.netcheckin2.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;

public class Demo extends ActionBarActivity {

    EditText u, p;
    Button login, logout, forcelogout, set;
    TextView msg, state;
    LoginHelper helper = new LoginHelper();
    String TAG = "LoginHelper";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        u=(EditText)findViewById(R.id.editText);
        p=(EditText)findViewById(R.id.editText2);
        login=(Button)findViewById(R.id.button);
        logout=(Button)findViewById(R.id.button2);
        forcelogout=(Button)findViewById(R.id.button3);
        set=(Button)findViewById(R.id.button4);
        msg=(TextView)findViewById(R.id.textView);
        state=(TextView)findViewById(R.id.textView2);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.asyncLogin();
            }
        });

        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                helper.asyncLogout();
            }
        });

        forcelogout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                helper.asyncForceLogout();
            }
        });

        set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //helper.setAccount(u.getText().toString(), p.getText().toString());
            }
        });
    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message m) {
            super.handleMessage(m);
            //Log.v(TAG, helper.getErrorMessage());
            //msg.setText(helper.getErrorMessage());
            //state.setText(helper.getLoginState()+"");
        }
    };
}
