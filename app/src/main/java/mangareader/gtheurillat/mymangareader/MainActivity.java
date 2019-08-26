package mangareader.gtheurillat.mymangareader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mangareader.gtheurillat.mymangareader.adapter.NouveautesExpandableListAdapter;
import mangareader.gtheurillat.mymangareader.model.Chapitre;
import mangareader.gtheurillat.mymangareader.model.Nouveaute;
import mangareader.gtheurillat.mymangareader.model.Serie;
import mangareader.gtheurillat.mymangareader.util.MangaReaderProxy;


public class MainActivity extends AppCompatActivity {


    Context mainContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setTitle("Nouveautés");

        mainContext = this;

        Button btn_japscan = (Button)findViewById(R.id.button_japscan);
        btn_japscan.setOnClickListener(new View.OnClickListener(){
               @Override
               public void onClick(View v) {
                   Intent intent_news = new Intent(MainActivity.this, NewsActivity.class);
                   intent_news.putExtra("WEBSITE_TITLE", "JAPSCAN");

                   startActivity(intent_news);
               }
        });

        Button btn_mangareader = (Button)findViewById(R.id.button_mangareader);
        btn_mangareader.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent_news = new Intent(MainActivity.this, NewsActivity.class);
                intent_news.putExtra("WEBSITE_TITLE", "MANGAREADER");

                startActivity(intent_news);
            }
        });



    }


}

