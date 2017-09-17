package com.csc301.project.iron;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    //Add YOUR Firebase Reference URL instead of the following URL
    private static final String TAG = "IronRegister";
    private Firebase mRef;
    private User user;
    private EditText emailTextField;
    private EditText passwordTextField;
    private EditText passwordConfirmationTextField;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private String emailString;
    private String passwordString;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRef = new Firebase("https://projiron-7bbac.firebaseio.com/");
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        emailTextField = (EditText) findViewById(R.id.email);
        passwordTextField = (EditText) findViewById(R.id.password_register);
        passwordConfirmationTextField = (EditText) findViewById(R.id.password_register_confirmation);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //This method sets up a new User by fetching the user entered details.
    protected void setUpUser() {
        user = new User();
        user.setEmail(emailString);
        user.setPassword(passwordString);
    }

    public void onSignUpClicked(View view) {
        if (passwordTextField.getText().toString().equals(passwordConfirmationTextField.getText().toString())) {
            emailString = emailTextField.getText().toString().trim();
            passwordString = SHA1HashConverter.computeSHA1(passwordTextField.getText().toString().trim()).toString().trim();
            createNewAccount(emailString, passwordString);
            showProgressDialog();
        }
        else {
            Toast.makeText(RegisterActivity.this, "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void createNewAccount(String email, String password) {
        Log.d(TAG, "createNewAccount:" + email);
        if (!validateForm()) {
            return;
        }
        //This method sets up a new User by fetching the user entered details.
        setUpUser();
        //This method  method  takes in an emailTextField address and passwordTextField, validates them and then creates a new user
        // with the createUserWithEmailAndPassword method.
        // If the new account was created, the user is also signed in, and the AuthStateListener runs the onAuthStateChanged callback.
        // In the callback, you can use the getCurrentUser method to get the user's account data.
        Log.d(TAG, "email: " + user.getEmail() + "pass: " + user.getPassword());
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Sign-in Failed:" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Sign-in Failed: " + task.getException().getMessage());
                        } else {
                            onAuthenticationSucess(task.getResult().getUser());
                        }

                    }
                });

    }

    private void onAuthenticationSucess(FirebaseUser mUser) {
        // Write new user
        saveNewUser(mUser.getUid(), user.getEmail(), user.getPassword());
        signOut();
        // Go to LoginActivity
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void saveNewUser(String userId, String email, String password) {
        User user = new User(userId,email,password);

        mRef.child("users").child(userId).setValue(user);
    }


    private void signOut() {
        mAuth.signOut();
    }
    //This method, validates emailTextField address and passwordTextField
    private boolean validateForm() {
        boolean valid = true;

        String userEmail = emailTextField.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            emailTextField.setError("Required.");
            valid = false;
        } else {
            emailTextField.setError(null);
        }

        String userPassword = passwordTextField.getText().toString();
        if (TextUtils.isEmpty(userPassword)) {
            passwordTextField.setError("Required.");
            valid = false;
        } else {
            passwordTextField.setError(null);
        }

        return valid;
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
