package com.aditya.neomorphicui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String File_NAME="Password.txt";
    EditText editText;
    TextView textView2;
    Button open_file;
    Button save_file;
    Button get_all_files;

    //Strings related to file name and file directory
    String FileName="";
    String FileContent = "";

    // After API 23 the permission request for accessing external storage is changed
    // Before API 23 permission request is asked by the user during installation of app
    // After API 23 permission request is asked at runtime
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    //button that will open an application
    Button open_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.EditText);
        textView2 = findViewById(R.id.textView2);
        save_file=findViewById(R.id.save);
        open_file = findViewById(R.id.open);
        get_all_files = findViewById(R.id.get_all_files);
        open_app=findViewById(R.id.open_app);

        // Requesting Permission to access External Storage
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_PERMISSION_CODE);

        //initialize two String variables for storing file name and file path
        FileName = "Password.txt";

        //open application using intent
        open_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Performs action on click
                //it will send "This is my text to send" to whatsapp app so that user can send this message to his/her contacts
                //text/plain will tell this intent that the data sent is just a plain text
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(sendIntent, ""));
                startActivity(sendIntent);
                //opens the portfolio details class
            }
        });

        save_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileContent = editText.getText().toString();
                try {
                    ContentValues values = new ContentValues();

                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Password");       //file name
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");        //file extension, will automatically add to file
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Password/");     //end "/" is not mandatory

                    Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);      //important! external storage

                    OutputStream outputStream = getContentResolver().openOutputStream(uri);

                    //FileContent String contains the text content of the file
                    outputStream.write(FileContent.getBytes());

                    outputStream.close();

                    Toast.makeText(getApplicationContext(), "File created successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Fail to create file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        open_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri contentUri = MediaStore.Files.getContentUri("external"); //tells which storage device is to be used

                String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";

                String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/Password/"};

                Cursor cursor = getContentResolver().query(contentUri, null, selection, selectionArgs, null);

                Uri uri = null;

                if (cursor.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "No file found in \"" + Environment.DIRECTORY_DOCUMENTS + "/Password/\"", Toast.LENGTH_LONG).show();
                } else {
                    while (cursor.moveToNext()) {
                        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                      //implement  for loop to read the multiple Passwords text file
                        if (fileName.equals("Password (1).txt")) {
                            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                            uri = ContentUris.withAppendedId(contentUri, id);

                            break;
                        }
                    }

                    if (uri == null) {
                        //implement  for loop to read the multiple Passwords text file
                        Toast.makeText(getApplicationContext(), "\"Password.txt\" not found", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);

                            int size = inputStream.available();

                            byte[] bytes = new byte[size];

                            inputStream.read(bytes); //this will read the contents of the text file line by line

                            inputStream.close();

                            String jsonString = new String(bytes, StandardCharsets.UTF_8); //this will store the read contents in string format of Password.txt file
                            textView2.setText(jsonString);
                            Log.i("content_of_text_file",jsonString);
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Fail to read file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //creating a list that will hold the list of text files present in the custom directory external_storage/Documents/Password
        ArrayList<File> inFiles = new ArrayList<File>();

        get_all_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS); //getting external storage directory
                    //it's another method to get the directory of a file inside the external storage of a device
                    File folder = new File(externalStorageDirectory + "/Password");
                    File file[] = folder.listFiles();
                    if (file.length != 0) {
                        for (int i = 0; i < file.length; i++) {
                            //here populate your list
                            inFiles.add(file[i]);

                        }
                    } else {
                        //no file available
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
                //checking if th inFiles list is populated or not
                //this list will store the list of files with their directory location
                Log.i("list", inFiles.toString());

                //now we can extract file names and their contents and show them inside the textView
                for (int l = 0; l < inFiles.size(); l++) {
                    String jsonString="";
                    //file_name: /storage/emulated/0/Documents/Password/Password.txt it store the entire directory as a string
                    String file_directory = inFiles.get(l).toString() + "\n";
                    Log.i("file_directory", file_directory);

                    //now we can extract the file name from the extracted file's directory
                    // create object of Path
                    Path path = Paths.get(file_directory);

                    // call getFileName() and get FileName path object
                    //Path fileName contains the name of the file
                    Path fileNamePath = path.getFileName();

                    Log.i("file_name", fileNamePath.toString());

                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    //now here we can read files from the documents directory
                    File file = new File(file_directory);
                    String data = getdata(file);
                    Log.i("file_read",file.toString());
                    if (data != null) {
                        Log.i("file_content",data);
                    } else {
                        Toast.makeText(getApplicationContext(),"File not found!",Toast.LENGTH_LONG).show();
                    }

                }
            }

        });
    }//oncreate closed

    // getdata() is the method which reads the data
    // the data that is saved in byte format in the file
    private String getdata(File myfile) {
        FileInputStream fileInputStream = null;
        try {
            Log.i("file_read_send_to_getData_function",myfile.toString());
            fileInputStream = new FileInputStream(myfile);
            int i = -1;
            StringBuffer buffer = new StringBuffer();
            while ((i = fileInputStream.read()) != -1) {
                buffer.append((char) i);
            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}