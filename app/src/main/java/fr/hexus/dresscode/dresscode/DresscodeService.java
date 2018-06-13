package fr.hexus.dresscode.dresscode;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DresscodeService
{
    @POST("/signIn")
    Call<Account> logon(@Body LogonForm logonForm);
}
