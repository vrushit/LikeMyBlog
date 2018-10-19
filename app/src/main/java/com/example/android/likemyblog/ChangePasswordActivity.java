package com.example.android.likemyblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChangePasswordActivity extends AppCompatActivity {
    private Button changePassword_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        changePassword_btn = (Button)findViewById(R.id.change_btn);
        changePassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@Likemyblog.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Change Password");
                intent.putExtra(Intent.EXTRA_TEXT, "Please Change my Password with this User name and Email");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

    }
}
