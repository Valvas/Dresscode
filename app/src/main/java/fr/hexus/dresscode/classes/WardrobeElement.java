package fr.hexus.dresscode.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.Job;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Struct;
import java.util.ArrayList;

import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WardrobeElement implements Serializable, IJobServiceObservable
{
    private String path;
    private int id;
    private int type;
    private String uuid;
    private boolean storedOnApi;
    private ArrayList<Integer> colors;

    private ArrayList<IJobServiceObserver> observers;

    public WardrobeElement(int id, int type, String uuid, ArrayList colors, String path, boolean storedOnApi)
    {
        this.id             = id;
        this.type           = type;
        this.path           = path;
        this.uuid           = uuid;
        this.colors         = colors;
        this.storedOnApi    = storedOnApi;

        this.observers      = new ArrayList<>();
    }

    public int getId()
    {
        return this.id;
    }

    public int getType()
    {
        return this.type;
    }

    public ArrayList<Integer> getColors()
    {
        return this.colors;
    }

    public String getPath()
    {
        return this.path;
    }

    public String getUuid()
    {
        return this.uuid;
    }

    public boolean getStoredOnApi()
    {
        return this.storedOnApi;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setColors(ArrayList<Integer> colors)
    {
        this.colors = colors;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public void setStoredOnApi(boolean storedOnApi)
    {
        this.storedOnApi = storedOnApi;
    }

    public String toString()
    {
        StringBuilder stringToReturn = new StringBuilder();
        stringToReturn.append("\n[Element]\n- ID : " + this.id + "\n- Type : " + this.type + "\n- UUID : " + this.uuid + "\n- Path : " + this.path + "\n- Stored on API : " + this.storedOnApi + "\n- Colors : ");

        for(int x = 0; x < this.colors.size(); x++)
        {
            stringToReturn.append(" " + colors.get(x));
        }

        return String.valueOf(stringToReturn);
    }

    /****************************************************************************************************/
    // SAVE ELEMENT IN DATABASE
    /****************************************************************************************************/

    public boolean saveWardrobeElementInDatabase(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.WARDROBE_TABLE_COLUMNS_TYPE, this.type);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_PATH, this.path);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_UUID, this.uuid);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API, this.storedOnApi);

        long insertedRowId = db.insert(Constants.WARDROBE_TABLE_NAME, null, values);

        if(insertedRowId > 0)
        {
            this.id = (int) insertedRowId;

            for(int x = 0; x < this.colors.size(); x++)
            {
                ContentValues colorValues = new ContentValues();
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID, insertedRowId);
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID, this.colors.get(x));

                long insertedColorId = db.insert(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, null, colorValues);

                if(insertedColorId < 0) return false;
            }
        }

        else
        {
            return false;
        }

        appDatabaseCreation.close();

        return true;
    }

    /****************************************************************************************************/
    // UPDATE ELEMENT IN DATABASE
    /****************************************************************************************************/

    public boolean updateWardrobeElementInDatabase(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.WARDROBE_TABLE_COLUMNS_TYPE, this.type);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_PATH, this.path);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_UUID, this.uuid);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API, false);

        long updatedRows = db.update(Constants.WARDROBE_TABLE_NAME, values, "uuid = ?", new String[]{ this.uuid });

        if(updatedRows > 0)
        {
            long deletedRows = db.delete(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " = ?", new String[]{ String.valueOf(this.id) });

            for(int x = 0; x < this.colors.size(); x++)
            {
                ContentValues colorValues = new ContentValues();
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID, this.id);
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID, this.colors.get(x));

                long insertedColorId = db.insert(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, null, colorValues);

                if(insertedColorId < 0)
                {
                    db.close();

                    return false;
                }
            }
        }

        else
        {
            db.close();

            return false;
        }

        db.close();

        return true;
    }

    /****************************************************************************************************/
    // SEND ELEMENT TO THE API
    /****************************************************************************************************/

    public void sendWardrobeElementToTheAPI(String token, Context context)
    {
        File image = new File(String.valueOf(Environment.getExternalStorageDirectory() + this.path));
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        int[] colors = new int[this.colors.size()];

        for(int i = 0; i < this.colors.size(); i++)
        {
            colors[i] = this.colors.get(i);
        }

        Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        WardrobeElementForm wardrobeElementForm = new WardrobeElementForm(this.type, colors, this.uuid, encodedImage);

        Call<Void> call = service.addWardrobeElement(token, wardrobeElementForm);

        call.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.errorBody() != null)
                {
                    try
                    {
                        JSONObject object = new JSONObject(response.errorBody().string());

                        Log.println(Log.ERROR, "DresscodeAsyncTask", object.getString("message"));

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
                    // REQUEST SUCCEDEED, WARDROBE ELEMENT IS STORED ON THE API AND NEEDS TO BE UPDATED IN LOCAL DATABASE

                    storedOnApi = true;

                    updateWardrobeElementInDatabase(context);

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
    // UPDATE ELEMENT IN THE API
    /****************************************************************************************************/

    public void updateInApi(String token, Context context)
    {
        File image = new File(String.valueOf(Environment.getExternalStorageDirectory() + this.path));
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        int[] colors = new int[this.colors.size()];

        for(int i = 0; i < this.colors.size(); i++)
        {
            colors[i] = this.colors.get(i);
        }

        Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        WardrobeElementForm wardrobeElementForm = new WardrobeElementForm(this.type, colors, this.uuid, encodedImage);

        // PREPARING REQUEST TO THE API

        Call<Void> call = service.updateWardrobeElement(token, wardrobeElementForm);

        // SENDING THE REQUEST

        call.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.errorBody() != null)
                {
                    try
                    {
                        JSONObject object = new JSONObject(response.errorBody().string());

                        Log.println(Log.ERROR, "DresscodeAsyncTask", object.getString("message"));

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
    // REMOVE ELEMENT FROM THE API
    /****************************************************************************************************/

    public void removeFromApi(String token, Context context)
    {
        Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        // PREPARING REQUEST TO THE API

        Call<Void> call = service.removeWardrobeElement(token, this.uuid);

        // SENDING THE REQUEST

        call.enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if(response.errorBody() != null)
                {
                    try
                    {
                        JSONObject object = new JSONObject(response.errorBody().string());

                        Log.println(Log.ERROR, "DresscodeAsyncTask", object.getString("message"));

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
