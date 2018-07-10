package fr.hexus.dresscode.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import java.io.File;
import java.util.ArrayList;

import fr.hexus.dresscode.activities.R;

public abstract class Logout
{
    private static ArrayList<FinishListener> finishListeners = new ArrayList<>();

    public static void addFinishListener(FinishListener fl)
    {
        finishListeners.add(fl);
    }

    public static void logoutAccount(Context applicationContext)
    {
        boolean nonSynchronizedElements = false;

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(applicationContext);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        // CHECK IF THERE ARE NON SYNCHRONIZED WARDROBE ELEMENTS

        Cursor wardrobeElementsCursor = db.query(Constants.WARDROBE_TABLE_NAME, new String[]{ Constants.WARDROBE_TABLE_COLUMNS_UUID }, Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API + " = ?", new String[]{ "0" }, null, null, null);

        if(wardrobeElementsCursor.getCount() > 0) nonSynchronizedElements = true;

        wardrobeElementsCursor.close();

        // CHECK IF THERE ARE NON SYNCHRONIZED WARDROBE OUTFITS

        Cursor wardrobeOutfitsCursor = db.query(Constants.OUTFIT_TABLE_NAME, new String[]{ Constants.OUTFIT_TABLE_COLUMNS_UUID }, Constants.OUTFIT_TABLE_COLUMNS_STORED_ON_API + " = ?", new String[]{ "0" }, null, null, null);

        if(wardrobeOutfitsCursor.getCount() > 0) nonSynchronizedElements = true;

        wardrobeOutfitsCursor.close();

        // WARN USER THAT HE WILL LOSE HIS DATA IF HE LOGS OUT NOW

        if(nonSynchronizedElements)
        {
            new AlertDialog.Builder(applicationContext)
            .setTitle(applicationContext.getResources().getString(R.string.logout_title))
            .setMessage(applicationContext.getResources().getString(R.string.logout_non_synch_warning))
            .setIcon(R.drawable.ic_info_red)
            .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
            {
                removeRemainingQueries(applicationContext);
            })
            .setNegativeButton(android.R.string.no, null).show();
        }

        // WARN USER THAT DATA WILL BE REMOVED FROM DEVICE

        else
        {
            new AlertDialog.Builder(applicationContext)
            .setTitle(applicationContext.getResources().getString(R.string.logout_title))
            .setMessage(applicationContext.getResources().getString(R.string.logout_remove_data_warning))
            .setIcon(R.drawable.ic_info_red)
            .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
            {
                removeDataAndLogout(applicationContext);
            })
            .setNegativeButton(android.R.string.no, null).show();
        }
    }

    /****************************************************************************************************/

    private static void removeRemainingQueries(Context context)
    {
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        jobDispatcher.cancelAll();

        removeDataAndLogout(context);
    }

    /****************************************************************************************************/

    private static void removeDataAndLogout(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        // CLEAR DATABASE

        db.delete(Constants.OUTFIT_TABLE_NAME, null, null);
        db.delete(Constants.WARDROBE_TABLE_NAME, null, null);
        db.delete(Constants.OUTFIT_ELEMENTS_TABLE_NAME, null, null);
        db.delete(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, null, null);

        // CLEAR DRESSCODE PICTURES FOLDER

        File dresscodeDirectory = new File(Environment.getExternalStorageDirectory() + Constants.DRESSCODE_APP_FOLDER);

        if(dresscodeDirectory.isDirectory())
        {
            String[] pictures = dresscodeDirectory.list();

            for(int i = 0; i < pictures.length; i++)
            {
                new File(dresscodeDirectory, pictures[i]).delete();
            }
        }

        // CLEAR TOKEN FROM PREFERENCES

        final SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, context.MODE_PRIVATE);

        if(sharedPreferences.contains("token"))
        {
            sharedPreferences.edit().remove("token").commit();
        }

        closeActivity();
    }

    /****************************************************************************************************/

    private static void closeActivity()
    {
        for(FinishListener fl : finishListeners)
        {
            fl.finishActivity();
        }
    }
}
