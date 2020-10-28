package com.jwohlleben.datarecorder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddClassActivity extends AppCompatActivity
{
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        final EditText editTextLabel = findViewById(R.id.edit_class_label);
        final EditText editTextDescription = findViewById(R.id.edit_class_description);
        final Button buttonCancel = findViewById(R.id.button_add_class_cancel);
        final Button buttonSave = findViewById(R.id.button_add_class_save);

        Intent intent = getIntent();
        index = intent.getIntExtra("INDEX", -1);

        if (index != -1)
        {
            String label = intent.getStringExtra("LABEL");
            String description = intent.getStringExtra("DESCRIPTION");

            editTextLabel.setText(label);
            editTextDescription.setText(description);
        }

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(editTextLabel.getText()))
                {
                    Toast.makeText(AddClassActivity.this, "Please enter a label", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();

                intent.putExtra("INDEX", index);
                intent.putExtra("LABEL", editTextLabel.getText().toString());
                intent.putExtra("DESCRIPTION", editTextDescription.getText().toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
