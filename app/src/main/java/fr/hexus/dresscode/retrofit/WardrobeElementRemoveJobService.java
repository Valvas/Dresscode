package fr.hexus.dresscode.retrofit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.IJobServiceObserver;
import fr.hexus.dresscode.classes.WardrobeElement;

public class WardrobeElementRemoveJobService extends JobService
{
    @Override
    public boolean onStartJob(JobParameters job)
    {
        new WardrobeElementRemoveJobService.DresscodeAsyncTask(this).execute(job);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params)
    {
        return false;
    }

    /****************************************************************************************************/
    // INTERN CLASS THAT WILL HANDLE ASYNC TASKS
    /****************************************************************************************************/

    private class DresscodeAsyncTask extends AsyncTask<com.firebase.jobdispatcher.JobParameters, Void, com.firebase.jobdispatcher.JobParameters> implements IJobServiceObserver
    {
        private com.firebase.jobdispatcher.JobService jobService;
        private com.firebase.jobdispatcher.JobParameters jobParameters;

        public DresscodeAsyncTask(com.firebase.jobdispatcher.JobService jobService)
        {
            this.jobService = jobService;
        }

        @Override
        protected com.firebase.jobdispatcher.JobParameters doInBackground(com.firebase.jobdispatcher.JobParameters... job)
        {
            this.jobParameters = job[0];

            final Bundle extras = job[0].getExtras();
            final String uuid = extras.getString("uuid");
            final String token = extras.getString("token");

            WardrobeElement currentElement = new WardrobeElement(0, 0, uuid, new ArrayList(), "", true);

            currentElement.addObserver(this);

            Log.println(Log.INFO, Constants.LOG_NETWORK_MANAGER_REMOVING_WARDROBE_ELEMENT, "SRemoving wardrobe element from the API (" + currentElement.getUuid() + ")");

            currentElement.removeFromApi(token, getApplicationContext());

            return job[0];
        }

        @Override
        protected void onPostExecute(com.firebase.jobdispatcher.JobParameters job)
        {

        }

        @Override
        public void jobDone(boolean rescheduleJob) throws Exception
        {
            jobService.jobFinished(jobParameters, rescheduleJob);
        }
    }
}
