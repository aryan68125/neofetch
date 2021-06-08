package com.aditya.neomorphicui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String File_NAME="Password.txt";
    EditText editText;
    TextView textView2;
    Button open_file;
    Button save_file;

    //Strings related to file name and file directory
    String FileName="";
    String FileDirectory = "";
    String FileContent = "";

    //creating a variable to store data while writing
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.EditText);
        textView2 = findViewById(R.id.textView2);
        save_file=findViewById(R.id.save);
        open_file = findViewById(R.id.open);

        //initialize two String variables for storing file name and file path
        FileName = "Password.txt";
        FileDirectory = "PasswordDir";

        save_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!editText.getText().toString().equals("")) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)  //checking our bhuild version of the os greater than marshmallow version
                    {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                            //show popup for runtime permissions
                            requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                        } else {
                            //permission already granted; hence save data
                            //text typed inside the editText
                            String save = editText.getText().toString();
                            saveToTxtFile(save);
                        }
                    }//if statement closed

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Input filed is empty!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //checking permission and requesting permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE: {
                // if request is canceled then result arrays are empty
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String save = editText.getText().toString();
                    saveToTxtFile(save);
                } else {
                    Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //actually saving data onto the file system by creating a directory and a txt file
    private void saveToTxtFile(String save) {
        //get current Time for file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        try{
            //path to storage in the file system
            //File path = Environment.getExternalStorageDirectory();

            //getting the public directory
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            //create folder name "Brain_Trainer_game"
            File dir = new File(path+"/versus");
            dir.mkdirs();

            //File name
            String Filename = "Password" + timeStamp + ".txt";

            //creating new file
            File file = new File(dir, Filename);

            //FileWriter class s used to store characters in file
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            //save = String text data to be saved
            bw.write(save);
            bw.close();

            //Showing file name that was just created
            Toast.makeText(this,Filename + "is saved\n" +dir, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            //if anything goes wrong
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}