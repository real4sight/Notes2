package com.example.notes2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;


import com.example.notes2.Notes.ItemClickSupport;
import com.example.notes2.Notes.Note;
import com.example.notes2.Notes.NoteAdapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.widget.LinearLayout.VERTICAL;


public class NotesActivity extends AppCompatActivity{

    public static final List<Note> noteList = new ArrayList<>();
    public RecyclerView recyclerView;
    private NoteAdapter mAdapter;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static int listSize;
    private boolean start;
    public static final List<String> date=new ArrayList<>();
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    public static Set<String> set = new HashSet<String>();


    public NotesActivity(){
          start=true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //set up the recycler view
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_notes);
        mAdapter = new NoteAdapter(noteList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), VERTICAL, true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //get shared pref
        SharedPreferences sharedPref= getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        listSize = sharedPref.getInt("listSize", MODE_PRIVATE);
        Set d=sharedPref.getStringSet("dateList", set);





        //when a recycler view item is clicked
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //Snackbar.make(recyclerView, "this is text number "+position, BaseTransientBottomBar.LENGTH_LONG)
                 //       .setAction("Action", null).show();


                Bundle extras = getIntent().getExtras();
                if(extras!=null){
                    boolean isNew = extras.getBoolean("IS_NEW");

                }
                Intent writeIntent= new Intent(NotesActivity.this, WriteNote.class);
                writeIntent.putExtra("EXTRA_POSITION", position);
                startActivity(writeIntent);


            }
        });


        //float action button setting in the note list
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //save shared pref
                SharedPreferences sharedPref= getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor= sharedPref.edit();
                editor.putInt("listSize", listSize+1);
                editor.commit();

                //start a new activity
                int noteSize=listSize;
                Intent writeIntent= new Intent(NotesActivity.this, WriteNote.class);
                writeIntent.putExtra("EXTRA_POSITION", noteSize);

                startActivity(writeIntent);

            }
        });

        //can use this space to populate note list
        noteList.clear();
        date.addAll(d);

        while(start){
    for (int i = 0; i < listSize; i++) {

        if (readFromTitle(getApplicationContext(),i).equals(null)||readFromTitle(getApplicationContext(),i).equals("")){

        }else{
            addNote(readFromTitle(getApplicationContext(),i), date.get(i));
        }

    }
    start=false;

        }

    }


    public void addNote(String title, String date) {
        Note note = new Note(title, date);
        noteList.add(note);

        mAdapter.notifyDataSetChanged();
    }


    //acess file title depending on the position
    private String titleNumber(int position){
        String file = "title"+position+".txt";
        return file;
    }

    //read title from the file
    private String readFromTitle(Context context, int i) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(titleNumber(i));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }


}
