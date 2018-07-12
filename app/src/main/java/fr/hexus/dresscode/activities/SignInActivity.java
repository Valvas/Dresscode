package fr.hexus.dresscode.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import fr.hexus.dresscode.classes.ApiDataGetter;
import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.IGetDataFromApiObserver;
import fr.hexus.dresscode.classes.LogonForm;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.classes.WardrobeAllElementsForm;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.GetNewTokenJobService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignInActivity extends AppCompatActivity implements IGetDataFromApiObserver
{
    private ProgressBar loadingSpinner;
    private EditText emailInput;
    private EditText passwordInput;
    private LinearLayout signInForm;

    private Button signInButton;
    private Button signUpButton;

    private FirebaseJobDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

        signInButton = findViewById(R.id.logonPageSendButton);
        signUpButton = findViewById(R.id.logonPageRegisterButton);

        signInForm = findViewById(R.id.signInForm);

        emailInput = findViewById(R.id.signInEmailInput);
        passwordInput = findViewById(R.id.signInPasswordInput);

        loadingSpinner = findViewById(R.id.loadingSpinner);
    }

    public void onSignInClick(View view)
    {
        if(emailInput.getText().length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_in_email_empty), Toast.LENGTH_SHORT).show();
        }

        else if(passwordInput.getText().length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_in_password_empty), Toast.LENGTH_SHORT).show();
        }

        else
        {
            Retrofit retrofit = RetrofitClient.getClient();

            DresscodeService service = retrofit.create(DresscodeService.class);

            LogonForm logonForm = new LogonForm(emailInput.getText().toString(), passwordInput.getText().toString());

            Call<Token> call = service.signIn(logonForm);

            /****************************************************************************************************/
            // ADD A SPINNER WHILE LOADING
            /****************************************************************************************************/

            signInForm.setVisibility(View.GONE);
            loadingSpinner.setVisibility(View.VISIBLE);

            /****************************************************************************************************/
            // HIDE KEYBOARD WHILE LOADING
            /****************************************************************************************************/

            View currentView = this.getCurrentFocus();

            if(currentView != null)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            /****************************************************************************************************/
            // CALL THE API TO TRY AUTHENTICATION
            /****************************************************************************************************/

            call.enqueue(new Callback<Token>()
            {
                @Override
                public void onResponse(Call<Token> call, Response<Token> response)
                {
                    if(response.errorBody() != null )
                    {
                        loadingSpinner.setVisibility(View.GONE);
                        signInForm.setVisibility(View.VISIBLE);

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

                        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

                        sharedPreferences.edit().putString("token", newToken.getToken()).commit();
                        sharedPreferences.edit().putString("email", emailInput.getText().toString()).commit();

                        Bundle extras = new Bundle();

                        extras.putString("email", sharedPreferences.getString("email", null));
                        extras.putString("token", sharedPreferences.getString("token", null));

                        Job job = dispatcher.newJobBuilder()
                                .setService(GetNewTokenJobService.class)
                                .setTag(Constants.GET_NEW_TOKEN_JOB_TAG)
                                .setRecurring(true)
                                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                                .setTrigger(Trigger.executionWindow(Constants.NEW_TOKEN_RECURRING_TASK_MIN, Constants.NEW_TOKEN_RECURRING_TASK_MAX))
                                .setReplaceCurrent(true)
                                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                                .setConstraints(Constraint.ON_ANY_NETWORK)
                                .setExtras(extras)
                                .build();

                        dispatcher.mustSchedule(job);

                        getAccountDataFromAPI(newToken.getToken());
                    }
                }

                @Override
                public void onFailure(Call<Token> call, Throwable t)
                {
                    loadingSpinner.setVisibility(View.GONE);
                    signInForm.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onSignUpClick(View view)
    {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    /****************************************************************************************************/

    public void getAccountDataFromAPI(String token)
    {
        ApiDataGetter apiDataGetter = new ApiDataGetter();

        apiDataGetter.addObserver(this);

        apiDataGetter.getDataFromApi(token, getApplicationContext());
    }

    /****************************************************************************************************/

    @Override
    public void taskDone(boolean isTaskSuccessful)
    {
        if(isTaskSuccessful)
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_in_success), Toast.LENGTH_LONG).show();
        }

        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.get_data_from_api_failed), Toast.LENGTH_LONG).show();
        }

        finish();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    /****************************************************************************************************/
}
