package mangareader.gtheurillat.mymangareader.util;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import mangareader.gtheurillat.mymangareader.model.Chapitre;
import mangareader.gtheurillat.mymangareader.model.Nouveaute;
import mangareader.gtheurillat.mymangareader.model.Page;
import mangareader.gtheurillat.mymangareader.model.Serie;
import mangareader.gtheurillat.mymangareader.model.Tome;

/**
 * Created by gtheurillat on 10/07/2018.
 */

public class MangaReaderProxy implements IReaderProxy{

    private String urlRoot = "https://www.mangareader.net";
    private String urlCatalogue = "https://www.mangareader.net/alphabetical";

    public MangaReaderProxy() {

    }

    public ArrayList<Nouveaute> getNouveautes(){
        ArrayList<Nouveaute> lstNouveautes = new ArrayList<Nouveaute>();

        try {
            // Document doc = Jsoup.connect(this.urlRoot).get();
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";


            Log.i("URL", this.urlRoot);
            Document doc = Jsoup.connect(this.urlRoot).userAgent(userAgent).get();
            Log.i("GET", "JSOUP \n"+ doc.text());

            Element nouveautes_node = doc.select("#latestchapters").first();

            Log.i("NOUVEAUTES", "JSOUP \n"+ nouveautes_node.text());

            Nouveaute new_nouveaute = new Nouveaute("Nouveautes");
            Serie newSerie = null;



            Elements lst_lignes_tableaux = nouveautes_node.select("td");
            for (Element ligne_tableau :  lst_lignes_tableaux) {
                String ligne_tableau_className = ligne_tableau.attr("class");
                //Log.i("CLASS td", ligne_tableau_className);

                if (ligne_tableau_className.equals("manga_open") || ligne_tableau_className.equals("manga_close")) {
                    if (new_nouveaute.getLstSeries().size() > 0) {
                        lstNouveautes.add(new_nouveaute);
                    }
                    new_nouveaute = new Nouveaute("Nouveautes");
                }
                else {
                    for (Element link : ligne_tableau.select("a")) {
                        String link_className = link.attr("class");
                        Log.i("CLASS a", link_className);

                        if (link_className.equals("chapter")) {
                            newSerie = new Serie(link.text(), this.urlRoot + link.attr("href").toString());
                            new_nouveaute.addSerie(newSerie);
                            Log.i("SERIE", link.text());
                        }
                        else if (link_className.equals("chaptersrec")) {
                            Chapitre newChapitre = new Chapitre(link.text(), this.urlRoot + link.attr("href"));
                            newSerie.addChapitre(newChapitre);
                            Log.i("CHAPITRE", link.text());
                        }
                    }
                }


            }

/*
            Elements lst_nouveautes = nouveautes_node.select("td.manga_open");

            new_nouveaute = new Nouveaute("Nouveautes");

            for (Element elementNode : lst_nouveautes) {

                Element blocNode = elementNode.parent();

                Log.i("INFO SERIE", "JSOUP : " + blocNode.text());

                for (Element blocContentNode : blocNode.children()) {
                    //Log.i("TAG1", blocContentNode.tag().toString());

                    if (blocContentNode.tagName() == "td") {
                        //Log.i("TAG2", blocContentNode.tag().toString());

                        String className = blocContentNode.attr("class");
                        //Log.i("TAG2 CLASS ", className.toString());
                        if (className.equals("c5") || className.equals("c7")) {
                            Log.i("DATE", blocContentNode.text());
                            new_nouveaute.setDate(blocContentNode.text());
                            lstNouveautes.add(new_nouveaute);

                            new_nouveaute = new Nouveaute("Nouveautes");
                        }
                        else {
                            Elements serieNodes = blocContentNode.select("td a");

                            for (Element serieNode : serieNodes) {
                                String serieClassName = serieNode.attr("class");
                                if (serieClassName.equals("chapter")) {
                                    if (newSerie != null) {
                                        new_nouveaute.addSerie(newSerie);
                                    }

                                    Log.i("SERIE NAME", serieNode.text());
                                    newSerie = new Serie(serieNode.text(), this.urlRoot + serieNode.attr("href").toString());

                                } else if (serieClassName.equals("chaptersrec")) {
                                    Log.i("CHAPITRE NAME", serieNode.text());
                                    Chapitre newChapitre = new Chapitre(serieNode.text(), this.urlRoot + serieNode.attr("href"));
                                    newSerie.addChapitre(newChapitre);
                                }
                            }
                        }
                    }

                }
            }
*/
        }  catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("LIST NOUVEAUTES", String.valueOf(lstNouveautes.size()));

        return lstNouveautes;
    }

