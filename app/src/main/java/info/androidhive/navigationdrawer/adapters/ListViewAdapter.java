package info.androidhive.navigationdrawer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;



import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.model.AnyItem;

/**
 * Created by ahmed on 11/08/17.
 */

public class ListViewAdapter extends BaseAdapter implements ImageView.OnClickListener{

    Context context= null;
    ArrayList<AnyItem> anyItems = null;

    public ListViewAdapter(Context context, ArrayList<AnyItem> anyItems) {
        this.context = context;
        this.anyItems = anyItems;
    }

    @Override
    public int getCount() {
        return this.anyItems.size();
    }

    @Override
    public AnyItem getItem(int position) {
        return this.anyItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
        ImageView ivAudioOrVid = (ImageView) view.findViewById(R.id.ivAudioOrVideoOr),
                ivOptions = (ImageView) view.findViewById(R.id.ivOptions);
        TextView tvFolderName = (TextView) view.findViewById(R.id.tvName),
                tvFolderCreationDate = (TextView) view.findViewById(R.id.tvDate);

        AnyItem anyItem = getItem(position);

        tvFolderCreationDate.setText(anyItem.getStrDate());
        tvFolderName.setText(anyItem.getStrName());
        ivOptions.setOnClickListener(this);
        Picasso.with(context).load(anyItem.getDrawableId()).into(ivAudioOrVid);


        return view;
    }

    @Override
    public void onClick(View v) {

        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_for_one_folder, popupMenu.getMenu());
        popupMenu.show();


    }
}
