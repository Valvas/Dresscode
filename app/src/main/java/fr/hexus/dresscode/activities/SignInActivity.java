package fr.hexus.dresscode.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.LogonForm;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.classes.WardrobeAllElementsForm;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignInActivity extends AppCompatActivity
{
    private ProgressBar loadingSpinner;
    private EditText emailInput;
    private EditText passwordInput;
    private LinearLayout signInForm;

    private Button signInButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

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

                        getAccountDataFromAPI(newToken.getToken());

                        /*Toast.makeText(getApplicationContext(), getResources().getString(R.string.sign_in_success), Toast.LENGTH_LONG).show();

                        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

                        sharedPreferences.edit().putString("token", newToken.getToken()).commit();

                        finish();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));*/
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
        Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        Call<WardrobeAllElementsForm> call = service.getAllWardrobeElements(token);

        /****************************************************************************************************/
        // CALL THE API TO GET ALL WARDROBE ELEMENTS
        /****************************************************************************************************/

        call.enqueue(new Callback<WardrobeAllElementsForm>()
        {
            @Override
            public void onResponse(Call<WardrobeAllElementsForm> call, Response<WardrobeAllElementsForm> response)
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
                    WardrobeAllElementsForm wardrobeAllElementsForm = response.body();

                    ArrayList<WardrobeElement> preparedElements = new ArrayList<>();

                    for(int i = 0; i < wardrobeAllElementsForm.getElements().size(); i++)
                    {
                        /****************************************************************************************************/

                        byte[] decode = Base64.decode(wardrobeAllElementsForm.getElements().get(i).getPicture(), Base64.DEFAULT);

                        Bitmap bmp = BitmapFactory.decodeByteArray(decode, 0, decode.length);

                        File dresscodeDirectory = new File(Environment.getExternalStorageDirectory() + "/Dresscode");

                        if(!dresscodeDirectory.exists())
                        {
                            dresscodeDirectory.mkdirs();
                        }

                        try
                        {
                            File f = new File(dresscodeDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(decode);
                            fo.close();

                            wardrobeAllElementsForm.getElements().get(i).setPicture(f.getName());

                        } catch(IOException e1)
                        {
                            e1.printStackTrace();
                        }

                        /****************************************************************************************************/

                        ArrayList<Integer> currentElementColors = new ArrayList<>();

                        for(int j = 0; j < wardrobeAllElementsForm.getElements().get(i).getColors().length; j++)
                        {
                            currentElementColors.add(wardrobeAllElementsForm.getElements().get(i).getColors()[j]);
                        }

                        preparedElements.add(new WardrobeElement(0, wardrobeAllElementsForm.getElements().get(i).getType(), wardrobeAllElementsForm.getElements().get(i).getUuid(), currentElementColors, wardrobeAllElementsForm.getElements().get(i).getPicture(), true));

                        System.out.println(preparedElements.get(i).toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<WardrobeAllElementsForm> call, Throwable t)
            {
                loadingSpinner.setVisibility(View.GONE);
                signInForm.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /****************************************************************************************************/
}
