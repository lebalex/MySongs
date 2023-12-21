package xyz.lebalex.mysongs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class SongEditActivity extends AppCompatActivity {
    private String songFileName,songFileNameOnly;
    private EditText editText;
    private boolean textChange=false;
    private ActivityResultLauncher<Intent> startActivityIntent;
    private static Activity thisActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisActivity = this;
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
        startActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri uri = null;
                            Intent data = result.getData();
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                                    builder.setMessage("Имя файла должно быть "+songFileNameOnly).show();
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
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
                                    startActivityIntent.launch(intent);

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    builder.setMessage("Текст изменен, сохранить?").setPositiveButton("Да", dialogClickListener)
                            .setNegativeButton("Нет", dialogClickListener).show();
                }else
                    finish();

            }
        });
    }
    /*@Override
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
    }*/
    /*@Override
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
                            startActivityIntent.launch(intent);

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
    }*/
}
