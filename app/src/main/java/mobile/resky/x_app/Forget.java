package mobile.resky.x_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget extends AppCompatActivity {

    private EditText forgetText;
    private Button btnForget;
    private String emailForget;
//    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        getSupportActionBar().hide();

        forgetText = findViewById(R.id.etEmail);
//        progressBar = findViewById(R.id.progressForget);

        btnForget = findViewById(R.id.btnForget);
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailForget = forgetText.getText().toString();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = emailForget;

                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Forget.this, "Periksa Email Anda", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }

}