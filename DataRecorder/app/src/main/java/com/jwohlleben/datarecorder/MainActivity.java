package com.jwohlleben.datarecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_PERMISSIONS = 0;
    private static final int REQUEST_FILE_SELECT = 1;

    private Handler handler;

    private boolean checkPermissions()
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button createNewProjectButton = findViewById(R.id.button_create_new_project);
        final Button selectExistingProjectButton = findViewById(R.id.button_select_existing_project);
        final Button requestPermissionsButton = findViewById(R.id.request_permissions);

        createNewProjectButton.setEnabled(false);
        selectExistingProjectButton.setEnabled(false);

        final TextView permissionsMissingText = findViewById(R.id.text_permissions_missing);

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (checkPermissions())
                {
                    requestPermissionsButton.setVisibility(View.INVISIBLE);
                    permissionsMissingText.setVisibility(View.INVISIBLE);
                    createNewProjectButton.setEnabled(true);
                    selectExistingProjectButton.setEnabled(true);
                    return;
                }

                handler.postDelayed(this, 200);
            }
        };

        if (checkPermissions())
            r.run();
        else
            handler.postDelayed(r, 200);

        if (!checkPermissions())
            requestPermissions();

        createNewProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, NewProjectActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        selectExistingProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_FILE_SELECT);
            }
        });

        requestPermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                requestPermissions();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            Uri selectedFile = data.getData();
            UriHandler uriHandler = new UriHandler(this);
            Intent intent = new Intent(MainActivity.this, MachineLearningActivity.class);
            intent.putExtra("PROJECT_FILE_PATH", uriHandler.getPath(selectedFile));
            MainActivity.this.startActivity(intent);
        }
    }
}
