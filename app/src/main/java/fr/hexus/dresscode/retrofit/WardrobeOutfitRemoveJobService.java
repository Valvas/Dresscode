package fr.hexus.dresscode.retrofit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.IJobServiceObserver;
import fr.hexus.dresscode.classes.Outfit;

public class WardrobeOutfitRemoveJobService extends JobService
{
    @Override
    public boolean onStartJob(JobParameters job)
    {
        new WardrobeOutfitRemoveJobService.DresscodeAsyncTask(this).execute(job);

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

            final String outfitUuid = extras.getString("outfitUuid");
            final String token = extras.getString("token");

            // CREATE THE OUTFIT USING THE ELEMENTS CREATED ABOVE

            Outfit currentOutfit = new Outfit("", null, outfitUuid, false);

            currentOutfit.addObserver(this);

            Log.println(Log.INFO, Constants.LOG_NETWORK_MANAGER_REMOVING_WARDROBE_OUTFIT, "Removing wardrobe outfit from the API (" + currentOutfit.getUuid() + ")");

            currentOutfit.removeOutfitFromTheApi(token);

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
