package com.example.iome.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iome.device_select.DeviceSelectActivity;
import com.example.iome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText email, password;
    private TextView signupRedirect;
    private Button confirmButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.signin_email);
        password = findViewById(R.id.signin_password);
        confirmButton = findViewById(R.id.confirm_button_signin);
        signupRedirect = findViewById(R.id.login_redirect_signup);


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString();
                String pass = password.getText().toString();
                if (!emailStr.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(emailStr, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignInActivity.this, DeviceSelectActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        password.setError("Empty fields are not allowed");
                    }
                } else if (emailStr.isEmpty()) {
                    email.setError("Empty fields are not allowed");
                } else {
                    email.setError("Please enter correct email");
                }
            }
        });
        signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }
}
