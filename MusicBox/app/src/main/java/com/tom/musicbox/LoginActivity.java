package com.tom.musicbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tom on 2016/5/21.
 */
public class LoginActivity  extends Activity implements View.OnClickListener {

    Button loginBtn;
    EditText userNameEditText, passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        userNameEditText = (EditText) findViewById(R.id.username_edit);
        passwordEditText = (EditText) findViewById(R.id.password_edit);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        enableDisableBtns();


        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                enableDisableBtns();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                enableDisableBtns();
            }

            @Override
            public void afterTextChanged(Editable s) {

                enableDisableBtns();
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                enableDisableBtns();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                enableDisableBtns();
            }

            @Override
            public void afterTextChanged(Editable s) {

                enableDisableBtns();
            }


        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_btn:
                //TODO: save login username and password into SharedPreference
                String userNameEditTextStr = userNameEditText.getText().toString().trim();
                String passwordEditTextStr = passwordEditText.getText().toString().trim();
                saveLoginInfo(userNameEditTextStr,passwordEditTextStr);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                break;

            default:
                break;
        }


    }

    protected void enableDisableBtns() {
        String userNameEditTextStr = userNameEditText.getText().toString().trim();
        String passwordEditTextStr = passwordEditText.getText().toString().trim();
        Log.d("WEI check", "username " + userNameEditTextStr + " passwordStr " + passwordEditTextStr);


        if (userNameEditTextStr.length() == 0 || passwordEditTextStr.length() == 0) {
            loginBtn.setEnabled(false);
            loginBtn.setClickable(false);
            loginBtn.setAlpha(0.5f);
        } else {
            loginBtn.setEnabled(true);
            loginBtn.setClickable(true);
            loginBtn.setAlpha(1.0f);
        }

    }

    protected void saveLoginInfo(String username,String password){
            SharedPreferences sharedpreferences = getSharedPreferences("MusicBox", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.commit();
    }




}
