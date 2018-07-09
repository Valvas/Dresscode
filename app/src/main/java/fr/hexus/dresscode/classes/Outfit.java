package fr.hexus.dresscode.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Outfit implements Serializable, IJobServiceObservable
{
    private String name;
    private String uuid;
    private boolean storedOnApi;
    private List<WardrobeElement> elements;

    private ArrayList<IJobServiceObserver> observers;

    public Outfit(String name, List<WardrobeElement> elements, String uuid, boolean storedOnApi)
    {
        this.uuid = uuid;
        this.storedOnApi = storedOnApi;
        this.elements = elements;
        this.name = name;

        this.observers = new ArrayList<>();
    }

    public List<WardrobeElement> getElements()
    {
        return elements;
    }

    public void setElements(List<WardrobeElement> elements)
    {
        this.elements = elements;
    }

    public void addElementToOutfit(WardrobeElement element)
    {
        this.elements.add(element);
    }

    public String getName()
    {
        return this.name;
    }

    public String getUuid()
    {
        return this.uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public void setStoredInApi(boolean storedInApi)
    {
        this.storedOnApi = storedInApi;
    }

    public String toString()
    {
        return "\n[Outfit]\n- UUID : " + this.uuid + "\n- Name : " + this.name + "\n- Stored in API : " + this.storedOnApi + "\n- Elements : " + this.elements.size() + "\n";
    }

    /****************************************************************************************************/
    // SAVE OUTFIT IN DATABASE
    /****************************************************************************************************/

    public boolean saveOutfitInDatabase(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.OUTFIT_TABLE_COLUMNS_NAME, this.name);
        values.put(Constants.OUTFIT_TABLE_COLUMNS_UUID, this.uuid);
        values.put(Constants.OUTFIT_TABLE_COLUMNS_STORED_ON_API, this.storedOnApi);

        long insertedRowId = db.insert(Constants.OUTFIT_TABLE_NAME, null, values);

        if(insertedRowId > 0)
        {
            for(int i = 0; i < this.elements.size(); i++)
            {
                ContentValues outfitCurrentElementValues = new ContentValues();
                outfitCurrentElementValues.put(Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_OUTFIT_UUID, this.uuid);
                outfitCurrentElementValues.put(Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_ELEMENT_ID, this.elements.get(i).getId());

                long insertedElementId = db.insert(Constants.OUTFIT_ELEMENTS_TABLE_NAME, null, outfitCurrentElementValues);

                if(insertedElementId < 0)
                {
                    appDatabaseCreation.close();

                    return false;
                }
            }

            appDatabaseCreation.close();

            return true;
        }

        else
        {
            appDatabaseCreation.close();

            return false;
        }
    }

    /****************************************************************************************************/
    // UPDATE OUTFIT IN THE LOCAL DATABASE
    /****************************************************************************************************/

    public void updateOutfitInDatabase(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.OUTFIT_TABLE_COLUMNS_NAME, this.name);
        values.put(Constants.OUTFIT_TABLE_COLUMNS_UUID, this.uuid);
        values.put(Constants.OUTFIT_TABLE_COLUMNS_NAME, this.storedOnApi);

        long updatedRows = db.update(Constants.OUTFIT_TABLE_NAME, values, Constants.OUTFIT_TABLE_COLUMNS_UUID + " = ?", new String[]{ String.valueOf(this.uuid) });
    }

    /****************************************************************************************************/
    // SEND OUTFIT TO THE API
    /****************************************************************************************************/

    public void sendOutfitToTheAPI(String token, Context context)
    {
        WardrobeElementForm[] wardrobeElementForms = new WardrobeElementForm[this.elements.size()];

        /****************************************************************************************************/
        // CREATE A WARDROBE ELEMENT FORM FOR EACH ELEMENT IN THE OUTFIT
        /****************************************************************************************************/

        for(int i = 0; i < this.elements.size(); i++)
        {
            File image = new File(String.valueOf(Environment.getExternalStorageDirectory() + elements.get(i).getPath()));
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            byte[] byteArrayImage = baos.toByteArray();

            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

            int[] colors = new int[elements.get(i).getColors().size()];

            for(int j = 0; j < elements.get(i).getColors().size(); j++)
            {
                colors[j] = elements.get(i).getColors().get(j);
            }

            wardrobeElementForms[i] = new WardrobeElementForm(elements.get(i).getType(), colors, elements.get(i).getUuid(), encodedImage);
        }

        /****************************************************************************************************/
        // CREATE THE OUTFIT FORM
        /****************************************************************************************************/

        OutfitForm outfitForm = new OutfitForm(this.name, wardrobeElementForms);

        /****************************************************************************************************/
        // GET RETROFIT CLIENT AND PREPARE CALL
        /****************************************************************************************************/

        Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        Call<Void> call = service.addWardrobeOutfit(token, outfitForm);

        /****************************************************************************************************/
        // EXECUTE THE CALL AND SEND DATA
        /****************************************************************************************************/

        call.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.errorBody() != null )
                {
                    try
                    {
                        JSONObject object = new JSONObject(response.errorBody().string());

                        Log.println(Log.ERROR, "TEST", object.getString("message"));

                    } catch(JSONException e)
                    {
                        e.printStackTrace();

                    } catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                    try
                    {
                        notifyObservers(true);

                    } catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                else
                {
                    storedOnApi = true;

                    updateOutfitInDatabase(context);

                    try
                    {
                        notifyObservers(false);

                    } catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Log.println(Log.ERROR, "DresscodeAsyncTask", t.getMessage());

                try
                {
                    notifyObservers(true);

                } catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /****************************************************************************************************/
    // ADD OBSERVER TO THIS CLASS
    /****************************************************************************************************/

    @Override
    public void addObserver(IJobServiceObserver o)
    {
        observers.add(o);
    }

    /****************************************************************************************************/
    // REMOVE OBSERVER TO THIS CLASS
    /****************************************************************************************************/

    @Override
    public void removeObserver(IJobServiceObserver o)
    {
        observers.remove(o);
    }

    /****************************************************************************************************/
    // NOTIFY OBSERVERS OF THIS CLASS
    /****************************************************************************************************/

    @Override
    public void notifyObservers(boolean rescheduleJob) throws Exception
    {
        Log.println(Log.DEBUG, "DresscodeAsyncTask", "Notifying observers ! Reschedule : " + rescheduleJob);

        for(int i = 0; i < observers.size(); i++)
        {
            observers.get(i).jobDone(rescheduleJob);
        }
    }
}
