package com.example.ambilbalik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    TextInputLayout name, phone_number, ic_number, address, email, password, confirm_password;
    DatePicker dob;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        name=findViewById(R.id.name);
        phone_number=findViewById(R.id.phone_number);
        ic_number=findViewById(R.id.ic_number);
        address=findViewById(R.id.address);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirm_password=findViewById(R.id.confirm_password);
        dob=findViewById(R.id.dob);

    }

    public void goLogin(View view){
        String userName = name.getEditText().getText().toString();
        String phoneNumber = phone_number.getEditText().getText().toString();
        String icNumber = ic_number.getEditText().getText().toString();
        String userAddress = address.getEditText().getText().toString();
        String userEmail = email.getEditText().getText().toString();
        String userPassword = password.getEditText().getText().toString();
        String confirmPassword = confirm_password.getEditText().getText().toString();

        Date userDob = getDateFromDatePicker(dob);

        if (userName.isEmpty() || phoneNumber.isEmpty() || icNumber.isEmpty() || userAddress.isEmpty() ||
        userEmail.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(Register.this, "Please Fill all the requirement !", Toast.LENGTH_SHORT).show();
        } else
        {
            if (isEmailValid(userEmail)){
                if (!userPassword.equals(confirmPassword)){
                    Toast.makeText(Register.this, "The password is not match", Toast.LENGTH_SHORT).show();
                }else {
                    if (checkPassword(userPassword)){
                    user.setUserName(userName);
                    user.setPhoneNumber(phoneNumber);
                    user.setIcNumber(icNumber);
                    user.setUserAddress(userAddress);
                    user.setUserEmail(userEmail);
                    user.setUserPassword(userPassword);
                    user.setConfirmPassword(confirmPassword);
                    user.setUserDob(userDob.toString());
                    user.setUserType("user");
                    user.setImagesUrl("https://firebasestorage.googleapis.com/v0/b/ambilbalik-84f9c.appspot.com/o/Users%2Fblank-profile-picture-973460_640.png?alt=media&token=5a390127-21b6-4f48-b5a6-2ad41b9813f0");

                        createUserEmailAndPassword(userPassword,userEmail);
                    }
                }
            }else{
                Toast.makeText(Register.this, "Email is invalid !", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean checkPassword(String password) {

        boolean hasLetter = false;
        boolean hasDigit = false;

        if (password.length() >= 8) {
            for (int i = 0; i < password.length(); i++) {
                char x = password.charAt(i);
                if (Character.isLetter(x)) {

                    hasLetter = true;
                } else if (Character.isDigit(x)) {

                    hasDigit = true;
                }

                // no need to check further, break the loop
                if (hasLetter && hasDigit) {

                    break;
                }

            }
            if (hasLetter && hasDigit) {
                return true;
            } else {
                Toast.makeText(Register.this, "The password must contained letter and alphabet ", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Register.this, "The password must more than 8 character ", Toast.LENGTH_LONG).show();
        }
        return false;
    }
    public void uploadUserToDB(User user) {

        db.collection("users").document("User " + user.getUid()).set(user).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    public void createUserEmailAndPassword(String password, String email) {


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {


                            user.setUid(task.getResult().getUser().getUid());
                            uploadUserToDB(user);
                            Toast.makeText(Register.this, "Congrats your account had been registered." + user.getUserName(), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Register.this, Login.class);
                            startActivity(intent);

                        } else if (!task.isSuccessful() && task.getException() instanceof FirebaseAuthUserCollisionException) {
                            FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) task.getException();
                            if (exception.getErrorCode() != null) {

                                Toast.makeText(Register.this, "The email you enter is already used", Toast.LENGTH_LONG).show();

                            }

                        }

                    }
                });


    }

    public void goBackLogin(View view){
        Intent intent = new Intent (Register.this,Login.class);
        startActivity(intent);
    }
}