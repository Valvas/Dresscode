package fr.hexus.dresscode.classes;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

public class GCMService extends GcmTaskService
{
    @Override
    public int onRunTask(TaskParams taskParams)
    {System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! CALLING ON RUN TASK !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        /****************************************************************************************************/
        // SEND NEW WARDROBE ELEMENT TO THE API
        /****************************************************************************************************/

        if(Constants.WARDROBE_ELEMENT_API_TAG_CREATE.equals(taskParams.getTag()))
        {
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!! SENDING TO API !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            final Bundle extras = taskParams.getExtras();
            final WardrobeElement currentElement = (WardrobeElement) extras.getSerializable(Constants.SERIALIZED_WARDROBE_ELEMENT_KEY);
            final String picture = extras.getString("picture");
            final String token = extras.getString("token");

            try
            {
                currentElement.sendWardrobeElementToTheAPI(token, picture);
                return GcmNetworkManager.RESULT_SUCCESS;

            } catch(CallException exception)
            {
                return GcmNetworkManager.RESULT_RESCHEDULE;
            }
        }

        /****************************************************************************************************/
        // UPDATE WARDROBE ELEMENT IN THE API
        /****************************************************************************************************/

        if(Constants.WARDROBE_ELEMENT_API_TAG_UPDATE.equals(taskParams.getTag()))
        {

        }

        /****************************************************************************************************/
        // DELETE WARDROBE ELEMENT IN THE API
        /****************************************************************************************************/

        if(Constants.WARDROBE_ELEMENT_API_TAG_DELETE.equals(taskParams.getTag()))
        {

        }

        return 0;
    }
}
