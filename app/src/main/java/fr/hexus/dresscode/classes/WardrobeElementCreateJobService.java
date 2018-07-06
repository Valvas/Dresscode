package fr.hexus.dresscode.classes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

public class WardrobeElementCreateJobService extends JobService
{
    @Override
    public boolean onStartJob(JobParameters job)
    {
        new WardrobeElementCreateJobService.DresscodeAsyncTask(this).execute(job);

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
