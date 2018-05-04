package fr.hexus.dresscode.dresscode;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends FragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);
    }

    public void onOpenMenu(View view)
    {
        startActivity(new Intent(this, MenuActivity.class));
    }
}
