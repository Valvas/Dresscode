package fr.hexus.dresscode.retrofit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.IJobServiceObserver;
import fr.hexus.dresscode.classes.Outfit;
import fr.hexus.dresscode.classes.WardrobeElement;

public class WardrobeOutfitCreateJobService extends JobService
{
    @Override
    public boolean onStartJob(JobParameters job)
    {
        new WardrobeOutfitCreateJobService.DresscodeAsyncTask(this).execute(job);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job)
    {
        return false;
    }

    /****************************************************************************************************/
    // INTERN CLASS THAT WILL HANDLE ASYNC TASKS
    /****************************************************************************************************/

    private class DresscodeAsyncTask extends AsyncTask<JobParameters, Void, JobParameters> implements IJobServiceObserver
    {
        private JobService jobService;
        private JobParameters jobParameters;

        public DresscodeAsyncTask(JobService jobService)
        {
            this.jobService = jobService;
        }

        @Override
        protected JobParameters doInBackground(JobParameters... job)
        {
            this.jobParameters = job[0];

            final Bundle extras = job[0].getExtras();

            final String outfitName = extras.getString("outfitName");
            final String outfitUuid = extras.getString("outfitUuid");

            final String token = extras.getString("token");
            final String types = extras.getString("types");
            final String uuids = extras.getString("uuids");
            final String paths = extras.getString("paths");
            final String stored = extras.getString("stored");
            final String colors = extras.getString("colors");

            final String[] elementsTypes = types.split(";");
            final String[] elementsUuids = uuids.split(";");
            final String[] elementsPaths = paths.split(";");
            final String[] elementsStored = stored.split(";");
            final String[] elementsColors = colors.split(";");

            ArrayList<WardrobeElement> elements = new ArrayList<>();

            // BROWSE EACH ELEMENT TO INSERT THEM INTO AN ARRAY LIST

            for(int i = 0; i < elementsUuids.length; i++)
            {
                ArrayList<Integer> elementColors = new ArrayList<>();

                String[] splittedColors = elementsColors[i].split(",");

                for(int j = 0; j < splittedColors.length; j++)
                {
                    elementColors.add(Integer.parseInt(splittedColors[j]));
                }

                elements.add(new WardrobeElement(0, Integer.parseInt(elementsTypes[i]), elementsUuids[i], elementColors, elementsPaths[i], Integer.parseInt(elementsStored[i]) == 1));
            }

            // CREATE THE OUTFIT USING THE ELEMENTS CREATED ABOVE

            Outfit currentOutfit = new Outfit(outfitName, elements, outfitUuid, false);

            currentOutfit.addObserver(this);

            Log.println(Log.INFO, Constants.LOG_NETWORK_MANAGER_SENDING_NEW_WARDROBE_OUTFIT, "Sending new wardrobe outfit to the API (" + currentOutfit.getUuid() + ")");

            currentOutfit.sendOutfitToTheAPI(token, getApplicationContext());

            return job[0];
        }

        @Override
        protected void onPostExecute(JobParameters job)
        {

        }

        @Override
        public void jobDone(boolean rescheduleJob) throws Exception
        {
            jobService.jobFinished(jobParameters, rescheduleJob);
        }
    }
}
