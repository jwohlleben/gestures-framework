package com.jwohlleben.modeltester;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_XML_FILE_SELECT = 0;
    private final int REQUEST_TFLITE_FILE_SELECT = 1;

    private final int TIMER_DELAY = 200;

    private boolean xmlFileSelected;
    private boolean tfliteFileSelected;

    private Uri xmlUri;
    private Uri tfliteUri;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xmlFileSelected = false;
        tfliteFileSelected = false;

        handler = new Handler();

        final Button buttonXml = findViewById(R.id.button_select_xml);
        buttonXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_XML_FILE_SELECT);
            }
        });

        final Button buttonTflite = findViewById(R.id.button_select_tflite);
        buttonTflite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_TFLITE_FILE_SELECT);
            }
        });

        final Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, MachineLearningActivity.class);
                intent.putExtra("XML_FILE", xmlUri.toString());
                intent.putExtra("TFLITE_FILE", tfliteUri.toString());

                MainActivity.this.startActivity(intent);
            }
        });

        buttonStart.setEnabled(false);

        final Runnable r = new Runnable() {
            public void run()
            {
                if (xmlFileSelected && tfliteFileSelected)
                {
                    buttonStart.setEnabled(true);
                    return;
                }
                else
                {
                    buttonStart.setEnabled(false);
                }

                handler.postDelayed(this, TIMER_DELAY);
            }
        };

        handler.postDelayed(r, TIMER_DELAY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_XML_FILE_SELECT && resultCode == RESULT_OK)
        {
            xmlUri = data.getData();
            xmlFileSelected = true;
        }

        if (requestCode == REQUEST_TFLITE_FILE_SELECT && resultCode == RESULT_OK)
        {
            tfliteUri = data.getData();
            tfliteFileSelected = true;
        }
    }
}
