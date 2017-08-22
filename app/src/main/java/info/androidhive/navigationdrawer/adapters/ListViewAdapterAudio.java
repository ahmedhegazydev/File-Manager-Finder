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

public class ListViewAdapterAudio extends BaseAdapter {

    Context context = null;
    ArrayList<LstViewImageItem> list = new ArrayList<LstViewImageItem>();
    int listOrGrid = 0;
    boolean checkedOrNot = false;


    public ListViewAdapterAudio(Context context, ArrayList<LstViewImageItem> list, int listOrGrid, boolean checkedOrNot) {
        this.context = context;
        this.list = list;
        this.listOrGrid = listOrGrid;
        this.checkedOrNot = checkedOrNot;
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
        View view1 = null;

        if (listOrGrid == 0)
            if (checkedOrNot) {
                view1 = LayoutInflater.from(context).inflate(R.layout.lst_image_item_checked, null);
            } else {
                view1 = LayoutInflater.from(context).inflate(R.layout.lst_image_item, null);
            }

        else if (checkedOrNot) {
            view1 = LayoutInflater.from(context).inflate(R.layout.grid_image_item_checked, null);
        } else {
            view1 = LayoutInflater.from(context).inflate(R.layout.grid_image_item, null);
        }


        ImageView ivThumbnail = (ImageView) view1.findViewById(R.id.ivThumbnail);
        //ivThumbnail.setImageResource(R.drawable.audio);

        TextView tvImageName = null;
        TextView tvImageSize = null;
        TextView tvImageCreationDate = null;
        tvImageName = (TextView) view1.findViewById(R.id.tvImageName);
        tvImageSize = (TextView) view1.findViewById(R.id.tvImageSize);
        tvImageCreationDate = (TextView) view1.findViewById(R.id.tvCreationDate);
//        CheckBox checkBox = null;
//        if (checkedOrNot) {
//            if (listOrGrid == 0)
//                checkBox = (CheckBox) view1.findViewById(R.id.cbList);
//            else
//                checkBox = (CheckBox) view1.findViewById(R.id.cbGrid);
//
//        }


        LstViewImageItem lstViewImageItem = getItem(position);


        tvImageName.setText(shortenNameLen(lstViewImageItem.getImgName().toString()));
        Picasso.with(context).load(R.drawable.audio).into(ivThumbnail);
        tvImageCreationDate.setText(lstViewImageItem.getImgCreationDate());
        tvImageSize.setText(lstViewImageItem.getImgSizeKb()+"KB");
//        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });


        return view1;
    }

    private CharSequence shortenNameLen(String string) {

        if (string.length() > 15){
            return string.substring(0, 5)+"..."+string.substring(string.length() - 6, string.length());
        }

        return string;
    }
}
