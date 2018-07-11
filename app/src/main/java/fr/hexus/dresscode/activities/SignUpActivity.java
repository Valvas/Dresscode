package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.SignUpForm;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity
{
    ProgressBar loadingSpinner;
    EditText emailInput;
    EditText passwordInput;
    EditText lastnameInput;
    EditText firstnameInput;
    EditText confirmationInput;
    CheckBox termsOfUse;
    TextView termsOfUseDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailInput = findViewById(R.id.signUpEmailInput);
        termsOfUse = findViewById(R.id.signUpTermsOfUse);
        passwordInput = findViewById(R.id.signUpPasswordInput);
        lastnameInput = findViewById(R.id.signUpLastnameInput);
        firstnameInput = findViewById(R.id.signUpFirstnameInput);
        termsOfUseDetail = findViewById(R.id.signUpTermsOfUseDetail);
        confirmationInput = findViewById(R.id.signUpConfirmationInput);

        loadingSpinner = findViewById(R.id.loadingSpinner);

        SpannableString content = new SpannableString(String.valueOf(termsOfUseDetail.getText()));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        termsOfUseDetail.setText(content);
    }

    public void checkFormBeforeSending(View view)
    {
        if(emailInput.getText().length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_email_empty), Toast.LENGTH_SHORT).show();
        }

        else if(emailInput.getText().toString().matches(Constants.EMAIL_REGEX_FORMAT) == false)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_email_format), Toast.LENGTH_SHORT).show();
        }

        else if(lastnameInput.getText().length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_lastname_empty), Toast.LENGTH_SHORT).show();
        }

        else if(lastnameInput.getText().toString().matches(Constants.LASTNAME_REGEX_FORMAT) == false)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_lastname_format), Toast.LENGTH_SHORT).show();
        }

        else if(firstnameInput.getText().length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_firstname_empty), Toast.LENGTH_SHORT).show();
        }

        else if(firstnameInput.getText().toString().matches(Constants.FIRSTNAME_REGEX_FORMAT) == false)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_firstname_format), Toast.LENGTH_SHORT).show();
        }

        else if(passwordInput.getText().length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_password_empty), Toast.LENGTH_SHORT).show();
        }

        else if(passwordInput.getText().toString().matches(Constants.PASSWORD_REGEX_FORMAT) == false)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_password_format), Toast.LENGTH_SHORT).show();
        }

        else if(confirmationInput.getText().length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_confirmation_empty), Toast.LENGTH_SHORT).show();
        }

        else if(confirmationInput.getText().toString().equals(passwordInput.getText().toString()) == false)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_confirmation_mismatch), Toast.LENGTH_SHORT).show();
        }

        else if(termsOfUse.isChecked() == false)
        {
            Toast.makeText(this, getResources().getString(R.string.sign_up_error_terms_of_use), Toast.LENGTH_SHORT).show();
        }

        else
        {
            sendDataToServer();
        }
    }

    public void sendDataToServer()
    {
        Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        SignUpForm signUpForm = new SignUpForm(emailInput.getText().toString(), firstnameInput.getText().toString(), lastnameInput.getText().toString(), passwordInput.getText().toString());

        Call<Token> call = service.signUp(signUpForm);

        loadingSpinner.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<Token>()
        {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response)
            {
                loadingSpinner.setVisibility(View.GONE);

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

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_up_success), Toast.LENGTH_LONG).show();

                    final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

                    sharedPreferences.edit().putString("token", newToken.getToken()).commit();
                    sharedPreferences.edit().putString("email", emailInput.getText().toString()).commit();
                    sharedPreferences.edit().putLong("expire", System.currentTimeMillis()).commit();

                    finish();
                    Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    homeIntent.setFlags(homeIntent.FLAG_ACTIVITY_NEW_TASK | homeIntent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(homeIntent);
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t)
            {
                Toast.makeText(getApplicationContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
