package info.androidhive.navigationdrawer.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.adapters.ListViewAdapterImages;
import info.androidhive.navigationdrawer.model.FileUtils;
import info.androidhive.navigationdrawer.model.LstViewImageItem;

/**
 * Created by ahmed on 16/08/17.
 */

public class FragmentImages extends Fragment implements ListView.OnItemClickListener {

    View viewRoot = null;
    Context context = null;
    @BindView(R.id.lvFragImages)
    ListView listView;
    ListViewAdapterImages listViewAdapterImages = null;
    ArrayList<LstViewImageItem> listImages = new ArrayList<LstViewImageItem>();
    ArrayList<File> listFiles = new ArrayList<File>();
    List<String> tFileList = new ArrayList<String>();
    private String SD_CARD_ROOT;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        viewRoot = inflater.inflate(R.layout.fragment_images, container, false);
        context = getActivity();

        setHasOptionsMenu(true);

        ButterKnife.bind(this, viewRoot);

        fireToast("images fragment");

        //getting the path of external sd card for getting all images from there
        File mFile = Environment.getExternalStorageDirectory();
        SD_CARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
        SD_CARD_ROOT = mFile.toString();

        //getting all images by executing the async task
        new AsyncTastGetAllImages(context).execute(SD_CARD_ROOT);

        listViewAdapterImages = new ListViewAdapterImages(context, listImages);
        listView.setAdapter(listViewAdapterImages);
        listView.setOnItemClickListener(this);
        //listViewAdapterImages.notifyDataSetChanged();

