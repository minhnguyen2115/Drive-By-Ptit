package com.ptit.ptitdrive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.drive.Drive;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private Button bt_logout, bt_gg;
    private TextView tv_name;
    private SignInButton signInButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static String personName;
    int login = 1;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = findViewById(R.id.signed_in_button);
        bt_logout = findViewById(R.id.bt_logout);
        bt_gg = findViewById(R.id.bt_gg);
        tv_name = findViewById(R.id.tv_name);

        sharedPreferences = getSharedPreferences("note", MODE_PRIVATE);
        login = sharedPreferences.getInt("login", login);
        name = sharedPreferences.getString("name", "");
        if(login==1) {
            bt_gg.setVisibility(View.INVISIBLE);
            bt_logout.setVisibility(View.INVISIBLE);
        }else{
            bt_logout.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
            tv_name.setVisibility(View.VISIBLE);
            tv_name.setText("Hello "+name);
        }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestScopes(Drive.SCOPE_FILE).requestScopes(Drive.SCOPE_APPFOLDER).build();
                googleSignInClient = GoogleSignIn.getClient(MainActivity.this, signInOptions);
                googleSignInClient.signOut();
                Toast.makeText(MainActivity.this,"LOGOUT SUCCESSFUlLY", Toast.LENGTH_SHORT).show();
                bt_logout.setVisibility(View.INVISIBLE);
                signInButton.setVisibility(View.VISIBLE);
                bt_gg.setVisibility(View.INVISIBLE);
                tv_name.setText("");
                editor = sharedPreferences.edit();
                editor.remove("login");
                editor.remove("name");
                editor.commit();
            }
        });

        bt_gg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signIn() {
        Log.i(TAG, "Start sign in");
        GoogleSignInClient GoogleSignInClient = buildGoogleSignInClient();
        startActivityForResult(GoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");
                if (resultCode == RESULT_OK && data!=null) {
                    Log.i(TAG, "Signed in successfully.");
                    getInfo();
                    bt_logout.setVisibility(View.VISIBLE);
                    bt_gg.setVisibility(View.VISIBLE);
                    signInButton.setVisibility(View.INVISIBLE);
                    login = 2;
                    editor = sharedPreferences.edit();
                    editor.putInt("login", login);
                    editor.commit();
                }
                else {
                    Toast.makeText(MainActivity.this,"Login False",Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void getInfo(){
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {
            personName = googleSignInAccount.getDisplayName();
            tv_name.setText("Hello "+personName);
            editor = sharedPreferences.edit();
            editor.putString("name", personName);
            editor.commit();

            String personGivenName = googleSignInAccount.getGivenName();
            String personFamilyName = googleSignInAccount.getFamilyName();
            String personEmail = googleSignInAccount.getEmail();
            String personId = googleSignInAccount.getId();
            Uri personPhoto = googleSignInAccount.getPhotoUrl();
            Log.i(TAG +" photo", personPhoto+"");
            Log.i(TAG +" name", personName+"");
        }
    }
}
