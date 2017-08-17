package info.androidhive.navigationdrawer.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.model.AnyItem;

import static java.security.AccessController.getContext;

/**
 * Created by ahmed on 05/08/17.
 */

public class GridViewAdapter extends BaseAdapter implements ImageView.OnClickListener{


    Context context = null;
    ArrayList<AnyItem> anyItems = null;
    TextView tvName = null;
    AnyItem anyItem = null;
    TextView tvNumFoldersFiles = null;
    String path = "";
    int countFolders = 0, countFiles = 0;


    public GridViewAdapter(Context context, ArrayList<AnyItem> anyItems) {
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


        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, null);

        Animation animTrans = AnimationUtils.loadAnimation(context,R.anim.grid_item_anim);
        Animation animFadeIn = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        view.startAnimation(animFadeIn);

        ImageView ivAudioOrVidOr  = (ImageView) view.findViewById(R.id.ivAudioOrVideoOr);
        View view1 = view.findViewById(R.id.rl12);
        tvName = (TextView) view1.findViewById(R.id.tvName);
        TextView tvDate = (TextView) view1.findViewById(R.id.tvDate);
        ImageView ivOptions = (ImageView) view1.findViewById(R.id.ivOptions);
        tvNumFoldersFiles = (TextView) view.findViewById(R.id.tvNumFoldFiles);

        anyItem = getItem(position);


        setFolderName();
        tvDate.setText(anyItem.getStrDate());
        Picasso.with(context).load(anyItem.getDrawableId()).into(ivAudioOrVidOr);
        ivOptions.setOnClickListener(this);

        // assetManager = getActivity().getAssets();
//            typeface= Typeface.createFromAsset(assetManager, "fonts/ShadowsIntoLight.ttf");
        tvNumFoldersFiles.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ShadowsIntoLight.ttf"));


        path = anyItem.getStrFolderPath();
        getAllFolders(path);
        tvNumFoldersFiles.setText(countFiles+" files, "+countFolders+" folders");

        return view;

    }

    public ArrayList<File> getAllFolders(String path) {
        ArrayList<File> fileArrayList = new ArrayList<File>();
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
                fileArrayList.add(inFile);
                countFolders++;
            } else {
                countFiles++;
            }
        }

        return fileArrayList;
    }

    private void setFolderName() {
        if (anyItem != null && tvName != null){
            String str = anyItem.getStrName();
            if (str.length() >= 15){
                str = str.substring(0, 5)+"..."+str.substring(10, 14);
            }
            tvName.setText(str);
        }
    }

    @Override
    public void onClick(View v) {

        showPopupMenu(context, v);

    }

    private void showPopupMenu(Context context, View v) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_for_one_folder, popupMenu.getMenu());
        popupMenu.show();

    }


}
