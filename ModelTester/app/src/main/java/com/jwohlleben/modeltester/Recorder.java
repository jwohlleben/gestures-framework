package com.jwohlleben.modeltester;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Recorder implements SensorEventListener
{
    private Context context;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private float[] lastAccelerometerValues;
    private float[] lastGyroscopeValues;
    private float[] tempValues;

    private boolean hasStarted;
    private boolean isFinished;
    private boolean startAutomatically;
    private boolean stopAutomatically;

    private int recordedFrames;
    private int frameLength;
    private int vectorLength;

    private List<float[]> values;

    public Recorder(Context context)
    {
        super();
        this.context = context;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        lastAccelerometerValues = new float[3];
        lastGyroscopeValues = new float[3];

        tempValues = null;
        values = null;

        hasStarted = false;
        isFinished = true;
        startAutomatically = false;
        stopAutomatically = false;

        recordedFrames = 0;
    }

    public void setAutostart(boolean startAutomatically)
    {
        this.startAutomatically = startAutomatically;
    }

    public boolean getAutostart()
    {
        return startAutomatically;
    }

    public void setAutostop(boolean stopAutomatically)
    {
        this.stopAutomatically = stopAutomatically;
    }

    public boolean getAutostop()
    {
        return stopAutomatically;
    }

    public void setVectorLength(int vectorLength)
    {
        this.vectorLength = vectorLength;
    }

    public int getVectorLength()
    {
        return vectorLength;
    }

    public void setFrameLength(int frameLength)
    {
        this.frameLength = frameLength;
    }

    public int getFrameLength()
    {
        return frameLength;
    }

    public void start()
    {
        tempValues = new float[6];
        values = new ArrayList<>();

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);

        isFinished = false;
        hasStarted = false;

        recordedFrames = 0;
    }

    public void stop()
    {
        sensorManager.unregisterListener(this);

        isFinished = true;
        hasStarted = false;
    }

    public boolean isFinished()
    {
        return isFinished;
    }

    public boolean hasStarted()
    {
        return hasStarted;
    }

    public List<float[]> getValues()
    {
        return values;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                lastAccelerometerValues = event.values;
                break;

            case Sensor.TYPE_GYROSCOPE:
                lastGyroscopeValues = event.values;
                break;

            default:
                Toast.makeText(context, "RecError: Unknown Sensor!", Toast.LENGTH_SHORT).show();
        }

        float vector = .0f;

        for (int i = 0; i < lastAccelerometerValues.length; i++)
        {
            tempValues[i] = lastAccelerometerValues[i];
            vector += Math.pow(lastAccelerometerValues[i], 2);
        }

        for (int i = 0; i < lastGyroscopeValues.length; i++)
        {
            tempValues[i + 3] = lastGyroscopeValues[i];
            vector += Math.pow(lastGyroscopeValues[i], 2);
        }

        vector = (float) Math.sqrt(vector);

        if (startAutomatically)
        {
            if (!hasStarted)
            {
                if (vector > vectorLength)
                    hasStarted = true;
                else
                    return;
            }
        }

        values.add(tempValues);
        recordedFrames++;
        tempValues = new float[6];

        if (stopAutomatically && recordedFrames >= frameLength && vector <= vectorLength)
            stop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }
}
