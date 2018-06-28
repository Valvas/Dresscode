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
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.enums.Colors;
import fr.hexus.dresscode.enums.Types;
import fr.hexus.dresscode.classes.GlideApp;

public class WardrobeElementEdit extends AppCompatActivity
{
    private static final int STORAGE = 0;
    private static final int CAMERA = 1;

    private int selectedColors = 0;

    private ImageView picture;
    private Button addPicture;
    private String wardrobeElementPicturePath;
    private String wardrobeElementOldPicturePath;
    private LinearLayout colorsList;

    private WardrobeElement wardrobeElement;

    private Spinner wardrobeElementType;

    private FloatingActionButton wardrobeElementSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_element_edit);

        colorsList = findViewById(R.id.wardrobeAddFormColorsList);

        for(int i = 1; i <= Colors.values().length; i++)
        {
            String color = getResources().getString(getResources().getIdentifier(Colors.getKey(i), "string", getPackageName()));
            final CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkBox.setText(color);
            checkBox.setId(i);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    selectedColors += isChecked ? 1 : -1;
                    checkForm();
                }
            });

            colorsList.addView(checkBox);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_return_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.header_title_edit_wardrobe_element));
        getSupportActionBar().setIcon(R.drawable.ic_edit_white);

        picture = findViewById(R.id.wardrobeAddFormPicture);
        addPicture = findViewById(R.id.wardrobeAddFormPictureButton);
        wardrobeElementSave = findViewById(R.id.wardrobeAddFormSave);

        Intent intent = getIntent();
        wardrobeElement = (WardrobeElement) intent.getSerializableExtra("wardrobeElement");

        wardrobeElementPicturePath = wardrobeElement.getPath();

        wardrobeElementPicturePath = String.valueOf(Environment.getExternalStorageDirectory()) + wardrobeElementPicturePath;

        GlideApp.with(this)
                .load(wardrobeElementPicturePath)
                .centerInside()
                .placeholder(R.drawable.ic_launcher_background)
                .into(picture);

        fillTypeSpinner();
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

        wardrobeElementType.setSelection(wardrobeElement.getType() - 1);
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

        wardrobeElementOldPicturePath = wardrobeElementPicturePath;

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

            wardrobeElementOldPicturePath = wardrobeElementPicturePath;

            wardrobeElementPicturePath = getRealPathFromURI(this, contentURI);;

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

    /****************************************************************************************************/
    // CHECK FORM EACH TIME AN EVENT OCCUR
    /****************************************************************************************************/

    public void checkForm()
    {
        boolean formIsReady = true;

        if(wardrobeElementType.getSelectedItem().toString().length() == 0) formIsReady = false;
        if(picture.getDrawable() == null) formIsReady = false;
        if(selectedColors <= 0) formIsReady = false;

        if(formIsReady)
        {
            wardrobeElementSave.setVisibility(View.VISIBLE);
        }

        else
        {
            wardrobeElementSave.setVisibility(View.GONE);
        }
    }

    /****************************************************************************************************/
    // SAVE DATA ON SD CARD AND SEND THEM TO THE API
    /****************************************************************************************************/

    public void onSubmitForm(View view)
    {
        int type = wardrobeElementType.getSelectedItemPosition() + 1;

        BitmapDrawable draw = (BitmapDrawable) picture.getDrawable();
        Bitmap bitmap = draw.getBitmap();

        String path = savePictureInDresscodeFolder(bitmap);

        ArrayList<Integer> colors = new ArrayList<>();

        for(int x = 0; x < colorsList.getChildCount(); x++)
        {
            CheckBox currentColor = (CheckBox) colorsList.getChildAt(x);

            if(currentColor.isChecked()) colors.add(currentColor.getId());
        }

        if(path.length() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.wardrobe_view_image_could_not_be_save), Toast.LENGTH_LONG).show();
        }

        else
        {
            wardrobeElement.setColors(colors);
            wardrobeElement.setType(type);
            wardrobeElement.setPath(path);

            if(wardrobeElement.updateWardrobeElementInDatabase(this))
            {
                Toast.makeText(this, R.string.new_wardrobe_element_saved, Toast.LENGTH_LONG).show();
                Intent finishIntent = new Intent();
                finishIntent.putExtra("wardrobeElement", wardrobeElement);
                setResult(RESULT_OK, finishIntent);
                finish();
            }

            else
            {
                Toast.makeText(this, getResources().getString(R.string.could_not_save_wardrobe_element), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /****************************************************************************************************/

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

            if(wardrobeElementOldPicturePath != null)
            {
                if(!wardrobeElementOldPicturePath.equals(wardrobeElementPicturePath))
                {
                    File fileToDelete = new File(wardrobeElementOldPicturePath);

                    boolean b = fileToDelete.delete();
                }
            }

            return "/Dresscode/" + f.getName();

        } catch(IOException e1)
        {
            e1.printStackTrace();
        }

        return "";
    }
}
