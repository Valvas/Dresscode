package fr.hexus.dresscode.classes;

import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

public class DresscodeJobService extends JobService
{
    @Override
    public boolean onStartJob(JobParameters job)
    {
        /****************************************************************************************************/
        // SEND NEW WARDROBE ELEMENT TO THE API
        /****************************************************************************************************/

        if(Constants.WARDROBE_ELEMENT_API_TAG_CREATE.equals(job.getTag()))
        {
            Log.println(Log.INFO, Constants.LOG_NETWORK_MANAGER_SENDING_NEW_WARDROBE_ELEMENT, "Sending new wardrobe element to the API");

            final Bundle extras = job.getExtras();
            final int type = extras.getInt("type");
            final String colors = extras.getString("colors");
            final String picture = extras.getString("picture");
            final String token = extras.getString("token");

            ArrayList<Integer> elementColors = new ArrayList<>();

            String[] splittedColors = colors.split(",");

            for(int i = 0; i < splittedColors.length; i++)
            {
                elementColors.add(Integer.parseInt(splittedColors[i]));
            }

            WardrobeElement currentElement = new WardrobeElement(0, type, elementColors, picture);

            try
            {
                currentElement.sendWardrobeElementToTheAPI(token, getApplicationContext());

                jobFinished(job, false);

            } catch(CallException exception)
            {
                return true;
            }
        }

        /****************************************************************************************************/
        // UPDATE WARDROBE ELEMENT IN THE API
        /****************************************************************************************************/

        if(Constants.WARDROBE_ELEMENT_API_TAG_UPDATE.equals(job.getTag()))
        {

        }

        /****************************************************************************************************/
        // DELETE WARDROBE ELEMENT IN THE API
        /****************************************************************************************************/

        if(Constants.WARDROBE_ELEMENT_API_TAG_DELETE.equals(job.getTag()))
        {

        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job)
    {
        return true;
    }
}
