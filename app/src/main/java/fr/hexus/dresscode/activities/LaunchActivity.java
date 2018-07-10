package fr.hexus.dresscode.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

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

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class LaunchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        FirebaseJobDispatcher test = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

        test.cancelAll();

        checkWritingStorageRight();
    }

    /****************************************************************************************************/
    // CHECK WRITING STORAGE RIGHT
    /****************************************************************************************************/

    public void checkWritingStorageRight()
    {
        if(checkSelfPermission("WRITE_EXTERNAL_STORAGE") == PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }

        else
        {
            checkInternetAccessRight();
        }
    }

    /****************************************************************************************************/
    // CHECK INTERNET ACCESS RIGHT
    /****************************************************************************************************/

    public void checkInternetAccessRight()
    {
        if(checkSelfPermission("INTERNET") == PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.INTERNET }, 2);
        }

        else
        {
            checkForToken();
        }
    }

    /****************************************************************************************************/
    // GET THE RESULT OF THE PERMISSION
    /****************************************************************************************************/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case 1:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    checkInternetAccessRight();
                }

                else
                {
                    Toast.makeText(this, getResources().getString(R.string.write_external_storage_permission_denied), Toast.LENGTH_LONG).show();
                    finish();
                }

                return;
            }

            case 2:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    checkForToken();
                }

                else
                {
                    Toast.makeText(this, getResources().getString(R.string.internet_permission_denied), Toast.LENGTH_LONG).show();
                    finish();
                }

                return;
            }
        }
    }

    /****************************************************************************************************/
    // CHECK IF THERE IS A TOKEN
    /****************************************************************************************************/

    public void checkForToken()
    {
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

            handler.postDelayed(() ->
            {
                finish();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }, 3000);
        }
    }
}
