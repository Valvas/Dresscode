package fr.hexus.dresscode.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewTokenForm
{
    private String email;
    private String token;

    public NewTokenForm(String email, String token)
    {
        this.email = email;
        this.token = token;
    }

    /****************************************************************************************************/

    public void getNewTokenFromApi(Context applicationContext)
    {
        Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        Call<Token> call = service.getNewToken(this);

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

                        Log.println(Log.ERROR, "GETTING_NEW_TOKEN_FROM_API", "Erreur : " + object.getString("message"));

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
                    final SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, applicationContext.MODE_PRIVATE);

                    Token newToken = response.body();

                    sharedPreferences.edit().putString("token", newToken.getToken()).commit();

                    Log.println(Log.INFO, "GETTING_NEW_TOKEN_FROM_API", "Token successfully updated");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t)
            {
                Log.println(Log.ERROR, "GETTING_NEW_TOKEN_FROM_API", "Erreur : " + t.getMessage());
            }
        });
    }

    /****************************************************************************************************/
}
