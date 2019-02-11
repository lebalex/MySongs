package xyz.lebalex.mysongs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class SongEditActivity extends AppCompatActivity {
    private String songFileName,songFileNameOnly;
    private EditText editText;
    private boolean textChange=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_edit);
        songFileName = getIntent().getStringExtra("filename");
        songFileNameOnly = (new File(songFileName)).getName();

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(songFileNameOnly);

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(FileHelper.getFileContext(songFileName));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChange=true;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 43) {
            try {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                    File f = new File(uri.getPath());
                    String snn = f.getName();
                    if(songFileNameOnly.equals(snn)) {
                        ParcelFileDescriptor pfd = getApplicationContext().getContentResolver().
                                openFileDescriptor(uri, "w");
                        FileOutputStream fileOutputStream =
                                new FileOutputStream(pfd.getFileDescriptor());
                        fileOutputStream.write((editText.getText().toString()).getBytes());
                        fileOutputStream.close();
                        pfd.close();
                        textChange = false;
                        setResult(RESULT_OK);
                        finish();
                    }else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Имя файла должно быть "+songFileNameOnly).show();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        try {
                            //FileHelper.writeToFile(songFileName, editText, getApplicationContext());

                            Uri.Builder uri = new Uri.Builder().clearQuery();
                            uri.encodedPath(songFileName);



                            int WRITE_REQUEST_CODE = 43;
                            //Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TITLE, songFileNameOnly);
                            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri.build());
                            startActivityForResult(intent, WRITE_REQUEST_CODE);

                        }
                        catch (Exception e) {
                            //Log.e("Exception", "File write failed: " + e.toString());
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                        //finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        setResult(RESULT_CANCELED);
                        finish();
                        break;
                }
            }
        };

        if(textChange) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Текст изменен, сохранить?").setPositiveButton("Да", dialogClickListener)
                    .setNegativeButton("Нет", dialogClickListener).show();
        }else
            finish();
        return;
    }
}
