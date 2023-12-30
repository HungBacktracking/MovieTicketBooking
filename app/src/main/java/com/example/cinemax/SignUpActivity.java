package com.example.cinemax;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cinemax.dialog.ProgressHelper;
import com.example.cinemax.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {
    Button signup;
    TextView login;
    EditText username, password, re_password, email;
    String emailPattern = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$";


    TextView facebookSignUpBtn;
    TextView googleSignUpBtn;
    CallbackManager callbackManager;
    GoogleSignInClient googleSignInClient;
    ActivityResultLauncher<Intent> signInLauncher;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        login = findViewById(R.id.convert_to_sign_in);
        signup = findViewById(R.id.btn_signup);
        username = findViewById(R.id.username_signup_edit);
        password = findViewById(R.id.password_signup_edit);
        re_password = findViewById(R.id.password_reenter_signup_edit);
        email = findViewById(R.id.email_signup_edit);
        googleSignUpBtn = findViewById(R.id.btn_signup_google);
        facebookSignUpBtn = findViewById(R.id.btn_signup_facebook);

        handleSwitchToLogin();
        handleSignUp();

        handleSignUpByFacebook();
        handleSignUpByGoogle();
    }

    private void handleSignUpByGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        handleSignInResult(data);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        googleSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut();
                SignUpWithGoogle();
            }
        });
    }

    private void SignUpWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(intent);
    }

    private void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthGoogle(String idToken) {
        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("users").child(id);

                                // Kiểm tra xem user đã tồn tại trong database hay chưa
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // User đã tồn tại, không thực hiện lưu thông tin lên database
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // User chưa tồn tại, thực hiện lưu thông tin lên database
                                            String imageUri = "https://firebasestorage.googleapis.com/v0/b/cinemax-363b4.appspot.com/o/avatar.jpg?alt=media&token=953ebbd4-5031-483a-9142-483ce7b849a1";
                                            User user = new User(id, imageUri);
                                            reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SignUpActivity.this, "Success in signing", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(SignUpActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void handleSignUpByFacebook() {
        callbackManager = CallbackManager.Factory.create();
        facebookSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("email", "public_profile"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Đăng ký thành công, sử dụng AccessToken để đăng ký vào Firebase
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignUpActivity.this, "You canceled the authentication.",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignUpActivity.this, error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication he.", Toast.LENGTH_LONG).show();
                            String id = task.getResult().getUser().getUid();
                            DatabaseReference reference = database.getReference().child("users").child(id);

                            // Kiểm tra xem user đã tồn tại trong database hay chưa
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // User đã tồn tại, không thực hiện lưu thông tin lên database
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // User chưa tồn tại, thực hiện lưu thông tin lên database
                                        String imageUri = "https://firebasestorage.googleapis.com/v0/b/cinemax-363b4.appspot.com/o/avatar.jpg?alt=media&token=953ebbd4-5031-483a-9142-483ce7b849a1";
                                        User user = new User(id, imageUri);
                                        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "Success in creating new user", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignUpActivity.this, "Error in creating the user", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(SignUpActivity.this, "Database error", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleSignUp() {
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username = username.getText().toString();
                String Password = password.getText().toString();
                String RePassword = re_password.getText().toString();
                String Email = email.getText().toString();

                if (TextUtils.isEmpty(Username) || TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(RePassword)) {
                    ProgressHelper.dismissDialog();
                    Toast.makeText(SignUpActivity.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(emailPattern)) {
                    ProgressHelper.dismissDialog();
                    email.setError("Please enter a valid email address");
                } else if (Password.length() < 6) {
                    ProgressHelper.dismissDialog();
                    password.setError("Your password must be at least 6 characters long");
                } else if (!Password.equals(RePassword)) {
                    ProgressHelper.dismissDialog();
                    re_password.setError("The password don't match");
                } else {
                    createUserAccount(Username, Password, Email);
                }
            }
        });
    }

    private void createUserAccount(String Username, String Password, String Email) {
        auth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = task.getResult().getUser().getUid();
                    DatabaseReference reference = database.getReference().child("users").child(id);

                    String imageUri = "https://firebasestorage.googleapis.com/v0/b/cinemax-363b4.appspot.com/o/avatar.jpg?alt=media&token=953ebbd4-5031-483a-9142-483ce7b849a1";
                    User user = new User(id, imageUri, Username);
                    reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Success in creating the user", Toast.LENGTH_SHORT).show();
                                ProgressHelper.showDialog(SignUpActivity.this, "Establishing The Account...");
                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleSwitchToLogin() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressHelper.dismissDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ProgressHelper.dismissDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}