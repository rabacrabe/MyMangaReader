package mangareader.gtheurillat.mymangareader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.SearchView;

import java.util.List;

import mangareader.gtheurillat.mymangareader.adapter.CatalogueListAdapter;
import mangareader.gtheurillat.mymangareader.model.Serie;
import mangareader.gtheurillat.mymangareader.util.MangaReaderProxy;
import mangareader.gtheurillat.mymangareader.widget.IndexableListView;

public class MangasActivity extends AppCompatActivity {

    //ListView listView;
    IndexableListView listView;
    CatalogueListAdapter listAdapter;
    List<Serie> listTitle;
    List<Serie> listDetail;
    ProgressDialog mProgressDialog;
    Context mainContext;
    SearchView searchView;
    AlertDialog.Builder alertDialogBuilder;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    //mTextMessage.setText(R.string.title_home);
                    Intent intent_mangas = new Intent(MangasActivity.this, NewsActivity.class);
                    startActivity(intent_mangas);
                    return true;
                case R.id.navigation_tops:
                    //mTextMessage.setText(R.string.title_dashboard);
                    Intent intent_tops = new Intent(MangasActivity.this, TopsActivity.class);
                    startActivity(intent_tops);
                    return true;
                case R.id.navigation_mangas:
                    //mTextMessage.setText(R.string.title_notifications);

                    return true;
                case R.id.navigation_favoris:
                    Intent intent_favoris = new Intent(MangasActivity.this, FavorisActivity.class);
                    startActivity(intent_favoris);
                    return true;
                case R.id.navigation_bookmark:
                    Intent intent_bookmark = new Intent(MangasActivity.this, BookmarkActivity.class);
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
                Intent intent_favoris = new Intent(MangasActivity.this, FavorisActivity.class);
                startActivity(intent_favoris);
                return true;
            case R.id.menu_bookmark:
                Intent intent_bookmark = new Intent(MangasActivity.this, BookmarkActivity.class);
                startActivity(intent_bookmark);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mangas);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_mangas);


        mainContext = this;
        alertDialogBuilder = new AlertDialog.Builder(mainContext);

        //listView = (ListView) findViewById(R.id.lst_mangas);
        listView = (IndexableListView) findViewById(R.id.lst_mangas);
        listView.setFastScrollEnabled(true);

        searchView = (SearchView)findViewById(R.id.recherche_manga);

        new Catalogue().execute();

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
        }



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Object listItem = listDetail.get(position);
                Object listItem = listAdapter.getItem(position);
                /*
                Toast.makeText(
                        getApplicationContext(),
                        listAdapter.getItem(position)
                                + " -> "
                                + listAdapter.getItem(position).getUrl(), Toast.LENGTH_SHORT
                ).show();
                */
                Intent intent_seriedetail = new Intent(MangasActivity.this, SerieDetailsActivity.class);
                intent_seriedetail.putExtra("SERIE_TITLE", listAdapter.getItem(position).getTitle());
                intent_seriedetail.putExtra("SERIE_URL", listAdapter.getItem(position).getUrl());
                startActivity(intent_seriedetail);


            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // TODO Auto-generated method stub

                listAdapter.getFilter().filter(query);

                return false;
            }
        });
    }


    // Title AsyncTask
    private class Catalogue extends AsyncTask<Void, Void, Void> {
        String title;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MangasActivity.this);
            mProgressDialog.setTitle("Toutes les BDs");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                MangaReaderProxy proxy = new MangaReaderProxy();
                listDetail = proxy.getCatalogue();
            } catch (Exception e) {
                e.printStackTrace();
                // set title
                alertDialogBuilder.setTitle("Erreur");

                // set dialog message
                alertDialogBuilder
                        .setMessage(e.toString())
                        .setCancelable(false)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                MangasActivity.this.finish();
                            }
                        });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Set title into TextView
            //TextView txttitle = (TextView) findViewById(R.id.titletxt);
            //txttitle.setText(title);


            listAdapter = new CatalogueListAdapter(mainContext, android.R.layout.simple_list_item_1, listDetail);

            listView.setAdapter(listAdapter);


            mProgressDialog.dismiss();
        }

    }


}
