package com.teamtectris.survey;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @BindView(R.id.txt_email)
    TextInputLayout txtEmail;
    @BindView(R.id.txt_documento)
    TextInputLayout txtDocumento;
    @BindView(R.id.txt_name)
    TextInputLayout txtNobmbre;
    @BindView(R.id.txt_password)
    TextInputLayout txtPassword;

    ApiService service;
    Call<AccessToken> call;

    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));

    }

    @OnClick(R.id.btn_Registrar)
    void register(){

        String documento = txtDocumento.getEditText().getText().toString();
        String email = txtEmail.getEditText().getText().toString();
        String nombres = txtNobmbre.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        txtPassword.setError(null);
        txtNobmbre.setError(null);
        txtEmail.setError(null);
        txtDocumento.setError(null);

        call = service.register(documento,email,password,nombres,password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                Log.w(TAG,"onResponse : " + response);
                if(response.isSuccessful())
                {
                    tokenManager.saveToken(response.body());
                    startActivity(new Intent(RegisterActivity.this,PostActivity.class));
                    finish();
                } else {
                    handleErrors(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.w(TAG,"onFailure: " + t.getMessage());
            }
        });

    }

    private void handleErrors(ResponseBody response)
    {
        ApiError apiError = Utils.converErrors(response);
        for(Map.Entry<String, List<String>> error: apiError.getErrors().entrySet()){
            if (error.getKey().equals("documento")){
                txtDocumento.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("email")){
                txtEmail.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("nombres")){
                txtNobmbre.setError(error.getValue().get(0));
            }
            if (error.getKey().equals("password")){
                txtPassword.setError(error.getValue().get(0));
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
