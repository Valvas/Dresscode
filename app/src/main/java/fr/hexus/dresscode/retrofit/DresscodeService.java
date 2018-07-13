package fr.hexus.dresscode.retrofit;

import fr.hexus.dresscode.classes.LogonForm;
import fr.hexus.dresscode.classes.NewTokenForm;
import fr.hexus.dresscode.classes.OutfitForm;
import fr.hexus.dresscode.classes.SignUpForm;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.classes.UuidForm;
import fr.hexus.dresscode.classes.WardrobeAllElementsForm;
import fr.hexus.dresscode.classes.WardrobeAllOutfitsForm;
import fr.hexus.dresscode.classes.WardrobeElementForm;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
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
    Call<Token> getNewToken(@Body NewTokenForm currentToken);

    @POST("/addElement")
    Call<Void> addWardrobeElement(@Header("Authorization") String token, @Body WardrobeElementForm wardrobeElementForm);

    @POST("/updateElement")
    Call<Void> updateWardrobeElement(@Header("Authorization") String token, @Body WardrobeElementForm wardrobeElementForm);

    @POST("/addOutfit")
    Call<Void> addWardrobeOutfit(@Header("Authorization") String token, @Body OutfitForm outfitForm);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/deleteOutfit", hasBody = true)
    Call<Void> removeWardrobeOutfit(@Header("Authorization") String token, @Field("uuid") String uuid);

    @GET("/getAllElements")
    Call<WardrobeAllElementsForm> getAllWardrobeElements(@Header("Authorization") String token);

    @GET("/getAllOutfits")
    Call<WardrobeAllOutfitsForm> getAllWardrobeOutfits(@Header("Authorization") String token);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/deleteElement", hasBody = true)
    Call<Void> removeWardrobeElement(@Header("Authorization") String token, @Field("uuid") String uuid);
}
