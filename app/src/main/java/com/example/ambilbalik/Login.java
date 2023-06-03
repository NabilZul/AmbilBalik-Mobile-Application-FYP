package com.example.ambilbalik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ambilbalik.Admin.AdminDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputLayout password,email;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        button = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(Login.this, UserDashboard.class);
            startActivity(intent);
        }
    }

    public void goToDashboard(View view){

        if (email.getEditText().getText().toString().isEmpty() || password.getEditText().toString().isEmpty()){
            email.setError("Please fill all the details");
            password.setError("Please fill all the details");
        } else
        {
            validator(password.getEditText().getText().toString(), email.getEditText().getText().toString());
        }
    }

    public void validator (String password, String email){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserType();
                        } else {
                            Toast.makeText(Login.this, "email or password is wrong ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void checkUserType() {

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    User user = new User();
                                    user = document.toObject(User.class);
                                    if (user.getUid().equals(mAuth.getCurrentUser().getUid()) && user.getUserType().equals("user")) {
                                        Intent intent = new Intent(Login.this, UserDashboard.class);
                                        startActivity(intent);
                                        break;

                                    } else if (user.getUid().equals(mAuth.getCurrentUser().getUid()) && user.getUserType().equals("admin")){
                                        Intent intent3 = new Intent(Login.this, AdminDashboard.class);
                                        startActivity(intent3);
                                        break;
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(Login.this, "Fail ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void goToRegister (View view){
        Intent intent = new Intent (Login.this,Register.class);
        startActivity(intent);
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        startActivity(intent);
    }
}