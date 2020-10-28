package com.jwohlleben.modeltester;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MachineLearningActivity extends AppCompatActivity
{
    private final static int TIMER_DELAY = 40;

    private Uri xmlUri;
    private Uri tfliteUri;

    private ArrayAdapter<ProjectClass> adapter;

    private XMLReader xmlReader;

    private Classifier classifier;

    private int currentId;
    private int lastId;
    private int vectorSensitivity;

    private boolean isRecording;
    private boolean isAutostartActive;
    private boolean isAutostopActive;
    private boolean buttonTextChanged;
    private boolean criticalError;

    private Recorder recorder;

    private Button buttonRecord;

    private Handler handler;
    private Runnable timer;

    private List<float[]> values;

    private void showErrorAndReturn()
    {
        new AlertDialog.Builder(this)
                .setTitle("An error occurred")
                .setMessage("An error occurred while loading the project." +
                        "This can have the following possible reasons:\n\n" +
                        "\u2022 The opened files are not project files\n" +
                        "\u2022 The project files are damaged\n\n" +
                        "You will be taken to the home screen.")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showResults()
    {
        float[] result = classifier.classify(values);

        for (int i = 0; i < result.length; i++)
            adapter.getItem(i).setPossibility(result[i]);

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_learning);

        criticalError = false;

        Intent intent = getIntent();
        xmlUri = Uri.parse(intent.getStringExtra("XML_FILE"));
        tfliteUri = Uri.parse(intent.getStringExtra("TFLITE_FILE"));

        isRecording = false;
        isAutostartActive = false;
        isAutostopActive = false;
        buttonTextChanged = false;

        vectorSensitivity = 2;

        values = new ArrayList<>();

        xmlReader = new XMLReader();
        xmlReader.setFilePath(xmlUri.getPath());

        recorder = new Recorder(this);
        recorder.setVectorLength(vectorSensitivity);

        if (!xmlReader.read())
            showErrorAndReturn();

        classifier = new Classifier(this);
        classifier.setModelPath(tfliteUri.getPath());
        classifier.setFrameLength(xmlReader.getFrameLength());
        classifier.setOutputNeurons(xmlReader.getOutputNeurons());

        recorder.setFrameLength(xmlReader.getFrameLength());

        if (!classifier.init())
        {
            criticalError = true;
            return;
        }

        final TextView textProject = findViewById(R.id.machine_learning_project);
        textProject.setText(String.format(getResources().getString(R.string.machine_learning_project), xmlReader.getName()));

        final ListView listView = findViewById(R.id.list_view_machine_learning);
        adapter = new ArrayAdapter<ProjectClass>(this, R.layout.class_item);
        listView.setAdapter(adapter);

        ProjectClass[] classes = xmlReader.getClasses();

        if (classes == null)
        {
            criticalError = true;
            return;
        }

        currentId = classes[0].getId();

        for (int i = 0; i < classes.length; i++)
        {
            classes[i].setPossibility(0);
            adapter.add(classes[i]);
        }

        final TextView textLabel = (TextView) findViewById(R.id.machine_learning_label);
        textLabel.setText(String.format(getResources().getString(R.string.machine_learning_label), classes[0].getLabel()));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                currentId = adapter.getItem(position).getId();
                textLabel.setText(String.format(getResources().getString(R.string.machine_learning_label), adapter.getItem(position).getLabel()));
            }
        });

        final ToggleButton toggleButtonStart = findViewById(R.id.button_auto_record);
        final ToggleButton toggleButtonStop = findViewById(R.id.button_auto_stop);

        toggleButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isAutostartActive = toggleButtonStart.isChecked();
                recorder.setAutostart(isAutostartActive);
            }
        });

        toggleButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                isAutostopActive = toggleButtonStop.isChecked();
                recorder.setAutostop(isAutostopActive);
            }
        });

        final SeekBar seekBarVector = findViewById(R.id.seek_vector_sensitivity);

        final TextView textSeekVector = findViewById(R.id.text_seek_vector_sensitivity);

        textSeekVector.setText(String.format(getResources().getString(R.string.machine_learning_auto_record_vector), seekBarVector.getProgress() + 1));

        seekBarVector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                textSeekVector.setText(String.format(getResources().getString(R.string.machine_learning_auto_record_vector), progress + 1));
                recorder.setVectorLength(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        buttonRecord = findViewById(R.id.button_record);

        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (isRecording)
                {
                    if (isAutostartActive)
                        handler.removeCallbacks(timer);

                    isRecording = false;
                    recorder.stop();
                    buttonRecord.setText(getResources().getString(R.string.machine_learning_start_recording));

                    toggleButtonStart.setEnabled(true);
                    toggleButtonStop.setEnabled(true);

                    seekBarVector.setEnabled(true);
                }
                else
                {
                    isRecording = true;

                    handler.postDelayed(timer, TIMER_DELAY);

                    recorder.start();

                    if (isAutostartActive)
                        buttonRecord.setText(getResources().getString(R.string.machine_learning_is_waiting));
                    else
                        buttonRecord.setText(getResources().getString(R.string.machine_learning_is_recording));

                    toggleButtonStart.setEnabled(false);
                    toggleButtonStop.setEnabled(false);

                    seekBarVector.setEnabled(false);
                }

                buttonTextChanged = false;
            }
        });

        handler = new Handler();

        timer = new Runnable() {
            @Override
            public void run()
            {
                if (isRecording && isAutostartActive && recorder.hasStarted() && !buttonTextChanged)
                {
                    buttonRecord.setText(getResources().getString(R.string.machine_learning_is_recording));
                    buttonTextChanged = true;
                }

                if (isAutostopActive && recorder.isFinished())
                    buttonRecord.performClick();

                if (recorder.isFinished())
                {
                    values = recorder.getValues();
                    lastId = currentId;

                    showResults();

                    return;
                }

                handler.postDelayed(this, TIMER_DELAY);
            }
        };
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (criticalError)
            return;

        handler.removeCallbacks(timer);

        isRecording = false;
        recorder.stop();

        buttonRecord.setText(getResources().getString(R.string.machine_learning_start_recording));
    }
}
