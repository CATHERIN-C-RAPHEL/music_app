package abc.def.music_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    MediaPlayer player = null;
    SeekBar seek;
    ListView lis;
    Field[] fields = R.raw.class.getFields();
    ArrayList songs = new ArrayList<String>();
    String ab = Environment.getExternalStorageDirectory().getPath()+"/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = MediaPlayer.create(getApplicationContext(), R.raw.song1);
        for(int i = 0; i< fields.length; i++ )
            songs.add(fields[i].getName());

        seek = findViewById(R.id.seekbar);

        seek.setMax(player.getDuration() );

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               if(player != null){
               seek.setProgress(player.getCurrentPosition());
               }
            }

        } ,0,900);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                player.getCurrentPosition();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                player.seekTo(seekBar.getProgress());

            }
        });

        lis = findViewById(R.id.list);
        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,songs);
        lis.setAdapter(arr);
        lis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//           if(i == 0){
//               if(player != null)
//                   player.stop();
//               player = MediaPlayer.create(getApplicationContext(), R.raw.song1);
//           }
//           if(i == 1){
//               if(player != null)
//                   player.stop();
//               player = MediaPlayer.create(getApplicationContext(), R.raw.song2);
//           }
//           if(i == 2){
//               if(player != null)
//                   player.stop();
//               player = MediaPlayer.create(getApplicationContext(), R.raw.song3);
//           }
//           if(i == 3){
//               if(player != null)
//                   player.stop();
//               player = MediaPlayer.create(getApplicationContext(), R.raw.song4);
//           }
//           if(i == 4){
//               if(player != null)
//                   player.stop();
//               player = MediaPlayer.create(getApplicationContext(), R.raw.song5);
//           }
           if(player!=null){
               player.stop();
           }
           int resid = getResources().getIdentifier((String) songs.get(i),"raw", getPackageName());
           player = MediaPlayer.create(getApplicationContext(),resid);
       }
   });

    }

    public void play(View v) {
        if (player == null) {

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopPlayer();
                }
            });
        }

        player.start();
    }

    public void pause(View v) {
        if (player != null) {
            player.pause();
        }
    }

    public void stop(View v) {
       stopPlayer();
    }
    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mn:
//                String path = Environment.getExternalStorageDirectory().getPath() + "/" ;
//                                Uri uri = Uri.parse(path);
//                                Intent intent = new Intent(Intent.ACTION_PICK);
//                                intent.setDataAndType(uri, "*/*");
//                                startActivity(intent);
                Folder();




            case R.id.ml:
                opensd();
        }

        return super.onOptionsItemSelected(item);
    }

    private void opensd() {
        String state = Environment.getExternalStorageState();
        ArrayList<Object> listData = new ArrayList<>();
        Toast.makeText(getApplicationContext(),state,Toast.LENGTH_SHORT).show();

        if(Environment.MEDIA_MOUNTED.equals(state))
        {
            if(Build.VERSION.SDK_INT >=23){
                if(checkPermission()){
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";

                    File dir = new File(path);
                    if(dir.exists()){
                        File list [] = dir.listFiles();
                        for (int i = 0; i<list.length; i++){
                            listData.add(list[i].getName());
                        }
                    }
                }
                else
                {
                    requestPermissions();

                }
            }
        }

    }
    private void requestPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(getApplicationContext(),"Enable permisson externally",Toast.LENGTH_SHORT).show();
        }
        else{
            String permissionList [] ={
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(MainActivity.this,permissionList,100);
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }else
            return false;
    }



    private void Folder() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String path = Environment.getExternalStorageDirectory().getPath() + "/" ;
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open"));

    }

}