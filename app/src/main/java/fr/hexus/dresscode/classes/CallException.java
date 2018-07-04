package fr.hexus.dresscode.classes;

import android.util.Log;

public class CallException extends Exception
{
    public CallException(String message)
    {
        Log.println(Log.ERROR, "CallException", message);
    }
}
