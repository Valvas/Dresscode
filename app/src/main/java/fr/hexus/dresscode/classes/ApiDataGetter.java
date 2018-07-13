package fr.hexus.dresscode.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiDataGetter implements IGetDataFromApiObservable
{
    private ArrayList<IGetDataFromApiObserver> observers;

    private Retrofit retrofit = RetrofitClient.getClient();

    private DresscodeService service;

    public ApiDataGetter()
    {
        this.observers = new ArrayList<>();

        this.service = retrofit.create(DresscodeService.class);
    }

    /****************************************************************************************************/
    //  CALLED BY AN ACTIVITY TO GET ALL DATA FROM THE API
    /****************************************************************************************************/

    public void getDataFromApi(String token, Context applicationContext)
    {
        getWardrobeElementsFromApi(token, applicationContext);
    }

    /****************************************************************************************************/
    // CALLED TO GET ALL WARDROBE ELEMENTS FROM THE API
    /****************************************************************************************************/

    private void getWardrobeElementsFromApi(String token, Context applicationContext)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(applicationContext);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        Call<WardrobeAllElementsForm> call = service.getAllWardrobeElements(token);

        call.enqueue(new Callback<WardrobeAllElementsForm>()
        {
            @Override
            public void onResponse(Call<WardrobeAllElementsForm> call, Response<WardrobeAllElementsForm> response)
            {
                if(response.errorBody() != null )
                {
                    db.close();

                    try{ notifyObservers(false); } catch (Exception e){ e.printStackTrace(); }
                }

                else
                {
                    WardrobeAllElementsForm wardrobeAllElementsForm = response.body();

                    File dresscodeDirectory = new File(Environment.getExternalStorageDirectory() + "/Dresscode");

                    if(!dresscodeDirectory.exists())
                    {
                        dresscodeDirectory.mkdirs();
                    }

                    int counter = 0;

                    for(int i = 0; i < wardrobeAllElementsForm.getElements().size(); i++)
                    {
                        /****************************************************************************************************/

                        byte[] decode = Base64.decode(wardrobeAllElementsForm.getElements().get(i).getPicture(), Base64.DEFAULT);

                        Bitmap bmp = BitmapFactory.decodeByteArray(decode, 0, decode.length);

                        try
                        {
                            File f = new File(dresscodeDirectory, Calendar.getInstance().getTimeInMillis() + String.valueOf(counter += 1) + ".jpg");
                            f.createNewFile();
                            FileOutputStream fo = new FileOutputStream(f);
                            fo.write(decode);
                            fo.close();

                            wardrobeAllElementsForm.getElements().get(i).setPicture(Constants.DRESSCODE_APP_FOLDER + "/" + f.getName());

                        } catch(IOException e1)
                        {
                            db.close();

                            e1.printStackTrace();
                        }

                        /****************************************************************************************************/

                        ArrayList<Integer> currentElementColors = new ArrayList<>();

                        for(int j = 0; j < wardrobeAllElementsForm.getElements().get(i).getColors().length; j++)
                        {
                            currentElementColors.add(wardrobeAllElementsForm.getElements().get(i).getColors()[j]);
                        }

                        WardrobeElement currentElement = new WardrobeElement(0, wardrobeAllElementsForm.getElements().get(i).getType(), wardrobeAllElementsForm.getElements().get(i).getUuid(), currentElementColors, wardrobeAllElementsForm.getElements().get(i).getPicture(), true);

                        Cursor getElementCursor = db.query(Constants.WARDROBE_TABLE_NAME, new String[]{ "id" },  Constants.WARDROBE_TABLE_COLUMNS_UUID + " = ?", new String[]{ currentElement.getUuid() }, null, null, null);

                        if(getElementCursor.getCount() == 0)
                        {
                            currentElement.saveWardrobeElementInDatabase(applicationContext);
                        }

                        else
                        {
                            currentElement.updateWardrobeElementInDatabase(applicationContext);
                        }

                        getElementCursor.close();
                    }

                    db.close();

                    getWardrobeOutfitsFromApi(token, applicationContext);
                }
            }

            @Override
            public void onFailure(Call<WardrobeAllElementsForm> call, Throwable t)
            {
                db.close();

                try{ notifyObservers(false); } catch (Exception e){ e.printStackTrace(); }
            }
        });
    }

    /****************************************************************************************************/
    // CALLED TO GET ALL WARDROBE OUTFITS FROM THE API
    /****************************************************************************************************/

    private void getWardrobeOutfitsFromApi(String token, Context applicationContext)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(applicationContext);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        Call<WardrobeAllOutfitsForm> call = service.getAllWardrobeOutfits(token);

        call.enqueue(new Callback<WardrobeAllOutfitsForm>()
        {
            @Override
            public void onResponse(Call<WardrobeAllOutfitsForm> call, Response<WardrobeAllOutfitsForm> response)
            {
                if(response.errorBody() != null)
                {
                    db.close();

                    try{ notifyObservers(false); } catch(Exception e){ e.printStackTrace(); }
                }

                else
                {
                    WardrobeAllOutfitsForm wardrobeAllOutfitsForm = response.body();

                    File dresscodeDirectory = new File(Environment.getExternalStorageDirectory() + "/Dresscode");

                    int counter = 0;

                    // BROWSE EACH OUTFIT IN THE DATA

                    for(int i = 0; i < wardrobeAllOutfitsForm.getOutfits().size(); i++)
                    {
                        Cursor getCurrentOutfitCursor = db.query(Constants.OUTFIT_TABLE_NAME, new String[]{ "*" }, Constants.OUTFIT_TABLE_COLUMNS_UUID + " = ?", new String[]{ wardrobeAllOutfitsForm.getOutfits().get(i).getUuid() }, null, null, null);

                        if(getCurrentOutfitCursor.getCount() == 0)
                        {
                            ArrayList<WardrobeElement> currentOutfitElements = new ArrayList<>();

                            for(int j = 0; j < wardrobeAllOutfitsForm.getOutfits().get(i).getElements().length; j++)
                            {
                                ArrayList<Integer> currentElementColors = new ArrayList<>();

                                for(int k = 0; k < wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].getColors().length; k++)
                                {
                                    currentElementColors.add(wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].getColors()[k]);
                                }

                                /****************************************************************************************************/

                                byte[] decode = Base64.decode(wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].getPicture(), Base64.DEFAULT);

                                Bitmap bmp = BitmapFactory.decodeByteArray(decode, 0, decode.length);

                                try
                                {
                                    File f = new File(dresscodeDirectory, Calendar.getInstance().getTimeInMillis() + String.valueOf(counter += 1) + ".jpg");
                                    f.createNewFile();
                                    FileOutputStream fo = new FileOutputStream(f);
                                    fo.write(decode);
                                    fo.close();

                                    wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].setPicture(Constants.DRESSCODE_APP_FOLDER + "/" + f.getName());

                                } catch(IOException e1)
                                {
                                    db.close();

                                    e1.printStackTrace();
                                }

                                /****************************************************************************************************/

                                currentOutfitElements.add(new WardrobeElement(0, wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].getType(), wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].getUuid(), currentElementColors, wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].getPicture(), true));
                            }

                            Outfit currentOutfit = new Outfit(wardrobeAllOutfitsForm.getOutfits().get(i).getName(), currentOutfitElements, wardrobeAllOutfitsForm.getOutfits().get(i).getUuid(), true);

                            currentOutfit.saveOutfitInDatabase(applicationContext);
                        }

                        else
                        {
                            db.delete(Constants.OUTFIT_ELEMENTS_TABLE_NAME, Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_OUTFIT_UUID + " = ?", new String[]{ wardrobeAllOutfitsForm.getOutfits().get(i).getUuid() });

                            Outfit currentOutfit = new Outfit(wardrobeAllOutfitsForm.getOutfits().get(i).getName(), new ArrayList<>(), wardrobeAllOutfitsForm.getOutfits().get(i).getUuid(), true);

                            currentOutfit.updateOutfitInDatabase(applicationContext);

                            for(int j = 0; j < wardrobeAllOutfitsForm.getOutfits().get(i).getElements().length; j++)
                            {
                                ContentValues currentOutfitCurrentElement = new ContentValues();
                                currentOutfitCurrentElement.put(Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_OUTFIT_UUID, wardrobeAllOutfitsForm.getOutfits().get(i).getUuid());
                                currentOutfitCurrentElement.put(Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_ELEMENT_UUID, wardrobeAllOutfitsForm.getOutfits().get(i).getElements()[j].getUuid());

                                db.insert(Constants.OUTFIT_ELEMENTS_TABLE_NAME, null, currentOutfitCurrentElement);
                            }
                        }

                        getCurrentOutfitCursor.close();
                    }

                    db.close();

                    try{ notifyObservers(true); } catch(Exception e){ e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(Call<WardrobeAllOutfitsForm> call, Throwable t)
            {
                db.close();

                try{ notifyObservers(false); } catch (Exception e){ e.printStackTrace(); }
            }
        });
    }

    /****************************************************************************************************/

    @Override
    public void addObserver(IGetDataFromApiObserver o)
    {
        this.observers.add(o);
    }

    /****************************************************************************************************/

    @Override
    public void removeObserver(IGetDataFromApiObserver o)
    {
        this.observers.remove(o);
    }

    /****************************************************************************************************/

    @Override
    public void notifyObservers(boolean succeeded) throws Exception
    {
        for(int i = 0; i < this.observers.size(); i++)
        {
            this.observers.get(i).taskDone(succeeded);
        }
    }

    /****************************************************************************************************/
}
