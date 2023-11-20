package mobile.resky.x_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserInfo extends AppCompatActivity {

    private EditText etEmail, etPassword;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        etEmail = findViewById(R.id.infoEmail);
        etEmail.setEnabled(false);

        // Call the userInfo method to populate the EditText fields
        userInfo();
    }

    private void userInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String getEmail = user.getEmail();
            // Note: Firebase does not provide the password through getCurrentUser()
            // So, you cannot get the password like this

            // Set the email to the EditText
            etEmail.setText(String.format("Email : %s", getEmail));
        }
    }
}
