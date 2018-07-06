package fr.hexus.dresscode.classes;

import android.os.AsyncTask;
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
        Log.println(Log.INFO, "New Job", "A new job has started : " + job.getTag());

        new DresscodeAsyncTask(this).execute(job);

        return true;

        /*if(Constants.WARDROBE_OUTFIT_API_TAG_CREATE.equals(job.getTag()))
        {
            Log.println(Log.INFO, Constants.LOG_NETWORK_MANAGER_SENDING_NEW_WARDROBE_OUTFIT, "Sending new wardrobe outfit to the API");

            final Bundle extras = job.getExtras();

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

            try
            {
                currentOutfit.sendOutfitToTheAPI(token);

            } catch(CallException exception)
            {
                return true;
            }
        }

        return false;*/
    }

    @Override
    public boolean onStopJob(JobParameters job)
    {
        Log.println(Log.DEBUG, "DresscodeAsyncTask", "Job has been stopped !");
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

            /****************************************************************************************************/
            // SEND NEW WARDROBE ELEMENT TO THE API
            /****************************************************************************************************/

            if(Constants.WARDROBE_ELEMENT_API_TAG_CREATE.equals(job[0].getTag()))
            {
                final Bundle extras = job[0].getExtras();
                final int type = extras.getInt("type");
                final String uuid = extras.getString("uuid");
                final String colors = extras.getString("colors");
                final String picture = extras.getString("picture");
                final String token = extras.getString("token");

                ArrayList<Integer> elementColors = new ArrayList<>();

                String[] splittedColors = colors.split(",");

                for(int i = 0; i < splittedColors.length; i++)
                {
                    elementColors.add(Integer.parseInt(splittedColors[i]));
                }

                WardrobeElement currentElement = new WardrobeElement(0, type, uuid, elementColors, picture, false);

                currentElement.addObserver(this);

                Log.println(Log.INFO, Constants.LOG_NETWORK_MANAGER_SENDING_NEW_WARDROBE_ELEMENT, "Sending new wardrobe element to the API (" + currentElement.getUuid() + ")");

                currentElement.sendWardrobeElementToTheAPI(token, getApplicationContext());
            }

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
