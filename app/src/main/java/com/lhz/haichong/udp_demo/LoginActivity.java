package com.lhz.haichong.udp_demo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by lxy36 on 2017/3/7.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login_Button;
    private EditText login_username, login_password;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);

        init();
        login_Button.setOnClickListener(this);
    }

    private void init() {
        login_Button = (Button) findViewById(R.id.login_button);
        login_username = (EditText) findViewById(R.id.login_username);
        login_password = (EditText) findViewById(R.id.login_password);
    }

    @Override
    public void onClick(View view) {
        if(view == login_Button){
            new Thread(new Login()).start();
        }
    }

    private class Login implements Runnable{
        @Override
        public void run() {

        }
    }
}
