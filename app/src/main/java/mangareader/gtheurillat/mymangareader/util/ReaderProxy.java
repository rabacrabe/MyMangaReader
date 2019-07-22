package mangareader.gtheurillat.mymangareader.util;

import java.util.ArrayList;

import mangareader.gtheurillat.mymangareader.model.Nouveaute;
import mangareader.gtheurillat.mymangareader.model.Serie;

public interface ReaderProxy {
    public ArrayList<Nouveaute> getNouveautes();
    public ArrayList<Serie> getTops();
    public Serie getSerieDetails(String serieTitle, String serieUrl);
    public ArrayList<Serie> getCatalogue();
    public Serie getLecteurInfos(String title, String url);

}
