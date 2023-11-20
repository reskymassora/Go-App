package mobile.resky.x_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText et_email, et_password;
    private Button btnlogin;
    private TextView txtRegister;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener Listener;
    private String getEmail, getPassword;
    private ProgressBar progressBar;
    private TextView txtForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //Inisialisasi Widget
        et_email = findViewById(R.id.etEmail);
        et_password = findViewById(R.id.etPassword);
        btnlogin = findViewById(R.id.buttonLogin);
        txtRegister = findViewById(R.id.txtRegister);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);

        // Instance / membuat Objek Firebase Autentication
        auth = FirebaseAuth.getInstance();

        // cek keberadaan user
        Listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    //jika ada maka halaman akan langsung berpindah pada Menu Utama
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        };

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                //mendapatkan data yang diinputkan User
                getEmail = et_email.getText().toString();
                getPassword = et_password.getText().toString();

                //mengecek apakah email dan sandi kosong atau tidak
                if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPassword)) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Email atau Sandi Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
                } else {
                    loginUserAccount();
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }
        });


        txtForget = findViewById(R.id.txtClickere);
        txtForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Forget.class));
            }
        });

    }

    //Menerapkan Listener
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(Listener);
    }

    //Melepaskan Listener
    @Override
    protected void onStop() {
        super.onStop();
        if (Listener != null) {
            auth.removeAuthStateListener(Listener);
        }
    }

    private void loginUserAccount() {
        auth.signInWithEmailAndPassword(getEmail, getPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        //Mengecek status keberhasilan saat login
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            if (auth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);

                            } else {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                                alert.setTitle("Periksa Email anda untuk verifikasi !");
                                alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                                alert.create();
                                alert.show();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
