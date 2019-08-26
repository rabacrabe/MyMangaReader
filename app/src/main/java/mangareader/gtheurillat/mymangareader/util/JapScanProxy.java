package mangareader.gtheurillat.mymangareader.util;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import mangareader.gtheurillat.mymangareader.model.Chapitre;
import mangareader.gtheurillat.mymangareader.model.Nouveaute;
import mangareader.gtheurillat.mymangareader.model.Page;
import mangareader.gtheurillat.mymangareader.model.Serie;

public class JapScanProxy implements IReaderProxy {

    public static String urlRoot = "https://www.japscan.to";
    Map<String, String> cookies = null;

    public static String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";

    public JapScanProxy(Map<String, String> cookies){
        this.cookies = cookies;
    }


    @Override
    public ArrayList<Nouveaute> getNouveautes() {

        ArrayList<Nouveaute> lstNouveautes = new ArrayList<Nouveaute>();

        try {
            Log.i("URL", this.urlRoot);
            Log.i("COOKIES", this.cookies.toString());
            Document doc = Jsoup.connect(this.urlRoot).userAgent(this.UA).cookies(cookies).get();

            Log.i("GET", "JSOUP \n"+ doc.text());



            Elements elements_chapters = doc.select("div.chapters_list");


            Log.i("NB SERIE", String.valueOf(elements_chapters.size()));

            Nouveaute new_nouveaute = new Nouveaute("Nouveautes");
            Serie newSerie = null;

            for (Element element_chapters :  elements_chapters) {
                Element element_serie = element_chapters.previousElementSibling().select("a").first();

                if (new_nouveaute.getLstSeries().size() > 0) {
                    lstNouveautes.add(new_nouveaute);
                }
                new_nouveaute = new Nouveaute("Nouveautes");

                newSerie = new Serie(element_serie.text(), this.urlRoot + element_serie.attr("href").toString());
                new_nouveaute.addSerie(newSerie);
                Log.i("SERIE", element_serie.text());


                for (Element element_chapter : element_chapters.select("a")) {
                    Chapitre newChapitre = new Chapitre(element_chapter.text(), this.urlRoot + element_chapter.attr("href"));
                    newSerie.addChapitre(newChapitre);
                    Log.i("CHAPITRE", element_chapter.text());
                }

            }

        }  catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("LIST NOUVEAUTES", String.valueOf(lstNouveautes.size()));

        return lstNouveautes;
    }

    @Override
    public ArrayList<Serie> getTops() {
        return null;
    }

    @Override
    public Serie getSerieDetails(String serieTitle, String serieUrl) {
        return null;
    }

    @Override
    public ArrayList<Serie> getCatalogue() {
        return null;
    }

    @Override
    public Serie getLecteurInfos(String title, String url) {
        Serie serie = null;
        Chapitre chapitre = new Chapitre(title, url);


        try {


            Log.i("URL", url);
            Document doc = Jsoup.connect(url).userAgent(this.UA).cookies(cookies).get();
            Log.i("GET", "JSOUP \n"+ doc.text());

            Element img_node = doc.select("div#image").first();



/*
            Element lastcard = doc.select("div.card").last();

            Elements chapter_nodes = lastcard.select("a");

            Chapitre nextChapitre = null;
            Chapitre precChapitre = null;
            for (Element chapter_node : chapter_nodes)
            {
                Log.i("qsdqdqsdqsdsqdsq", chapter_node.text());
                if (chapter_node.previousElementSibling().text().contains("Chapitre Suivant")) {

                    nextChapitre = new Chapitre(chapter_node.text(), this.urlRoot + chapter_node.attr("href"));
                    Log.i("NEXT CHAPTER", nextChapitre.getTitle());

                }
                else {
                    precChapitre = new Chapitre(chapter_node.text(), this.urlRoot + chapter_node.attr("href"));
                    Log.i("PREVIOUS CHAPTER", precChapitre.getTitle());
                }
            }
*/

            Element prec_chapter_node = doc.select("p:contains(Chapitre Pr) > a").first();
            Chapitre precChapitre = null;
            if (prec_chapter_node != null) {
                precChapitre = new Chapitre(prec_chapter_node.text(), this.urlRoot + prec_chapter_node.attr("href"));
            }
            else{
                precChapitre = chapitre;
            }

            Element next_chapter_node = doc.select("p:contains(Chapitre Suivant) > a").first();
            Chapitre nextChapitre = null;
            if (next_chapter_node != null) {
                nextChapitre = new Chapitre(next_chapter_node.text(), this.urlRoot + next_chapter_node.attr("href"));
            }
            else{
                nextChapitre = chapitre;
            }

            Log.i("PREVIOUS CHAPTER", precChapitre.getTitle());
            Log.i("NEXT CHAPTER", nextChapitre.getTitle());







            Element pages_node = doc.select("select#pages").first();

            for (Element pageItem : pages_node.children()) {
                Log.i("PAGE", pageItem.text() + " -> " + this.urlRoot + pageItem.attr("value"));
                Page newPage = new Page(pageItem.text(), this.urlRoot + pageItem.attr("value"));



                if (pageItem.hasAttr("selected")) {
                    newPage.setTitle(img_node.attr("data-src"));
                    newPage.setImgUrl(img_node.attr("data-src"));

                    Log.e("IMG", img_node.attr("data-src"));

                    newPage.setSelected(true);
                }

                chapitre.addPage(newPage);
            }



            serie = new Serie("TEST", "");
            Element serie_name_node = doc.select("h2.c2 > a").first();
            if (serie_name_node != null) {
                serie.setTitle(serie_name_node.text());
                serie.setUrl(this.urlRoot + serie_name_node.attr("href") );


            }


            serie.addChapitre(precChapitre);
            serie.addChapitre(chapitre);
            serie.addChapitre(nextChapitre);

            serie.setIdxCurrentChapitre(1);

        }  catch (IOException e) {
            e.printStackTrace();
        }

        return serie;
    }


}

