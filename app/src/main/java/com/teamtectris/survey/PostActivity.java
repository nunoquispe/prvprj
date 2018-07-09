package com.teamtectris.survey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.teamtectris.survey.entities.AccessToken;
import com.teamtectris.survey.network.ApiService;
import com.teamtectris.survey.network.RetrofitBuilder;

import butterknife.ButterKnife;
import retrofit2.Call;

public class PostActivity extends AppCompatActivity {

    ApiService service;

    Call<AccessToken> call;

    TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this);

        service = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",MODE_PRIVATE));

        if (tokenManager.getToken() == null){
            startActivity(new Intent(PostActivity.this,LoginActivity.class));
            finish();
        }
    }
}
