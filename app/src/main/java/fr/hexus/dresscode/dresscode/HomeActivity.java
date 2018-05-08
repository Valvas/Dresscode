package fr.hexus.dresscode.dresscode;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class HomeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        File dresscodeDirectory = new File(Environment.getExternalStorageDirectory() + "/Dresscode");

        if(!dresscodeDirectory.exists())
        {
            if(!dresscodeDirectory.mkdirs())
            {
                Toast.makeText(this, R.string.could_no_create_app_directory, Toast.LENGTH_LONG).show();

                finish();
            }
        }
    }

    public void onOpenMenu(View view)
    {
        startActivity(new Intent(this, MenuActivity.class));
    }
}
