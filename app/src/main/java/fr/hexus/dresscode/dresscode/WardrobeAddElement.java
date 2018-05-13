package fr.hexus.dresscode.dresscode;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class WardrobeAddElement extends AppCompatActivity
{
    private static final int STORAGE = 0;
    private static final int CAMERA = 1;

    private ImageView picture;
    private Button addPicture;
    private String wardrobeElementPicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_add_element);

        picture = findViewById(R.id.wardrobeAddFormPicture);
        addPicture = findViewById(R.id.wardrobeAddFormPictureButton);

        picture.setScaleType(ImageView.ScaleType.FIT_XY);

        fillTypeSpinner();
        fillColorsSpinner();
    }

    public void onReturn(View view)
    {
        finish();
    }

    public void fillTypeSpinner()
    {
        Spinner wardrobeElementType = findViewById(R.id.wardrobeAddFormType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wardrobe_element_types, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        wardrobeElementType.setAdapter(adapter);
    }

    public void fillColorsSpinner()
    {
        Spinner wardrobeElementColor = findViewById(R.id.wardrobeAddFormColor);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wardrobe_element_colors, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        wardrobeElementColor.setAdapter(adapter);
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
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(picture);
        }

        else if(requestCode == CAMERA)
        {
            GlideApp.with(this)
                    .load(wardrobeElementPicturePath)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(picture);


        }

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

    public void checkForm(View view)
    {
        boolean formIsReady = true;

        if(formIsReady)
        {
            //SAVE DATA
        }
    }

    public void onSubmitForm(View view)
    {

    }

    /*public String savePictureInDresscodeFolder(Bitmap myBitmap)
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

            return f.getAbsolutePath();

        } catch(IOException e1)
        {
            e1.printStackTrace();
        }

        return "";
    }*/
}
