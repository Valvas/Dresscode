package fr.hexus.dresscode.dresscode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Account
{
    @SerializedName("user_id")
    @Expose
    private long userId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("id_token")
    @Expose
    private String token;
    @SerializedName("lastname")
    @Expose
    private String lastname;
    @SerializedName("firstname")
    @Expose
    private String firstname;

    public Account(String token, String email, String firstname, String lastname, String password, long id)
    {
        this.userId = id;
        this.email = email;
        this.token = token;
        this.lastname = lastname;
        this.password = password;
        this.firstname = firstname;
    }

    public long getUserId()
    {
        return userId;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPassword()
    {
        return password;
    }

    public String getToken()
    {
        return token;
    }

    public String getLastname()
    {
        return lastname;
    }

    public String getFirstname()
    {
        return firstname;
    }
}