        return viewRoot;
    }

    private void fireToast(String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    // for checking if a filepath is an image, would be:
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public String setFormatForDate(Date date) {
        String DATE_FORMAT = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        System.out.println("Formated Date " + sdf.format(date));
        return sdf.format(date);
    }

    public Date getCreationDateForFile(String filePath) {
        File file = new File(filePath);
        Date lastModDate = new Date(file.lastModified());
        //Log.i("creation", "File last modified @ : "+ lastModDate.toString());
        //return lastModDate.toString();
        return lastModDate;
    }

    public List<String> findFiles() {


        Resources resources = getActivity().getResources();
        // array of valid image file extensions
        String[] imageTypes = resources.getStringArray(R.array.image);

        FilenameFilter[] filter = new FilenameFilter[imageTypes.length];
        int i = 0;
        for (final String type : imageTypes) {
            filter[i] = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("." + type);
                }
            };
            i++;
        }

        FileUtils fileUtils = new FileUtils();
        File[] allMatchingFiles = fileUtils.listFilesAsArray(new File(SD_CARD_ROOT), filter, -1);
        for (File f : allMatchingFiles) {
            tFileList.add(f.getAbsolutePath());
            listFiles.add(f);
        }

        //Log.i("paths", tFileList.toString());

        return tFileList;
    }

    class AsyncTastGetAllImages extends AsyncTask<String, Void, ArrayList<LstViewImageItem>> {

        Context context = null;
        ProgressDialog dialog = null;


        AsyncTastGetAllImages(Context context) {
            this.context = context;
            dialog = new ProgressDialog(this.context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("Please Wait");
            dialog.setCancelable(false);
            dialog.setMessage("Getting all images ... ");
            dialog.setIndeterminate(true);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!dialog.isShowing()) {
                dialog.show();
            }

        }

        @Override
        protected ArrayList<LstViewImageItem> doInBackground(String... params) {
            findFiles();
            for (int i = 0; i < listFiles.size(); i++) {
                File file = listFiles.get(i);
                String imgName = "";
                Date imgCreationDate = null;
                String imgSize = "";
                String imgPath = "";

                imgName = file.getName();//image name associated with extention
//            imgSize = file.getTotalSpace()+"";//image size in kb
                double bytes = file.length();
                double kilobytes = (bytes / 1024);
                NumberFormat formatter = new DecimalFormat("#0.00");
                imgSize = formatter.format(kilobytes);
                imgCreationDate = getCreationDateForFile(file.getAbsolutePath());//image creation date or last modification date
                imgPath = tFileList.get(i);//image uri

                LstViewImageItem lstViewImageItem = new
                        LstViewImageItem(imgPath, imgName, setFormatForDate(imgCreationDate), imgSize + "KB");
                listImages.add(lstViewImageItem);

            }

            return listImages;
        }

        @Override
        protected void onPostExecute(ArrayList<LstViewImageItem> lstViewImageItems) {
            super.onPostExecute(lstViewImageItems);

            if (dialog.isShowing())
                dialog.dismiss();

            if (!lstViewImageItems.isEmpty() && !lstViewImageItems.equals(null)) {
                listViewAdapterImages.notifyDataSetChanged();
            }


        }
    }

    LstViewImageItem item = null;
    File file = null;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        item = listImages.get(position);
        file = listFiles.get(position);
        //--------------------------------------------------------------
        setPopUpMenu(view);
        //-------------------------------------------------------


    }

    private void setPopUpMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_for_one_folder, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.folderAddToBookmark:
                        //fireToast("add to bookmark");
                        fileAddToBookMark();
                        break;
                    case R.id.folderCopy:
                        //fireToast("copy");
                        fileCopy();
                        break;
                    case R.id.folderCut:
                        fileCut();
                        break;
                    case R.id.folderRename:
                        fileRename();
                        break;
                    case R.id.folderProperties:
                        break;
                    case R.id.folderDelete:
                        fileDelete();
                        break;
                    default:
                        break;

                }
                //--------------------------------------

                return true;
            }
        });
        popupMenu.show();
    }

    private void fileDelete() {

    }

    private void fileCopy() {

    }

    private void fileCut() {

    }

    private void fileAddToBookMark() {
    }

    private void fileRename() {

        View view = LayoutInflater.from(context).inflate(R.layout.file_rename, null);
        EditText etOldName = (EditText) view.findViewById(R.id.etOldFileName);
        final EditText etNewName = (EditText) view.findViewById(R.id.etNewFileName);
        
        etOldName.setText(item.getImgName());
        
        
        
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(context);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Log.e("ext", getFileExt(file.getName()));
                if (etNewName.getText().toString().trim().length() == 0){
                    etNewName.requestFocus();
                    fireToast("Enter name !!!! ");
                    try {
                        dialog.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }else{
                    // File (or directory) with old name
                    File file = new File(item.getImgUri());
                    // File (or directory) with new name
                    File file2 = new File(file.getPath()+"/"+etNewName.getText().toString()+"."+getFileExt(file.getName()));
                    if (file2.exists())
                        try {
                            throw new java.io.IOException("file exists");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    // Rename file (or directory)
                    boolean success = file.renameTo(file2);
                    if (success) {
                        // File was not successfully renamed
                        fireToast("File renamed ");
                    }
                }
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        if (!alertDialog.isShowing()){
            alertDialog.show();
        }
        

    }

    public String getFileExt(String fileName){
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }


    boolean toggle = true;
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!toggle) {
            menu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu1, menu);
            toggle = !toggle;
        } else {
            menu.clear();
            getActivity().getMenuInflater().inflate(R.menu.menu2, menu);
            toggle = !toggle;
        }
        MenuItem item = menu.add("Search");
        item.setIcon(android.R.drawable.ic_menu_search); // sets icon
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView sv = new SearchView(getActivity());

        // modifying the text inside edittext component
        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) sv.findViewById(id);
        textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        textView.setHint("Search for file ...");
        textView.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
        textView.setTextColor(getResources().getColor(android.R.color.holo_red_light));

        // implementing the listener
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        item.setActionView(sv);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.fileFinderExit:
                //System.exit(0);
                getActivity().finish();

                break;
            case R.id.fileFinderHistory:
                break;
            case R.id.fileFinderListView:
                break;
            case R.id.fileFinderSetAsHome:
                break;
            case R.id.fileFinderGridView:
                break;
            case R.id.sortBy:
                break;
            case R.id.direcSortMode:
                break;
            case R.id.fileFinderHome:
                break;
            default:
                break;

        }


        return super.onOptionsItemSelected(item);
    }



//    private ArrayList<LstViewImageItem> getAllImages(String path, File file) {
//        ArrayList<LstViewImageItem> listImages = new ArrayList<LstViewImageItem>();
//        ArrayList<File> listFiles = new ArrayList<File>();
//
//        File f = new File(path);
//        File[] files = f.listFiles();
//        for (File inFile : files) {
//            if (inFile.isDirectory()) {
//                // is directory
//
//
//            } else {
//                if (isImageFile()){
//                    listFiles.add();
//                }
//            }
//        }
//
//        return listImages;
//    }
}
