package fr.hexus.dresscode.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.enums.Colors;
import fr.hexus.dresscode.enums.Types;
import fr.hexus.dresscode.classes.GlideApp;

public class WardrobeAddElement extends AppCompatActivity
{
    private static final int STORAGE = 0;
    private static final int CAMERA = 1;

    private ImageView picture;
    private Button addPicture;
    private String wardrobeElementPicturePath;
    private DrawerLayout myDrawer;
    private NavigationView dresscodeMenu;

    private Spinner wardrobeElementType;
    private Spinner wardrobeElementColor;

    private FloatingActionButton wardrobeElementSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_add_element);

        myDrawer = findViewById(R.id.myDrawer);

        picture = findViewById(R.id.wardrobeAddFormPicture);
        addPicture = findViewById(R.id.wardrobeAddFormPictureButton);
        wardrobeElementSave = findViewById(R.id.wardrobeAddFormSave);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_return_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.header_title_add_wardrobe_element));
        getSupportActionBar().setIcon(R.drawable.ic_add_circle_white);

        if(savedInstanceState != null)
        {
            if(savedInstanceState.getString("picturePath") != null)
            {
                wardrobeElementPicturePath = savedInstanceState.getString("picturePath");

                GlideApp.with(this)
                        .load(wardrobeElementPicturePath)
                        .centerInside()
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(picture);
            }
        }

        wardrobeElementSave.setVisibility(View.GONE);

        fillTypeSpinner();
        fillColorsSpinner();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("picturePath", wardrobeElementPicturePath);
    }

    public void fillTypeSpinner()
    {
        String[] array = new String[Types.values().length];

        for(int i = 0; i < Types.values().length; i++)
        {
            array[i] = getResources().getString(getResources().getIdentifier(String.valueOf(Types.values()[i]), "string", getPackageName()));
        }

        wardrobeElementType = findViewById(R.id.wardrobeAddFormType);

        wardrobeElementType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                checkForm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        wardrobeElementType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, array));
    }

    public void fillColorsSpinner()
    {
        String[] array = new String[Colors.values().length];

        for(int i = 0; i < Colors.values().length; i++)
        {
            array[i] = getResources().getString(getResources().getIdentifier(String.valueOf(Colors.values()[i]), "string", getPackageName()));
        }

        wardrobeElementColor = findViewById(R.id.wardrobeAddFormColor);

        wardrobeElementColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                checkForm();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        wardrobeElementColor.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, array));
    }

    public void addAPicture(View view)
    {
        showPictureDialog();
    }

    private void showPictureDialog()
    {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);

        pictureDialog.setTitle(getResources().getString(R.string.wardrobe_add_form_picture_prompt_title));

        String[] pictureDialogItems = { getResources().getString(R.string.wardrobe_add_form_picture_prompt_gallery), getResources().getString(R.string.wardrobe_add_form_picture_prompt_camera) };

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int choice)
            {
                switch(choice)
                {
                    case STORAGE:
                        takePictureFromStorage();
                        break;
                    case CAMERA:
                        takePictureFromCamera();
                        break;
                }
            }
        });

        pictureDialog.show();
    }

    public void takePictureFromStorage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, STORAGE);
    }

    private void takePictureFromCamera()
    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;

            try
            {
                photoFile = createImageFile();

            } catch(IOException ex)
            {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            if(photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException
    {
        String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File picture = File.createTempFile(imageFileName, ".jpg", storageDir);

        wardrobeElementPicturePath = picture.getAbsolutePath();
        return picture;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == this.RESULT_CANCELED)
        {
            return;
        }

        else if(requestCode == STORAGE)
        {
            Uri contentURI = data.getData();

            wardrobeElementPicturePath = getRealPathFromURI(this, contentURI);

            GlideApp.with(this)
                    .load(wardrobeElementPicturePath)
                    .centerInside()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(picture);
        }

        else if(requestCode == CAMERA)
        {
            GlideApp.with(this)
                    .load(wardrobeElementPicturePath)
                    .centerInside()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(picture);
        }

        checkForm();

        addPicture.setText(getResources().getString(R.string.wardrobe_add_form_picture_change));
    }

    public String getRealPathFromURI(Context context, Uri contentURI)
    {
        Cursor cursor = null;

        try
        {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentURI,  proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);

        } finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
        }
    }

    public void checkForm()
    {
        boolean formIsReady = true;

        if(wardrobeElementType.getSelectedItem().toString().length() == 0) formIsReady = false;
        if(wardrobeElementColor.getSelectedItem().toString().length() == 0) formIsReady = false;
        if(picture.getDrawable() == null) formIsReady = false;

        if(formIsReady)
        {
            wardrobeElementSave.setVisibility(View.VISIBLE);
        }

        else
        {
            wardrobeElementSave.setVisibility(View.GONE);
        }
    }

    public void onSubmitForm(View view)
    {
        int type = wardrobeElementType.getSelectedItemPosition() + 1;
        int color = wardrobeElementColor.getSelectedItemPosition() + 1;

        BitmapDrawable draw = (BitmapDrawable) picture.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        String path = savePictureInDresscodeFolder(bitmap);

        if(path.length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.wardrobe_view_image_could_not_be_save), Toast.LENGTH_LONG).show();
        }

        else
        {
            AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

            SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(Constants.WARDROBE_TABLE_COLUMNS_TYPE, type);
            values.put(Constants.WARDROBE_TABLE_COLUMNS_PATH, path);
            values.put(Constants.WARDROBE_TABLE_COLUMNS_COLOR, color);

            long insertedRowId = db.insert(Constants.WARDROBE_TABLE_NAME, null, values);

            if(insertedRowId > 0)
            {
                Toast.makeText(this, R.string.new_wardrobe_element_saved, Toast.LENGTH_LONG).show();
                finish();
            }

            else
            {
                Toast.makeText(this, getResources().getString(R.string.could_not_save_wardrobe_element), Toast.LENGTH_SHORT).show();
            }

            appDatabaseCreation.close();
        }
    }

    public String savePictureInDresscodeFolder(Bitmap myBitmap)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File dresscodeDirectory = new File(Environment.getExternalStorageDirectory() + "/Dresscode");

        if(!dresscodeDirectory.exists())
        {
            dresscodeDirectory.mkdirs();
        }

        try
        {
            File f = new File(dresscodeDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this, new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);
            fo.close();

            return "/Dresscode/" + f.getName();

        } catch(IOException e1)
        {
            e1.printStackTrace();
        }

        return "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
