package fr.hexus.dresscode.classes;

public class LogonForm
{
    private String email;
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