    public ArrayList<Serie> getTops(){
        ArrayList<Serie> lstTops = new ArrayList<Serie>();

        try {
            // Document doc = Jsoup.connect(this.urlRoot).get();
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";


            Log.e("URL", this.urlRoot);
            Document doc = Jsoup.connect(this.urlRoot).userAgent(userAgent).get();
            Log.e("GET", "JSOUP \n"+ doc.text());

            Element tops_node = doc.select("div#popularlist").first();

            Elements lst_tops = tops_node.select(".manga");

            Integer number = 1;

            for (Element mangaNode : lst_tops) {
                Element serieNode = mangaNode.select("a").first();

                Log.e("SERIE", serieNode.text());

                Serie newSerie = new Serie(serieNode.text(), this.urlRoot + serieNode.attr("href").toString());
                newSerie.setNumber(String.format("%02d", Integer.parseInt(number.toString())));

                lstTops.add(newSerie);

                number += 1;
            }

        }  catch (IOException e) {
            e.printStackTrace();
        }

        return lstTops;
    }


    public Serie getSerieDetails(String serieTitle, String serieUrl){
        Serie serie = new Serie(serieTitle, serieUrl);

        try {
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";


            Log.e("URL", serieUrl);
            Document doc = Jsoup.connect(serieUrl).userAgent(userAgent).get();
            Log.e("GET", "JSOUP \n"+ doc.text());

            Element ImgNode = doc.select("div#mangaimg img").first();
            serie.setUrlImg(ImgNode.attr("src"));


            Elements row_table_nodes = doc.select("div#mangaproperties td");

            /*
            Log.i("0 ", row_table_nodes.get(0 ).text());
            Log.i("1 ", row_table_nodes.get(1 ).text());
            Log.i("2 ", row_table_nodes.get(2 ).text());
            Log.i("3 ", row_table_nodes.get(3 ).text());
            Log.i("4 ", row_table_nodes.get(4 ).text());
            Log.i("5 ", row_table_nodes.get(5 ).text());
            Log.i("6 ", row_table_nodes.get(6 ).text());
            Log.i("7 ", row_table_nodes.get(7 ).text());
            Log.i("8 ", row_table_nodes.get(8 ).text());
            Log.i("9 ", row_table_nodes.get(9 ).text());
            Log.i("10", row_table_nodes.get(10).text());

*/
            serie.setAuteur(row_table_nodes.get(9).text());
            serie.setDate_sortie(row_table_nodes.get(5).text());
            serie.setStatus(row_table_nodes.get(7).text());

            String genres = "";
            Elements row_genres = doc.select("div#mangaproperties td span.genretags");
            for (Element row_genre : row_genres) {
                genres += row_genre.text() + "/";
            }
            serie.setGenre(genres);

            Element synopsis_node = doc.select("div#readmangasum p").first();
            serie.setSynopsis(synopsis_node.text());

            Log.i("AUTEUR", serie.getAuteur());
            Log.i("DATE", serie.getDate_sortie());
            Log.i("GENRE", serie.getGenre());
            Log.i("STATUS", serie.getStatus());
            Log.i("SYNOPSIS", serie.getSynopsis());

            Elements chapitres_nodes = doc.select("div#chapterlist div.chico_manga");

            Log.i("CHAPTERS", chapitres_nodes.text());

            Tome newTome = new Tome("Chapitres Disponibles");

            for (Element chapitre_content : chapitres_nodes) {
                Element parent = chapitre_content.parent();
                Element chapite_node = parent.select("a").first();
                //Log.e("CHAPITRE", chapite_node.text());
                Chapitre newChapitre = new Chapitre(chapite_node.text(), this.urlRoot+chapite_node.attr("href").toString());
                newTome.addChapitre(newChapitre);

                serie.addChapitre(newChapitre);
            }
            serie.addTome(newTome);
            /*
            Tome newTome = null;
            for (Element elementNode : chapitres_node.children()) {
                if (elementNode.tagName() == "h2") {
                    Log.e("TOME", elementNode.text());
                    if (newTome != null) {
                        serie.addTome(newTome);
                    }
                    newTome = new Tome(elementNode.text());
                }
                if (elementNode.tagName() == "ul") {

                    for (Element subElementNode : elementNode.children()){
                        Element chapitreNode = subElementNode.select("a").first();

                        Log.e("CHAPITRE", chapitreNode.text());

                        Chapitre newChapitre = new Chapitre(chapitreNode.text(), "https:"+chapitreNode.attr("href").toString());

                        if (newTome == null) {
                            newTome = new Tome("PLUS RECENT");
                        }

                        newTome.addChapitre(newChapitre);
                    }
                }
            }
            if (newTome != null) {
                serie.addTome(newTome);
            }
            */
        }  catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("NB CHAPITITRES", String.valueOf(serie.getLstChapitres().size()));

        return serie;
    }


