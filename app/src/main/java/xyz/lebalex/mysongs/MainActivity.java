package xyz.lebalex.mysongs;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private List<SongModel> listSong = new ArrayList<SongModel>();
    //private final String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Song";
    private String file_path;
    private boolean searchD=false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getListSong();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean("black_theme", false))
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppThemeLight);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            getListSong();
        }

    }

    /*private String getRootOfInnerSdCardFolder(File file)
    {
        if(file==null)
            return null;
        final long totalSpace=file.getTotalSpace();
        while(true)
        {
            final File parentFile=file.getParentFile();
            if(parentFile==null||parentFile.getTotalSpace()!=totalSpace)
                return file.getAbsolutePath();
            file=parentFile;
        }
    }*/
    private File[] getFiles(String file_path)
    {
        File directory = new File(file_path);
        return directory.listFiles();
    }
    private void addSong(String file_path)
    {
        File[] files = getFiles(file_path);
        if(files!=null)
            for(File f: files) {
                String songName=firstStrFile(f.getAbsolutePath());
                if(songName!=null)
                    listSong.add(new SongModel(f.getAbsolutePath(), songName));
                //listSong.add(new SongModel(file_path+"/"+f.getName(),firstStrFile(file_path+"/"+f.getName())));
            }
    }

    private void getListSong()
    {

        File[] externalStorageFiles=ContextCompat.getExternalFilesDirs(this,null);

        for(File file : externalStorageFiles)
        {
            String path = file.getAbsolutePath();
            path = path.replaceAll("Android/data/xyz.lebalex.mysongs/files", "Song");
            ///storage/sdcard/Android/data/xyz.lebalex.mysongs/files

            //addSong(getRootOfInnerSdCardFolder(file) + "/Song");
            addSong(path);
        }
        if(listSong.size()==0){
            addSong(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Song");
        }




        createList(listSong, null, 0);

    }
    private void createList(final List<SongModel> listSong, MenuItem item, int menuTitle)
    {
        Collections.sort(listSong, new Comparator<SongModel>() {
            public int compare(SongModel o1, SongModel o2) {
                return (o1.getSongName()).compareToIgnoreCase(o2.getSongName());
                //compare(o1.getSongName(), o2.getSongName());
            }
        });
        ListView lvMain = (ListView) findViewById(R.id.lvMain);
        SongListAdapter adapter = new SongListAdapter(this, listSong);
        lvMain.setAdapter(adapter);
        lvMain.setEmptyView(findViewById(R.id.emptyElement));

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /*Log.d("OnItemClick", "itemClick: position = " + position + ", id = "
                        + id);*/
                Intent mIntent = new Intent(getApplicationContext(), SongActivity.class);
                mIntent.putExtra("filename", listSong.get(position).getFileName());
                startActivity(mIntent);
            }
        });
        if(item!=null)
            item.setTitle(menuTitle);
    }

    private String firstStrFile(String fileName)
    {
        String nameSong=null;
        if(fileName!=null) {
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    if ((line = br.readLine()) != null) {
                        nameSong=line;
                    }
                    br.close();
                }
                catch (Exception e) {
                }

                return nameSong;
            } else {
                return null;
            }
        }else return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent mIntent = new Intent(this,SettingsActivity.class);
            startActivity(mIntent);
            return true;
        }
        if(id==R.id.action_search)
        {
            if(!searchD) {
                searchDialog(item, R.string.clearSearch);
            }else
            {
                createList(listSong, item, R.string.action_search);
            }
            searchD=!searchD;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private int getPixelValue(int dp) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, resources.getDisplayMetrics());
    }
    private void searchDialog(final MenuItem item, final int menuTitle)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog));
        builder.setTitle("Поиск по названию");


        LinearLayout parentLayout = new LinearLayout(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        int left = getPixelValue((int)getResources().getDimension(R.dimen.activity_horizontal_margin));
        int top = getPixelValue((int)getResources().getDimension(R.dimen.activity_horizontal_margin));
        int right = getPixelValue((int)getResources().getDimension(R.dimen.activity_horizontal_margin));
        int bottom = getPixelValue((int)getResources().getDimension(R.dimen.activity_horizontal_margin));

        layoutParams.setMargins(left, 0, right, 0);

        editText.setLayoutParams(layoutParams);
        parentLayout.addView(editText);
        builder.setView(parentLayout);

        //builder.setView(editText);


// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchText(editText.getText().toString(), item, menuTitle);//m_Text = input.getText().toString();

            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void searchText(String text, MenuItem item, int menuTitle)
    {
        List<SongModel> listSong_search = new ArrayList<SongModel>();
        for(SongModel song: listSong)
        {
            if(song.getSongName().toLowerCase().contains(text.toLowerCase()))
            {
                listSong_search.add(song);
            }
        }

        createList(listSong_search, item, menuTitle);
    }


}
