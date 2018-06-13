package fr.hexus.dresscode.dresscode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogonForm
{
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    public LogonForm(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    public String getEmail()
    {
        return this.email;
    }

    public String getPassword()
    {
        return this.password;
    }
}