    public ArrayList<Serie> getCatalogue(){
        ArrayList<Serie> lstMangas = new ArrayList<Serie>();

        try {
            // Document doc = Jsoup.connect(this.urlRoot).get();
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";


            Log.e("URL", this.urlCatalogue);
            Document doc = Jsoup.connect(this.urlCatalogue).userAgent(userAgent).get();
            Log.e("GET", "JSOUP \n"+ doc.text());



            Elements manga_items = doc.select("ul.series_alpha li");

            for (Element manga_item : manga_items) {

                Element serieNode = manga_item.select("a").first();
                Log.e("SERIE", serieNode.text());

                Serie newSerie = new Serie(serieNode.text(), this.urlRoot + serieNode.attr("href").toString());

                Element serieStatusNode = manga_item.select("span.mangacompleted").first();
                if (serieStatusNode != null) {
                    newSerie.setStatus(serieStatusNode.text());
                } else {
                    newSerie.setStatus("[En cours]");
                }

                Log.e("SERIE", newSerie.getTitle() + " " + newSerie.getStatus());

                lstMangas.add(newSerie);

            }
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return lstMangas;
    }


    public Serie getLecteurInfos(String title, String url){
        Serie serie = null;
        Chapitre chapitre = new Chapitre(title, url);


        try {
            // Document doc = Jsoup.connect(this.urlRoot).get();
            String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";


            Log.i("URL", url);
            Document doc = Jsoup.connect(url).userAgent(userAgent).get();
            Log.i("GET", "JSOUP \n"+ doc.text());

            Element img_node = doc.select("img#img").first();

            Elements nav_chapters = doc.select("div#mangainfo_bas a");


            Element prec_chapter_node = nav_chapters.get(1);
            Chapitre precChapitre = null;
            if (prec_chapter_node != null) {
                precChapitre = new Chapitre(prec_chapter_node.text(), this.urlRoot + prec_chapter_node.attr("href"));
            }

            Element next_chapter_node = nav_chapters.get(0);
            Chapitre nextChapitre = null;
            if (next_chapter_node != null) {
                nextChapitre = new Chapitre(next_chapter_node.text(), this.urlRoot + next_chapter_node.attr("href"));
            }

            Log.i("PREVIOUS CHAPTER", precChapitre.getTitle());
            Log.i("NEXT CHAPTER", nextChapitre.getTitle());

            Element TestChapterMenu = doc.select("#chapterMenu").first();
            Log.i("CHAPTER MENU", TestChapterMenu.text());

            Element pages_node = doc.select("div#selectpage select#pageMenu").first();

            for (Element pageItem : pages_node.children()) {
                Log.i("PAGE", pageItem.text() + " -> " + this.urlRoot + pageItem.attr("value"));
                Page newPage = new Page(pageItem.text(), this.urlRoot + pageItem.attr("value"));


                String selected =pageItem.attr("selected");
                if (selected.equals("selected")) {
                    newPage.setTitle(img_node.attr("alt"));
                    newPage.setImgUrl(img_node.attr("src"));

                    Log.e("IMG", img_node.attr("src"));

                    newPage.setSelected(true);
                }

                chapitre.addPage(newPage);
            }



            serie = new Serie("TEST", "");
            Element serie_name_node = doc.select("div#mangainfo  h2 a").first();
            if (serie_name_node != null) {
                serie.setTitle(serie_name_node.text());
                serie.setUrl(this.urlRoot + serie_name_node.attr("href") );


            }


/*
            ArrayList<Chapitre> tmpLstChapitre = new ArrayList<Chapitre>();

            Element chapitres_node = doc.select("select#chapitres").first();

            while(chapitres_node.children().size() == 0) {

            }

            for (Element chapitreItem : chapitres_node.children()) {
                Log.e("PAGE", chapitreItem.text() + " -> " + this.urlRoot + chapitreItem.attr("value"));
                Chapitre newChapitre = new Chapitre(chapitreItem.text(), this.urlRoot + chapitreItem.attr("value"));
                tmpLstChapitre.add(newChapitre);
                serie.addChapitre(newChapitre);
            }
            //Collections.reverse(tmpLstChapitre);
            //serie.setLstChapitres(tmpLstChapitre);
            serie.setIdxCurrentChapitre(Integer.parseInt(chapitres_node.attr("data-uri"))-1);
*/


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

