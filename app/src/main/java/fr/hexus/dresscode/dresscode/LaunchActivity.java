package fr.hexus.dresscode.dresscode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaunchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        if(sharedPreferences.contains("token"))
        {
            Retrofit retrofit = RetrofitClient.getClient();

            DresscodeService service = retrofit.create(DresscodeService.class);

            LogonForm logonForm = new LogonForm("test@dresscode.fr", "Password123");

            Call<Account> call = service.logon(logonForm);

            call.enqueue(new Callback<Account>()
            {
                @Override
                public void onResponse(Call<Account> call, Response<Account> response)
                {
                    Account account = response.body();
                }

                @Override
                public void onFailure(Call<Account> call, Throwable t)
                {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            /*finish();
            startActivity(new Intent(this, HomeActivity.class));*/
        }

        else
        {
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LogonActivity.class));
                }
            }, 2000);
        }
    }
}
