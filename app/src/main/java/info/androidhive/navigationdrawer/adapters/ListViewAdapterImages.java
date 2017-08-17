package info.androidhive.navigationdrawer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.model.LstViewImageItem;

/**
 * Created by ahmed on 16/08/17.
 */

public class ListViewAdapterImages extends BaseAdapter {

    Context context = null;
    ArrayList<LstViewImageItem> list = new ArrayList<LstViewImageItem>();

    public ListViewAdapterImages(Context context, ArrayList<LstViewImageItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public LstViewImageItem getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view1 = view1 = LayoutInflater.from(context).inflate(R.layout.lst_image_item, null);


        ImageView ivThumbnail = (ImageView) view1.findViewById(R.id.ivThumbnail);
        TextView tvImageName = null;
        TextView tvImageSize = null;
        TextView tvImageCreationDate = null;
        tvImageName = (TextView) view1.findViewById(R.id.tvImageName);
        tvImageSize = (TextView) view1.findViewById(R.id.tvImageSize);
        tvImageCreationDate = (TextView) view1.findViewById(R.id.tvCreationDate);


        LstViewImageItem lstViewImageItem = getItem(position);


        tvImageName.setText(lstViewImageItem.getImgName().toString());
        Picasso.with(context).load(new File(lstViewImageItem.getImgUri())).into(ivThumbnail);
        tvImageCreationDate.setText(lstViewImageItem.getImgCreationDate());
        tvImageSize.setText(lstViewImageItem.getImgSizeKb());

        return view1;
    }
}
