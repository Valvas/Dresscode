package fr.hexus.dresscode.classes;

import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Retrofit;

public class Token
{
    private String token;

    public Token(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return this.token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
