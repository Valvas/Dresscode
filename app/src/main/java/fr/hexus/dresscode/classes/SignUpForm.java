package fr.hexus.dresscode.classes;

public class SignUpForm
{
    private String email;
    private String lastname;
    private String password;
    private String firstname;

    public SignUpForm(String email, String firstname, String lastname, String password)
    {
        this.email = email;
        this.lastname = lastname;
        this.password = password;
        this.firstname = firstname;
    }
}
