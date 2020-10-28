package com.jwohlleben.modeltester;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class Classifier
{
    private Interpreter tflite;
    private Context context;

    private String modelPath;

    private int frameLength;
    private int outputNeurons;

    public Classifier(Context context)
    {
        this.context = context;
    }

    private MappedByteBuffer loadModelFile() throws IOException
    {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private float[][] listToArray(List<float[]> values)
    {
        int frames = values.size() - frameLength + 1;

        if (frames <= 0)
            return null;

        float[][] result = new float[frames][frameLength * 6];

        for (int i = 0; i < frames; i++)
        {
            for (int j = i; j < frameLength - 1 + i; j++)
            {
                result[i][j - i + 0 * frameLength] = values.get(j)[0]; // x_acc
                result[i][j - i + 1 * frameLength] = values.get(j)[1]; // y_acc
                result[i][j - i + 2 * frameLength] = values.get(j)[2]; // z_acc
                result[i][j - i + 3 * frameLength] = values.get(j)[3]; // x_gyro
                result[i][j - i + 4 * frameLength] = values.get(j)[4]; // y_gyro
                result[i][j - i + 5 * frameLength] = values.get(j)[5]; // z_gyro
            }

        }

        return result;
    }

    public boolean init()
    {
        try
        {
            tflite = new Interpreter(new File(modelPath));
        }

        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void setModelPath(String modelPath)
    {
        this.modelPath = modelPath;
    }

    public String getModelPath()
    {
        return modelPath;
    }

    public void setFrameLength(int frameLength)
    {
        this.frameLength = frameLength;
    }

    public int getFrameLength()
    {
        return frameLength;
    }

    public void setOutputNeurons(int outputNeurons)
    {
        this.outputNeurons = outputNeurons;
    }

    public int getOutputNeurons()
    {
        return outputNeurons;
    }

    public float[] classify(List<float[]> values)
    {
        float[][] output = new float[1][1];

        float[][] input = listToArray(values);

        if (input == null)
            return null;

        float[] result = new float[input.length];

        for (int i = 0; i < input.length; i++)
        {
            tflite.run(input[0], output);

            result[i] = output[0][0];
        }

        return result;
    }
}
