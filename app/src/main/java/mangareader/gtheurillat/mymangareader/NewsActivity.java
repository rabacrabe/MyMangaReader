package mangareader.gtheurillat.mymangareader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.github.kelvao.cloudflarescrape.CloudflareScrape;
import mangareader.gtheurillat.mymangareader.adapter.NouveautesExpandableListAdapter;
import mangareader.gtheurillat.mymangareader.model.Chapitre;
import mangareader.gtheurillat.mymangareader.model.Nouveaute;
import mangareader.gtheurillat.mymangareader.model.Serie;
import mangareader.gtheurillat.mymangareader.util.CloudFlareProxy;
import mangareader.gtheurillat.mymangareader.util.ICloudFlareScrapperCallBack;
import mangareader.gtheurillat.mymangareader.util.JapScanProxy;
import mangareader.gtheurillat.mymangareader.util.MangaReaderProxy;
import io.github.kelvao.cloudflarescrape.CloudflareScrapeTask.Callback;


public class NewsActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<Serie> expandableListTitle;
    HashMap<Serie, List<Chapitre>> expandableListDetail;
    ProgressDialog mProgressDialog;
    //SpotsDialog mProgressDialog;
    Context mainContext;
    Map<String, String> cookies;
    String website_title;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_news:
                    //mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_tops:
                    //mTextMessage.setText(R.string.title_dashboard);
                    Intent intent_tops = new Intent(NewsActivity.this, TopsActivity.class);
                    intent_tops.putExtra("WEBSITE_TITLE", website_title);
                    startActivity(intent_tops);
                    return true;
                case R.id.navigation_mangas:
                    //mTextMessage.setText(R.string.title_notifications);
                    Intent intent_mangas = new Intent(NewsActivity.this, MangasActivity.class);
                    intent_mangas.putExtra("WEBSITE_TITLE", website_title);
                    startActivity(intent_mangas);
                    return true;
                case R.id.navigation_favoris:
                    Intent intent_favoris = new Intent(NewsActivity.this, FavorisActivity.class);
                    intent_favoris.putExtra("WEBSITE_TITLE", website_title);
                    startActivity(intent_favoris);
                    return true;
                case R.id.navigation_bookmark:
                    Intent intent_bookmark = new Intent(NewsActivity.this, BookmarkActivity.class);
                    intent_bookmark.putExtra("WEBSITE_TITLE", website_title);
                    startActivity(intent_bookmark);
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Intent intent_main = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(intent_main);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        website_title = getIntent().getStringExtra("WEBSITE_TITLE");
        setTitle(website_title);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_news);


        mainContext = this;

        expandableListView = (ExpandableListView) findViewById(R.id.lst_dernieres_sorties);


        if (website_title.equals("JAPSCAN")) {

            mProgressDialog = new ProgressDialog(this.mainContext);
            mProgressDialog.setTitle("Dernières Sorties");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();

            CloudFlareProxy cf = new CloudFlareProxy(JapScanProxy.urlRoot);

            cf.setUser_agent(JapScanProxy.UA);
            cf.getCookies(new CloudFlareProxy.cfCallback() {
                @Override
                public void onSuccess(List<HttpCookie> cookieList) {
                    Log.i("CLOUDFLAREPROXY", "SUCCESS");
                    cookies = CloudFlareProxy.List2Map(cookieList);

                    mProgressDialog.dismiss();

                    new NewsActivity.Nouveautes().execute();
                }

                @Override
                public void onFail() {
                    Log.i("CLOUDFLAREPROXY", "ECHEC");
                }
            });
        } else {
            new NewsActivity.Nouveautes().execute();
        }




        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            /*
            Toast.makeText(getApplicationContext(),
                    expandableListTitle.get(groupPosition) + " List Expanded.",
                    Toast.LENGTH_SHORT).show();
                    */
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
            /*
            Toast.makeText(getApplicationContext(),
                    expandableListTitle.get(groupPosition) + " List Collapsed.",
                    Toast.LENGTH_SHORT).show();
*/
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Serie current_serie = expandableListTitle.get(groupPosition);
                Chapitre current_chapitre = expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition);
                String serie_title = current_serie.getTitle();
                String serie_url = current_serie.getUrl();
                String chapitre_title = current_chapitre.getTitle();
                String chapitre_url = current_chapitre.getUrl();

/*
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition).getUrl(), Toast.LENGTH_SHORT
                ).show();
*/



                Intent intent_lecteur = new Intent(NewsActivity.this, LecteurActivity.class);
                intent_lecteur.putExtra("SERIE_TITLE", serie_title);
                intent_lecteur.putExtra("SERIE_URL", serie_url);
                intent_lecteur.putExtra("CHAPITRE_TITLE", chapitre_title);
                intent_lecteur.putExtra("CHAPITRE_URL", chapitre_url);
                intent_lecteur.putExtra("WEBSITE_TITLE", website_title);

                startActivity(intent_lecteur);

                return false;
            }
        });
    }


    // Title AsyncTask
    private class Nouveautes extends AsyncTask<Void, Void, Void> {
        String title;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(NewsActivity.this);
            mProgressDialog.setTitle("Dernières Sorties");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                ArrayList<Nouveaute> lstNouveautes = new ArrayList<Nouveaute>();
                if (website_title.equals("JAPSCAN")) {
                    JapScanProxy japscan_proxy = new JapScanProxy(cookies);
                    lstNouveautes = japscan_proxy.getNouveautes();
                }
                else if (website_title.equals("MANGAREADER")) {
                    MangaReaderProxy proxy = new MangaReaderProxy();
                    lstNouveautes = proxy.getNouveautes();
                }

                expandableListDetail = new HashMap<Serie, List<Chapitre>>();
                expandableListTitle = new ArrayList<Serie>();
                for (Nouveaute nouveaute : lstNouveautes) {
                    //expandableListDetail.put(new Serie(nouveaute.getDate(), null), new ArrayList<Chapitre>());

                    for (Serie serie : nouveaute.getLstSeries()) {
                        List<Chapitre> lst_chapitres = new ArrayList<Chapitre>();

                        for (Chapitre chapitre : serie.getLstChapitres()) {
                            chapitre.setDate_sortie(nouveaute.getDate());
                            lst_chapitres.add(chapitre);
                        }

                        expandableListDetail.put(serie, lst_chapitres);
                        expandableListTitle.add(serie);
                    }
                }


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

            //expandableListTitle = new ArrayList<Serie>(expandableListDetail.keySet());

            expandableListAdapter = new NouveautesExpandableListAdapter(mainContext, expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);

            mProgressDialog.dismiss();
        }
    }


}
