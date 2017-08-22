package info.androidhive.navigationdrawer.adapters;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.db.DbController;
import info.androidhive.navigationdrawer.fragment.FragmentHistory;
import info.androidhive.navigationdrawer.model.Lst_item_hist;

/**
 * Created by ahmed on 19/08/17.
 */

public class ListHistoryAdapter extends ArrayAdapter<Lst_item_hist>{


    Context context = null;
    ArrayList<Lst_item_hist> list = new ArrayList<Lst_item_hist>();
    Lst_item_hist itemHist = null;

//    public ListHistoryAdapter(Context context, ArrayList<Lst_item_hist> list) {
//        this.context = context;
//        this.list = list;
//    }


    public ListHistoryAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Lst_item_hist> objects) {
        super(context, resource, objects);


        this.context = context;
        this.list = objects;


    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Lst_item_hist getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null){


            view  = LayoutInflater.from(context).inflate(R.layout.lst_hist_item, null);

             itemHist = getItem(position);

            ((TextView)view.findViewById(R.id.tvPath)).setText(itemHist.getStrPathOfDirec());
//            view.findViewById(R.id.ivClose).setOnClickListener(new ImageView.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    //Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
//                    deleteRowPathFromDataBase();
//                    refresh();
//                    notifyDataSetChanged();
//                    //new FragmentHistory().adapter.notifyDataSetChanged();
//                }
//            });

        }


        return view;
    }

    private void refresh() {
        ArrayList<HashMap<String, String>> hashMaps = new DbController(context).getData("select * from "+DbController.tblAccessedPaths);
        if (hashMaps.size() != 0){
            for (HashMap<String, String> map : hashMaps){
                add(new Lst_item_hist(map.get(DbController.filePath)));
            }
        }
        notifyDataSetChanged();
//        notifyAll();

    }



}
