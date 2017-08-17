package info.androidhive.navigationdrawer.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.adapters.GridViewAdapter;
import info.androidhive.navigationdrawer.model.AnyItem;

/**
 * Created by ahmed on 12/08/17.
 */

public class FragmentExtSdCard extends Fragment {


    private Context context = null;
    private View viewRoot = null;
    private FrameLayout flMainContainer = null;
    private ArrayList<AnyItem> anyItems = new ArrayList<AnyItem>();
    String path = "";
    ArrayList<File> files = new ArrayList<File>();



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        viewRoot = inflater.inflate(R.layout.fragment_ext_sdcard, container, false);
        context = container.getContext();
        flMainContainer = (FrameLayout) viewRoot;
        if (isAvailable() || isWritable()){
            flMainContainer.removeAllViews();
            flMainContainer.addView(createGridView());
        }else
        {
            fireToast("Please, check if the external storage (SD Card) is mounted or available");
        }

        return viewRoot;
    }

    private void fireToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public GridView createGridView() {

        path = getSdCardPath();
//        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        files = getAllFolders(path);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            String fileName = file.getName();
            Date fileDate = getCreationDateForFile(file.getAbsolutePath());
            String fileAbsPath = file.getAbsolutePath();
            anyItems.add(new AnyItem(fileName, setFormatForDate(fileDate), fileAbsPath, R.drawable.folder));
        }

        GridView gridView = new GridView(getActivity());
        gridView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        gridView.setVerticalSpacing(5);
        gridView.setHorizontalSpacing(5);
        gridView.setBackgroundColor(Color.parseColor("#ff6666"));
        gridView.setNumColumns(2);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(context, anyItems);
        gridView.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();


        return gridView;
    }

    public Date getCreationDateForFile(String filePath) {
        File file = new File(filePath);
        Date lastModDate = new Date(file.lastModified());
        //Log.i("creation", "File last modified @ : "+ lastModDate.toString());
        //return lastModDate.toString();
        return lastModDate;
    }

    public ArrayList<File> getAllFolders(String path) {
        ArrayList<File> fileArrayList = new ArrayList<File>();
        File f = new File(path);
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
                fileArrayList.add(inFile);
            } else {

            }
        }

        return fileArrayList;
    }

    public String setFormatForDate(Date date) {
        String DATE_FORMAT = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        System.out.println("Formated Date " + sdf.format(date));
        return sdf.format(date);
    }


    /**
     * @return True if the external storage is available. False otherwise.
     */
    public static boolean isAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/";
    }

    /**
     * @return True if the external storage is writable. False otherwise.
     */
    public static boolean isWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;

    }



}
