package fr.hexus.dresscode.retrofit;

import fr.hexus.dresscode.classes.LogonForm;
import fr.hexus.dresscode.classes.Account;
import fr.hexus.dresscode.classes.SignUpForm;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.classes.WardrobeElementForm;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface DresscodeService
{
    @POST("/signIn")
    Call<Token> signIn(@Body LogonForm logonForm);

    @POST("/signUp")
    Call<Token> signUp(@Body SignUpForm signUpForm);

    @PUT("/getNewToken")
    Call<Token> getNewToken(@Body Token currentToken);

    @POST("/addElement")
    Call<Void> addWardrobeElement(@Header("Authorization") String token, @Body WardrobeElementForm wardrobeElementForm);
}
