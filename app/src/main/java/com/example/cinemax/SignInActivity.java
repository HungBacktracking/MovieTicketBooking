package com.example.cinemax;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    Button login;
    TextView signup;
    EditText email, password;
    TextView facebookLoginButton;
    TextView googleLoginButton;
    CallbackManager callbackManager;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    GoogleSignInClient googleSignInClient;
    ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        login = findViewById(R.id.btn_login);
        signup = findViewById(R.id.convert_to_sign_up);
        email = findViewById(R.id.email_login_edit);
        password = findViewById(R.id.password_login_edit);
        facebookLoginButton = findViewById(R.id.btn_login_facebook);
        googleLoginButton = findViewById(R.id.btn_login_google);

        handleClickAction();
        handleFacebookLogin();
        handleGoogleLogin();
    }

    private void handleGoogleLogin() {
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
                        Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignInClient.signOut();
                loginWithGoogle();
            }
        });
    }

    private void loginWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(intent);
    }

    private void handleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
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
                                                        Toast.makeText(SignInActivity.this, "Success in signing", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SignInActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(SignInActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void handleFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("email", "public_profile"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Đăng nhập thành công, sử dụng AccessToken để đăng nhập vào Firebase
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, "You canceled the authentication.",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, error.getMessage(),
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
                            String id = task.getResult().getUser().getUid();
                            DatabaseReference reference = database.getReference().child("users").child(id);

                            // Kiểm tra xem user đã tồn tại trong database hay chưa
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // User đã tồn tại, không thực hiện lưu thông tin lên database
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
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
                                                    Toast.makeText(SignInActivity.this, "Success in signing", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignInActivity.this, "Error in creating the user", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ database
                                    Toast.makeText(SignInActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleClickAction() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressHelper.dismissDialog();
                handleLogin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleLogin() {
        String Email = email.getText().toString();
        String Password = password.getText().toString();

        if (TextUtils.isEmpty(Email)) {
            ProgressHelper.dismissDialog();
            Toast.makeText(SignInActivity.this, "Enter the email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Password)) {
            ProgressHelper.dismissDialog();
            Toast.makeText(SignInActivity.this, "Enter the password", Toast.LENGTH_SHORT).show();
        } else if (!Email.matches(emailPattern)) {
            ProgressHelper.dismissDialog();
            Toast.makeText(SignInActivity.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
        } else if (Password.length() < 6) {
            ProgressHelper.dismissDialog();
            Toast.makeText(SignInActivity.this, "Your password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
        } else {
            startLogin(Email, Password);
        }
    }

    private void startLogin(String Email, String Password) {
        auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    ProgressHelper.showDialog(SignInActivity.this, "Please Wait...");
                    try {
                        Intent intent = new Intent(SignInActivity.this , MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        ProgressHelper.dismissDialog();
                        Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ProgressHelper.dismissDialog();
                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
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
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}