package info.androidhive.navigationdrawer.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.activity.MainActivity;
import info.androidhive.navigationdrawer.adapters.GridViewAdapter;
import info.androidhive.navigationdrawer.adapters.ListViewAdapter;
import info.androidhive.navigationdrawer.model.AnyItem;


public class FragmentPictures extends Fragment implements GridView.OnItemClickListener,
        AbsListView.OnScrollListener,
        ImageView.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ArrayList<AnyItem> anyItems = new ArrayList<AnyItem>();
    Context context = null;
    View viewRoot = null;
    String path = null;
    ArrayList<File> files = new ArrayList<File>();
    @BindView(R.id.mainContainer)
    SwipeRefreshLayout mainContainer;
    @BindView(R.id.tvPath)
    TextView tvPath;//don't init as null
    boolean toggle = true;
    GridView gridView = null;
    GridViewAdapter gridViewAdapter = null;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    ArrayList<ArrayList<AnyItem>> alStoredItems = new ArrayList<ArrayList<AnyItem>>();
    ArrayList<String> alStoredPaths = new ArrayList<String>();
    Animation animFadeIn = null;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    final static String KEY_HOME_PATH = "HOME_PATH_DCIM";
    final static String KEY_PATHS_ARRAYLIST = "PATHS_LIST_DCIM";
    final static int MODE = Context.MODE_PRIVATE;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            viewRoot = inflater.inflate(R.layout.fragment_sdcard0, container, false);
            //mainContainer = (LinearLayout) viewRoot;
            context = getActivity();
            context = container.getContext();
            //ButterKnife.bind(context, viewRoot);
            ButterKnife.bind(this, viewRoot);
            setHasOptionsMenu(true);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//            sharedPreferences = getActivity().getSharedPreferences(HOME_PATH, MODE);
            editor = sharedPreferences.edit();

            path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath();
            tvPath.setText(path);


            mainContainer.removeAllViews();
            initFolders();
            alStoredItems.add(anyItems);
            alStoredPaths.add(path);
            gridView = createGridView();
            gridViewAdapter = (GridViewAdapter) gridView.getAdapter();
            mainContainer.addView(gridView);
            mainContainer.setOnRefreshListener(this);
            mainContainer.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

            ivBack.setVisibility(View.INVISIBLE);
            ivBack.setOnClickListener(this);


            if (sharedPreferences.contains(KEY_HOME_PATH)) {
                tvPath.setText(sharedPreferences.getString(KEY_HOME_PATH, ""));
            } else {
                path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath();
                tvPath.setText(path);

            }


        } catch (Exception e) {
            //fireToast(e.getMessage().toString());
            fireAlertDlg(e.getMessage().toString());
        }

        return viewRoot;
    }


    @Override
    public void onStart() {
        super.onStart();

//        if (alStoredPaths.size() >=2 )
//        {
//            ivBack.setVisibility(View.VISIBLE);
//        }

    }

    @Override
    public void onResume() {
        super.onResume();


        if (sharedPreferences.contains(KEY_HOME_PATH)) {
            path = sharedPreferences.getString(KEY_HOME_PATH, "");
            tvPath.setText(path);
        } else {
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            tvPath.setText(path);
        }
        anyItems.clear();
        initFolders();
        ((GridViewAdapter) gridView.getAdapter()).notifyDataSetChanged();
//-------------------------------------------------------------------
        if (sharedPreferences.contains(KEY_PATHS_ARRAYLIST)) {
            //Retrieve the values
            Set<String> set = sharedPreferences.getStringSet(KEY_PATHS_ARRAYLIST, null);
            alStoredPaths.clear();
            alStoredPaths.addAll(set);
            //alStoredPaths = (ArrayList<String>) set;

        }

        //fireToast("size = " + alStoredPaths.size());
        Log.e("res", alStoredPaths.toString());
        if (alStoredPaths.size() >= 2) {
            ivBack.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onRefresh() {

    }

    boolean hideToolBar = false;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (hideToolBar) {
            ((MainActivity) getActivity()).getSupportActionBar().hide();
        } else {
            ((MainActivity) getActivity()).getSupportActionBar().show();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (visibleItemCount > 20) {
            hideToolBar = true;

        } else if (firstVisibleItem < -5) {
            hideToolBar = false;
        }
    }

    class AnsyncTaskGetAllFolders extends AsyncTask<String, Void, ArrayList<File>> {

        Context context = null;
        ProgressDialog dialog = null;

        public AnsyncTaskGetAllFolders(Context context) {
            context = this.context;
            initDlg();
        }

        private void initDlg() {
            dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Get all folders ...");
            dialog.setCancelable(false);
            dialog.setTitle("Please wait");
        }

        @Override
        protected void onPostExecute(ArrayList<File> files) {
            super.onPostExecute(files);

            if (dialog.isShowing())
                dialog.dismiss();


        }

        @Override
        protected ArrayList<File> doInBackground(String... params) {


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (!dialog.isShowing())
                dialog.show();

        }


    }

    public void fireAlertDlg(String msg) {
        new AlertDialog.Builder(getActivity())
                .setMessage(msg)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public void fireToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void initFolders() {
        files = getAllFolders(path);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            String fileName = file.getName();
            Date fileDate = getCreationDateForFile(file.getAbsolutePath());
            String fileAbsPath = file.getAbsolutePath();
            anyItems.add(new AnyItem(fileName, setFormatForDate(fileDate), fileAbsPath, R.drawable.folder));
        }

    }

    public GridView createGridView() {
        GridView gridView = new GridView(getActivity());
//        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(p);
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setBackgroundColor(Color.parseColor("#ff6666"));
        gridView.setNumColumns(2);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(context, anyItems);
        gridView.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(this);
//        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.grid_item_anim);
//        GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);
//        gridView.setLayoutAnimation(controller);
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

    // for checking if a filepath is an image, would be:
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    //And for video:
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public String setFormatForDate(Date date) {
        String DATE_FORMAT = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        System.out.println("Formated Date " + sdf.format(date));
        return sdf.format(date);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //get the clicke element by pos
        AnyItem anyItem = anyItems.get(position);
        //fireToast(anyItem.getStrFolderPath().toString());
        path = anyItem.getStrFolderPath().toString();
        alStoredPaths.add(path);
//        animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
//        ivBack.startAnimation(animFadeIn);
        ivBack.setVisibility(View.VISIBLE);
        anyItems.clear();
        initFolders();
        gridViewAdapter.notifyDataSetChanged();
        //gridView.setAdapter(new GridViewAdapter(getActivity(), anyItems));
        //((GridViewAdapter)gridView.getAdapter()).notifyDataSetChanged();
        //mainContainer.removeAllViews();
        //mainContainer.addView(createGridView());
        tvPath.setText(path);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //getActivity().getMenuInflater().inflate(R.menu.);
        super.onCreateOptionsMenu(menu, inflater);
        // Implementing ActionBar Search inside a fragment
//        MenuItem item = menu.add("Search");
//        item.setIcon(android.R.drawable.ic_menu_search); // sets icon
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        SearchView sv = new SearchView(getActivity());
//
//        // modifying the text inside edittext component
//        int id = sv.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//        TextView textView = (TextView) sv.findViewById(id);
//        textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
//        textView.setHint("Title/Region/Year/Language/");
//        textView.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
//        textView.setTextColor(getResources().getColor(android.R.color.holo_red_light));
//
//        // implementing the listener
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });
//        item.setActionView(sv);

    }

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
                mainContainer.removeAllViews();
                mainContainer.addView(createListView());
                break;
            case R.id.fileFinderSetAsHome:
                setSharedPref();
                break;
            case R.id.fileFinderGridView:
                mainContainer.removeAllViews();
                mainContainer.addView(createGridView());
                break;
            case R.id.sortBy:
                break;
            case R.id.direcSortMode:
                break;
            case R.id.fileFinderHome:
                getHomePath();
                break;
            default:
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    private void getHomePath() {
        if (sharedPreferences.contains(KEY_HOME_PATH)) {
            String homePath = sharedPreferences.getString(KEY_HOME_PATH, "");
            tvPath.setText(homePath);
            path = homePath;
            anyItems.clear();
            initFolders();
            ((GridViewAdapter) gridView.getAdapter()).notifyDataSetChanged();

            if (alStoredPaths.size() >= 1) {
                ivBack.setVisibility(View.VISIBLE);
            }

        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
            tvPath.setText(path);
            ivBack.setVisibility(View.GONE);
            anyItems.clear();
            initFolders();
            ((GridViewAdapter) gridView.getAdapter()).notifyDataSetChanged();

        }
        if (sharedPreferences.contains(KEY_PATHS_ARRAYLIST)) {
            Set<String> set = sharedPreferences.getStringSet(KEY_PATHS_ARRAYLIST, null);
            alStoredPaths.clear();
            alStoredPaths.addAll(set);

        }


        Log.e("res2", alStoredPaths.toString());


    }

    ArrayList<String> pathsCopy = new ArrayList<String>();

    private void setSharedPref() {

        //Set the values
        Set<String> set = new HashSet<String>();
        //pathsCopy = alStoredPaths;
        set.addAll(alStoredPaths);
        editor.putStringSet(KEY_PATHS_ARRAYLIST, set);
        editor.putString(KEY_HOME_PATH, path);
        editor.commit();

        fireToast("set as home");
    }

    private ListView createListView() {


        ListView listView = new ListView(context);
        anyItems.clear();
        initFolders();
        ListViewAdapter listViewAdapter = new ListViewAdapter(context, anyItems);
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();

        return listView;

    }

    int count = 0;
    @Override
    public void onClick(View v) {

        Log.e("onclick", alStoredPaths.toString());

        if (alStoredPaths.size() == 2) {
            // do nothing
            fireToast("Home Directory");
            ivBack.setVisibility(View.GONE);
        }

        //alStoredItems.remove(alStoredItems.size() -1);
        alStoredPaths.remove(alStoredPaths.size() - 1);

        path = alStoredPaths.get(alStoredPaths.size() - 1);
        tvPath.setText(path);
        anyItems.clear();
        initFolders();
        //for refreshing the gridview
        ((GridViewAdapter) gridView.getAdapter()).notifyDataSetChanged();


    }
}
