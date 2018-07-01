package fr.hexus.dresscode.classes;

public class CallException extends Exception
{
    public CallException()
    {
        super();
    }

    public CallException(String message)
    {
        super(message);
    }

    public CallException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CallException(Throwable cause)
    {
        super(cause);
    }
}
