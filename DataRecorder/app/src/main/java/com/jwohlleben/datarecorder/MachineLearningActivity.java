package com.jwohlleben.datarecorder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MachineLearningActivity extends AppCompatActivity
{
    private final static int TIMER_DELAY = 40;

    private String projectFilePath;
    private String projectDirectoryPath;

    private ArrayAdapter<ProjectClass> adapter;

    private XMLReader xmlReader;

    private int currentId;
    private int lastId;
    private int vectorSensitivity;
    private int frameLength;

    private boolean isRecording;
    private boolean isAutostartActive;
    private boolean isAutostopActive;
    private boolean buttonTextChanged;
    private boolean criticalError;

    private Recorder recorder;

    private Button buttonSave;
    private Button buttonRecord;

    private Handler handler;
    private Runnable timer;

    private CSVWriter csvWriter;

    private List<float[]> values;

    private void showErrorAndReturn()
    {
        new AlertDialog.Builder(this)
                .setTitle("An error occurred")
                .setMessage("An error occurred while loading the project." +
                        "This can have the following possible reasons:\n\n" +
                        "\u2022 The opened file is not a project file\n" +
                        "\u2022 The project file is damaged\n" +
                        "\u2022 Project creation failed\n\n" +
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_learning);

        criticalError = false;

        Intent intent = getIntent();
        projectFilePath = intent.getStringExtra("PROJECT_FILE_PATH");

        isRecording = false;
        isAutostartActive = false;
        isAutostopActive = false;
        buttonTextChanged = false;

        vectorSensitivity = 2;
        frameLength = 64;

        values = new ArrayList<>();

        xmlReader = new XMLReader();
        xmlReader.setFilePath(projectFilePath);

        recorder = new Recorder(this);
        recorder.setVectorLength(vectorSensitivity);
        recorder.setFrameLength(frameLength);

        if (projectFilePath != null && !projectFilePath.isEmpty())
        {
            File file = new File(projectFilePath);
            projectDirectoryPath = file.getParent();
        }
        else
        {
            showErrorAndReturn();
        }

        if (projectDirectoryPath == null || projectDirectoryPath.isEmpty())
            showErrorAndReturn();

        if (!xmlReader.read())
            showErrorAndReturn();

        csvWriter = new CSVWriter(this, projectDirectoryPath);

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

        final TextView textLabel = (TextView) findViewById(R.id.machine_learning_select_label);
        textLabel.setText(String.format(getResources().getString(R.string.machine_learning_select_label), classes[0].getLabel()));

        for (int i = 0; i < classes.length; i++)
            adapter.add(classes[i]);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                currentId = adapter.getItem(position).getId();
                textLabel.setText(String.format(getResources().getString(R.string.machine_learning_select_label), adapter.getItem(position).getLabel()));
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
        final SeekBar seekBarFrames = findViewById(R.id.seek_frames);

        final TextView textSeekVector = findViewById(R.id.text_seek_vector_sensitivity);
        final TextView textSeekFrames = findViewById(R.id.text_seek_frames);

        textSeekVector.setText(String.format(getResources().getString(R.string.machine_learning_auto_record_vector), seekBarVector.getProgress() + 1));
        textSeekFrames.setText(String.format(getResources().getString(R.string.machine_learning_frames), seekBarFrames.getProgress() + 1));

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

        seekBarFrames.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                textSeekFrames.setText(String.format(getResources().getString(R.string.machine_learning_frames), progress + 1));
                recorder.setFrameLength(progress + 1);
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



        buttonSave = findViewById(R.id.button_save_recording);
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

                    buttonSave.setEnabled(true);

                    toggleButtonStart.setEnabled(true);
                    toggleButtonStop.setEnabled(true);

                    seekBarFrames.setEnabled(true);
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

                    buttonSave.setEnabled(false);

                    toggleButtonStart.setEnabled(false);
                    toggleButtonStop.setEnabled(false);

                    seekBarFrames.setEnabled(false);
                    seekBarVector.setEnabled(false);
                }

                buttonTextChanged = false;
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(csvWriter.createFile(lastId, values))
                    Toast.makeText(MachineLearningActivity.this, "Successfully created file", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MachineLearningActivity.this, "Error while creating file", Toast.LENGTH_SHORT).show();

                buttonSave.setEnabled(false);
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
        buttonSave.setEnabled(false);
    }
}
