package com.example.musicplayerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // listview object by its id name
        listView = findViewById(R.id.listView);

        // For Run Time External Storage Read Permission - Use Dexter Library
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        Toast.makeText(MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT).show(); // Toast
                        // Calling FetchSong method
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        // Showing name of Songs
                        // string array items
                        String [] items = new String[mySongs.size()];
                        for(int i=0;i<mySongs.size();i++){
                            items[i] = mySongs.get(i).getName().replace(".mp3", "");
                        }
                        // For ListView (View Layout active using adaptor)
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        listView.setAdapter(adapter);

                        // Intent for another activity = PlaySongs
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(MainActivity.this, PlaySong.class); // go to another activity PlaySong Activity
                                String currentSong = listView.getItemAtPosition(position).toString(); // Current Song
                                intent.putExtra("songList", mySongs); // Sending Song List
                                intent.putExtra("currentSong", currentSong); // Sendiing Current Song
                                intent.putExtra("position", position); // Sending Position of Song
                                startActivity(intent); // To start intent(Activity)
                            }
                        });


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest(); // Permission Request
                    }
                })
                .check();
    }
    // Return arraylist of files
    // method name = fetchSongs
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        // File array
        File [] songs = file.listFiles(); // list all files of the file directory in song array
        // Recursive Calling of file
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile)); // Adding Songs from file directory into arraylist by using fetchsong method recursively
                }
                else{
                    // .mp3 files add into arraylist
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}