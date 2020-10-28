package com.jwohlleben.datarecorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewProjectActivity extends AppCompatActivity
{
    private static final int REQUEST_ADD_CLASS = 0;
    private static final int REQUEST_UPDATE_CLASS = 1;

    private String internalPath;

    private ArrayAdapter<ProjectClass> adapter;

    private XMLWriter xmlWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);

        internalPath = Environment.getExternalStorageDirectory().toString();

        xmlWriter = new XMLWriter();

        final Button buttonAddLabel = findViewById(R.id.button_add_class);
        final Button buttonCreateProject = findViewById(R.id.button_create_new_project);
        final EditText editTextName = findViewById(R.id.edit_name);
        final EditText editTextDescription = findViewById(R.id.edit_description);

        final ListView listView = findViewById(R.id.list_view_new_project);
        adapter = new ArrayAdapter<ProjectClass>(this, R.layout.class_item);
        listView.setAdapter(adapter);

        buttonAddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(NewProjectActivity.this, AddClassActivity.class);
                startActivityForResult(intent, REQUEST_ADD_CLASS);
            }
        });

        buttonCreateProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(editTextName.getText()))
                {
                    Toast.makeText(NewProjectActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fullPath = internalPath + '/' + DateTime.getTimestamp() + "/project.xml";

                ProjectClass.resetGlobalId();

                xmlWriter.setFilePath(fullPath);
                xmlWriter.setName(editTextName.getText().toString());
                xmlWriter.setDescription(editTextDescription.getText().toString());

                for (int i = 0; i < adapter.getCount(); i++)
                    xmlWriter.addClass(adapter.getItem(i).getLabel(), adapter.getItem(i).getDescription());

                xmlWriter.write();

                Intent intent = new Intent(NewProjectActivity.this, MachineLearningActivity.class);
                intent.putExtra("PROJECT_FILE_PATH", fullPath);
                NewProjectActivity.this.startActivity(intent);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(NewProjectActivity.this, AddClassActivity.class);
                intent.putExtra("INDEX", position);
                intent.putExtra("LABEL", adapter.getItem(position).getLabel());
                intent.putExtra("DESCRIPTION", adapter.getItem(position).getDescription());
                startActivityForResult(intent, REQUEST_UPDATE_CLASS);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                adapter.remove(adapter.getItem(position));
                return false;
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_CLASS && resultCode == RESULT_OK)
        {
            adapter.add(new ProjectClass(-1, data.getStringExtra("LABEL"), data.getStringExtra("DESCRIPTION")));
        }

        if (requestCode == REQUEST_UPDATE_CLASS && resultCode == RESULT_OK)
        {
            int index = data.getIntExtra("INDEX", -1);

            if (index == -1)
            {
                Log.d("NewProjectActivity", "Wrong index!");
                return;
            }

            adapter.getItem(index).setLabel(data.getStringExtra("LABEL"));
            adapter.getItem(index).setDescription(data.getStringExtra("DESCRIPTION"));
            adapter.notifyDataSetChanged();
        }
    }
}
