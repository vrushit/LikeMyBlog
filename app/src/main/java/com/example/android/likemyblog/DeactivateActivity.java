package com.example.android.likemyblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DeactivateActivity extends AppCompatActivity {

    private Button deactivateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deactivate);

        deactivateBtn = (Button) findViewById(R.id.de_btn);
        deactivateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@Likemyblog.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Deactivation");
                intent.putExtra(Intent.EXTRA_TEXT, "Please Deactivate my Account on Like My Blog with current Email.");

                startActivity(Intent.createChooser(intent, "Send Email"));

            }
        });

      /*  Toast.makeText(DeactivateActivity.this, "Your Email Has been sent", Toast.LENGTH_LONG).show();
            Intent backtoMain = new Intent(DeactivateActivity.this, MainActivity.class);
            startActivity(backtoMain);
            */
    }
}
