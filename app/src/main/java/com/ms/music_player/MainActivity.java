package com.ms.music_player;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
   ListView listView;
   String[] items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listViewSong);
        runtimePermission();
    }
    public void runtimePermission()
    {
     Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
         @Override
         public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
             diplaySongs();
         }

         @Override
         public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

         }

         @Override
         public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
         permissionToken.continuePermissionRequest();
         }
     }).check();
    }
    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList=new ArrayList<>();

        File[] files=file.listFiles();
        for (File singleFile: files){
            if (singleFile.isDirectory() && !singleFile.isHidden())
            {
                arrayList.addAll(findSong(singleFile));
            }
            else
            {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
                {
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
     void diplaySongs()
     {
         final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
         items= new String[mySongs.size()];
         for(int i=0;i<mySongs.size();i++){
             items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
         }
        // ArrayAdapter<String> myAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        // listView.setAdapter(myAdapter);
         CustomAdapter customAdapter=new CustomAdapter();
         listView.setAdapter(customAdapter);
         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 String songName =(String) listView.getItemAtPosition(i);
                 startActivity(new Intent(getApplicationContext(),PlayerActivity.class)
                 .putExtra("songs",mySongs)
                 .putExtra("songname",songName)
                 .putExtra("pos",i));
             }
         });
     }
     class CustomAdapter extends BaseAdapter
     {

         @Override
         public int getCount() {
             return items.length;
         }

         @Override
         public Object getItem(int i) {
             return null;
         }

         @Override
         public long getItemId(int i) {
             return 0;
         }

         @Override
         public View getView(int i, View view, ViewGroup viewGroup) {
             View myView= getLayoutInflater().inflate(R.layout.list_item,null);
             TextView textSong=myView.findViewById(R.id.txtsongname);
             textSong.setText(items[i]);
             return myView;
         }
     }
}