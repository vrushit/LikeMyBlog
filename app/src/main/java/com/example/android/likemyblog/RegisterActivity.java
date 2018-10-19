package com.example.android.likemyblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_emailField;
    private EditText reg_passField;
    private EditText reg_confirmPasswordField;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_Progress;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        reg_emailField = (EditText) findViewById(R.id.reg_email);
        reg_passField = (EditText) findViewById(R.id.reg_password);
        reg_confirmPasswordField = (EditText) findViewById(R.id.reg_confirmPassword);
        reg_btn = (Button) findViewById(R.id.reg_btn);
        reg_login_btn = (Button) findViewById(R.id.reg_login_btn);
        reg_Progress = (ProgressBar) findViewById(R.id.reg_progress_bar);

        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Email = reg_emailField.getText().toString();
                String Password = reg_passField.getText().toString();
                String confirmPassword = reg_confirmPasswordField.getText().toString();

                if(!TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Password) & !TextUtils.isEmpty(confirmPassword))
                {
                    if(Password.equals(confirmPassword))
                    {
                        reg_Progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {

                                    /* Intent setupIntent  = new Intent(RegisterActivity.this,SetupActivity.class);
                                      startActivity(setupIntent);
                                      finish();*/
                                    sendToMain();

                                }
                                else
                                {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error Message: " + errorMessage ,Toast.LENGTH_LONG).show();

                                }
                                reg_Progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this,"Confirm Password and Password are Not Matching",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
