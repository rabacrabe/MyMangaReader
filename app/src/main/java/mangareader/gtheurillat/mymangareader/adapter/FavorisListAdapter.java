package mangareader.gtheurillat.mymangareader.adapter;

/**
 * Created by gtheurillat on 10/07/2018.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mangareader.gtheurillat.mymangareader.db.model.Favoris;
import mangareader.gtheurillat.mymangareader.R;

// Custom list item class for menu items
public class FavorisListAdapter extends BaseAdapter {

    private List<Favoris> items;
    private Context context;
    private int numItems = 0;

    public FavorisListAdapter(Context context, final List<Favoris> items) {
        this.items = items;
        this.context = context;
        this.numItems = items.size();
    }

    public int getCount() {
        return numItems;
    }

    public Favoris getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the current list item
        final Favoris item = items.get(position);
        // Get the layout for the list item
        final LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.list_manga_item, parent, false);


        TextView txtLibelle = (TextView) itemLayout.findViewById(R.id.mangaLibelle);
        txtLibelle.setText(item.getName());

        TextView txtGenre = (TextView) itemLayout.findViewById(R.id.mangaGenre);
        txtGenre.setText(item.getGenre());

        TextView txtStatus = (TextView) itemLayout.findViewById(R.id.mangaStatus);
        txtStatus.setText(item.getStatus());

        return itemLayout;
    }

}