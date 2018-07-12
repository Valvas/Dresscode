package fr.hexus.dresscode.retrofit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.IJobServiceObserver;
import fr.hexus.dresscode.classes.NewTokenForm;
import fr.hexus.dresscode.classes.WardrobeElement;

public class GetNewTokenJobService extends JobService
{
    @Override
    public boolean onStartJob(JobParameters job)
    {
        new GetNewTokenJobService.DresscodeAsyncTask(this).execute(job);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job)
    {
        return true;
    }

    /****************************************************************************************************/
    // INTERN CLASS THAT WILL HANDLE ASYNC TASKS
    /****************************************************************************************************/

    private class DresscodeAsyncTask extends AsyncTask<JobParameters, Void, JobParameters>
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
            final String token = extras.getString("token");
            final String email = extras.getString("email");

            NewTokenForm newTokenForm = new NewTokenForm(email, token);

            newTokenForm.getNewTokenFromApi(getApplicationContext());

            return job[0];
        }

        @Override
        protected void onPostExecute(JobParameters job)
        {

        }
    }
}
