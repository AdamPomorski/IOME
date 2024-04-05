package com.example.iome.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText email, password;
    private Button confirmButton;
    private TextView loginRedirect;

@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmButton = findViewById(R.id.confirm_button_signup);
        loginRedirect = findViewById(R.id.login_redirect_signin);

    confirmButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String user = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            if (user.isEmpty()){
                email.setError("Email cannot be empty");
            }
            if (pass.isEmpty()){
                password.setError("Password cannot be empty");
            } else{
                auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    });
    loginRedirect.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        }
    });


    }
}
