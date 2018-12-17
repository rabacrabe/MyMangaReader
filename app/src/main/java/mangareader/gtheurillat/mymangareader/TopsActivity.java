package mangareader.gtheurillat.mymangareader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import mangareader.gtheurillat.mymangareader.adapter.TopsListAdapter;
import mangareader.gtheurillat.mymangareader.model.Serie;
import mangareader.gtheurillat.mymangareader.util.MangaReaderProxy;

public class TopsActivity extends AppCompatActivity {

    ListView listView;
    ListAdapter listAdapter;
    List<Serie> listTitle;
    List<Serie> listDetail;
    ProgressDialog mProgressDialog;
    Context mainContext;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    //mTextMessage.setText(R.string.title_home);
                    Intent intent_tops = new Intent(TopsActivity.this, NewsActivity.class);
                    startActivity(intent_tops);
                    return true;
                case R.id.navigation_tops:
                    //mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_mangas:
                    //mTextMessage.setText(R.string.title_notifications);
                    Intent intent_mangas = new Intent(TopsActivity.this, MangasActivity.class);
                    startActivity(intent_mangas);
                    return true;
                case R.id.navigation_favoris:
                    Intent intent_favoris = new Intent(TopsActivity.this, FavorisActivity.class);
                    startActivity(intent_favoris);
                    return true;
                case R.id.navigation_bookmark:
                    Intent intent_bookmark = new Intent(TopsActivity.this, BookmarkActivity.class);
                    startActivity(intent_bookmark);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.navigation_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_favoris:
                Intent intent_favoris = new Intent(TopsActivity.this, FavorisActivity.class);
                startActivity(intent_favoris);
                return true;
            case R.id.menu_bookmark:
                Intent intent_bookmark = new Intent(TopsActivity.this, BookmarkActivity.class);
                startActivity(intent_bookmark);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tops);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_tops);

        listView = (ListView) findViewById(R.id.lst_tops_semaine);
        mainContext = this;

        new Tops().execute();
/*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listDetail.get(position);

                Intent intent_seriedetail = new Intent(TopsActivity.this, SerieDetailsActivity.class);
                intent_seriedetail.putExtra("SERIE_TITLE", listDetail.get(position).getTitle());
                intent_seriedetail.putExtra("SERIE_URL", listDetail.get(position).getUrl());
                startActivity(intent_seriedetail);


            }
        });
*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent_seriedetail = new Intent(TopsActivity.this, SerieDetailsActivity.class);
                intent_seriedetail.putExtra("SERIE_TITLE", listDetail.get(position).getTitle());
                intent_seriedetail.putExtra("SERIE_URL", listDetail.get(position).getUrl());
                startActivity(intent_seriedetail);

            }
        });

    }


    // Title AsyncTask
    private class Tops extends AsyncTask<Void, Void, Void> {
        String title;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TopsActivity.this);
            mProgressDialog.setTitle("Tops de la semaine");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                MangaReaderProxy proxy = new MangaReaderProxy();
                listDetail = proxy.getTops();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            //TextView txttitle = (TextView) findViewById(R.id.titletxt);
            //txttitle.setText(title);


            listAdapter = new TopsListAdapter(mainContext, listDetail);
            listView.setAdapter(listAdapter);

            mProgressDialog.dismiss();
        }
    }


}
