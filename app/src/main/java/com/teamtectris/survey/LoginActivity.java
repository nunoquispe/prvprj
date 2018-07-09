package com.teamtectris.survey;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.teamtectris.survey.entities.AccessToken;
import com.teamtectris.survey.entities.ApiError;
import com.teamtectris.survey.network.ApiService;
import com.teamtectris.survey.network.RetrofitBuilder;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    @BindView(R.id.txt_password_login)
    TextInputLayout txtPassLogin;

    @BindView(R.id.txt_email_login)
    TextInputLayout txtEmailLogin;

    ApiService service;
    Call<AccessToken> call;

    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));

        if (tokenManager.getToken().getAccessToken() != null){
            startActivity(new Intent(LoginActivity.this,PostActivity.class));
            finish();
        }

    }

    @OnClick(R.id.btn_Login)
    void login(){
        String email = txtEmailLogin.getEditText().getText().toString();
        String password = txtPassLogin.getEditText().getText().toString();

        txtEmailLogin.setError(null);
        txtPassLogin.setError(null);

        call = service.login(email,password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                Log.w(TAG,"onResponse : " + response);
                if(response.isSuccessful())
                {
                    tokenManager.saveToken(response.body());
                    startActivity(new Intent(LoginActivity.this,PostActivity.class));
                    finish();

                } else {
                    if(response.code() == 422){
                        handleErrors(response.errorBody());
                    }
                    if(response.code() == 401){
                        ApiError apiError = Utils.converErrors(response.errorBody());
                        Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.w(TAG,"onFailure: " + t.getMessage());
            }
        });
    }

    private void handleErrors(ResponseBody response) {
        ApiError apiError = Utils.converErrors(response);
        for(Map.Entry<String, List<String>> error: apiError.getErrors().entrySet()){
            if (error.getKey().equals("username")){
                txtEmailLogin.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")){
                txtPassLogin.setError(error.getValue().get(0));
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (call != null)
        {
            call.cancel();
            call = null;
        }
    }
}

