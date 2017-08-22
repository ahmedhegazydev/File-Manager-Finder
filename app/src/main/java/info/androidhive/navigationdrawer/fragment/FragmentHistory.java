package info.androidhive.navigationdrawer.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.adapters.ListHistoryAdapter;
import info.androidhive.navigationdrawer.db.DbController;
import info.androidhive.navigationdrawer.model.Lst_item_hist;

/**
 * Created by ahmed on 19/08/17.
 */

public class FragmentHistory extends Fragment implements ListView.OnItemClickListener, ImageView.OnClickListener {


    View viewRoot = null;
    Context context = null;
    ArrayList<Lst_item_hist> hists = new ArrayList<Lst_item_hist>();
    @BindView(R.id.lvHist)
    ListView listView;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    public ListHistoryAdapter adapter = null;
    DbController dbController = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewRoot = inflater.inflate(R.layout.fragment_history, container, false);
        context = container.getContext();

        ButterKnife.bind(this, viewRoot);
        setHasOptionsMenu(true);

        dbController = new DbController(context);
        adapter = new ListHistoryAdapter(context, R.layout.lst_hist_item, hists);

        getPathsFroDataBase();

        if (hists.size() != 0) {
            tvEmpty.setVisibility(View.GONE);
            adapter = new ListHistoryAdapter(context, R.layout.lst_hist_item, hists);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            adapter.notifyDataSetChanged();

        } else {
            tvEmpty.setVisibility(TextView.VISIBLE);
        }


        return viewRoot;
    }

    private void getPathsFroDataBase() {

        String sql = "select * from " + DbController.tblAccessedPaths;
        ArrayList<HashMap<String, String>> hashMaps = dbController.getData(sql);
        Log.e("hash", hashMaps.toString());
        if (!hashMaps.isEmpty()) {
            for (int i = 0; i < hashMaps.size(); i++) {
                HashMap<String, String> hashMap = hashMaps.get(i);
                String strPath = hashMap.get(DbController.filePath);
                hists.add(new Lst_item_hist(strPath));
            }
        }

    }

    private Drawable resize(Drawable image, int w, int h) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, w, h, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_clear_all, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clearAllHist:
                clearAllHistory();
                break;
            default:
                break;

        }
        return true;
    }

    private void clearAllHistory() {
        dbController.exeQuery("delete * from " + DbController.tblAccessedPaths);
        dbController.exeQuery("delete from " + DbController.tblAccessedPaths);

        tvEmpty.setVisibility(View.VISIBLE);

        hists.clear();
        adapter.notifyDataSetChanged();

    }

    String selectedPath = "";
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        //getting the path of clicked item
        Lst_item_hist hist = ((Lst_item_hist) listView.getItemAtPosition(position));
        selectedPath = hist.getStrPathOfDirec();
        fireToast(selectedPath);

        //listView.getChildAt(position).findViewById(R.id.ivClose).setOnClickListener(this);
        //adapter.notifyDataSetChanged();
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Delete");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getTitle().toString()){
                    case "Delete":
                        deleteRowPathFromDataBase();
                        hists.clear();
                        adapter = new ListHistoryAdapter(context, R.layout.lst_hist_item, hists);
                        getPathsFroDataBase();
                        if (hists.size() != 0) {
                            tvEmpty.setVisibility(View.GONE);
                            adapter = new ListHistoryAdapter(context, R.layout.lst_hist_item, hists);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(FragmentHistory.this);
                            adapter.notifyDataSetChanged();

                        } else {
                            tvEmpty.setVisibility(TextView.VISIBLE);
                        }
                        break;
                    default:
                        break;

                }

                return true;
            }
        });


    }
    public void deleteRowPathFromDataBase(){

        DbController dbController = new DbController(context);
        String sql = "delete from "+DbController.tblAccessedPaths+" where "+DbController.filePath+" = '"+selectedPath+"'";
        dbController.exeQuery(sql);
        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
    }

    private void fireToast(String path) {
        Toast.makeText(context, path, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

    }


}
