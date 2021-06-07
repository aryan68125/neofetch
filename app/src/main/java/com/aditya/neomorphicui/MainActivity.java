package com.aditya.neomorphicui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String File_NAME="Password.txt";
    Button pick_file;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pick_file = findViewById(R.id.button);
        editText = findViewById(R.id.textView);

    }

    public void save(View v){
        String text = editText.getText().toString();
        //handeling dirctory where the file will be saved
        String PATH = "/remote/dir/server/";
        String fileName = text + ".txt";
        //now logic to create a text file
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(File_NAME,MODE_PRIVATE);
            //now writing the file
            try {
                fos.write(text.getBytes());
                //deleting contents of editText
                editText.getText().clear();
                Toast.makeText(getApplicationContext(),"soved to "+ getFilesDir()+" / "+File_NAME,Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(fos!=null){
                //closing the newly created file
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void load(View v){

        FileInputStream fis = null;
        try {
            fis = openFileInput(File_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while((text = br.readLine())!=null)
            {
               sb.append(text).append("\n");
            }
            editText.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}