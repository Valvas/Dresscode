package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LaunchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        if(sharedPreferences.contains("token"))
        {
            Retrofit retrofit = RetrofitClient.getClient();

            DresscodeService service = retrofit.create(DresscodeService.class);

            Token currentToken = new Token(sharedPreferences.getString("token", null));

            Call<Token> call = service.getNewToken(currentToken);

            call.enqueue(new Callback<Token>()
            {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response)
                {
                    if(response.errorBody() != null )
                    {
                        try
                        {
                            JSONObject object = new JSONObject(response.errorBody().string());

                            Toast.makeText(getApplicationContext(), "Erreur : " + object.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch(JSONException e)
                        {
                            e.printStackTrace();

                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    else
                    {
                        Token newToken = response.body();

                        sharedPreferences.edit().putString("token", newToken.getToken()).commit();

                        finish();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t)
                {
                    Toast.makeText(getApplicationContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                }
            }, 3000);
        }
    }
}
