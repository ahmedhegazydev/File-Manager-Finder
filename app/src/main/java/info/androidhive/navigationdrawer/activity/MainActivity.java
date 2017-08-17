package info.androidhive.navigationdrawer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
//import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.fragment.FragmentDcim;
import info.androidhive.navigationdrawer.fragment.FragmentDownload;
import info.androidhive.navigationdrawer.fragment.FragmentExtSdCard;
import info.androidhive.navigationdrawer.fragment.FragmentForAnyPath;
import info.androidhive.navigationdrawer.fragment.FragmentImages;
import info.androidhive.navigationdrawer.fragment.FragmentMovies;
import info.androidhive.navigationdrawer.fragment.FragmentMusic;
import info.androidhive.navigationdrawer.fragment.FragmentPictures;
import info.androidhive.navigationdrawer.fragment.FragmentSdCard0;
import info.androidhive.navigationdrawer.other.CircleTransform;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_SDCARD = "TAG_SDCARD";
    private static final String TAG_EXTSDCARD = "TAG_EXTSDCARD";
    private static final String TAG_DCIM = "TAG_DCIM";
    private static final String TAG_DOWNLOAD = "TAG_DOWNLOAD";
    private static final String TAG_MOVIES = "TAG_MOVIES";
    private static final String TAG_MUSIC = "TAG_MUSIC";
    private static final String TAG_PICTURES = "TAG_PICTURES";
    private static final String TAG_IMAGES = "TAG_IMAGES";
    private static final String TAG_VIDEOS = "TAG_VIDEOS";
    private static final String TAG_AUDIO = "TAG_AUDIO";
    private static final String TAG_APKS = "TAG_APKS";
    private static final String TAG_QUICK_ACCESS = "TAG_QUICK_ACCESS";
    private static final String TAG_RECENT_FILES = "TAG_RECENT_FILES";
    public static String CURRENT_TAG = TAG_SDCARD;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setVisibility(View.GONE);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_SDCARD;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText("Ravi Tamada");
        txtWebsite.setText("www.androidhive.info");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        //navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu(navigationView.getMenu());

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    public static String KEY_HOME_PATH = "";
    private Fragment getHomeFragment() {

        Fragment fragment = null;
        switch (navItemIndex) {
            case 0:
                fragment = new FragmentSdCard0();
                break;
            case 1:
                fragment = new FragmentExtSdCard();
                break;
            case 2:
                fragment = new FragmentDcim();
                break;
            case 3:
                fragment = new FragmentDownload();
                break;
            case 4:
                fragment = new FragmentMovies();
                break;
            case 5:
                fragment = new FragmentMusic();
                break;
            case 6:
                fragment = new FragmentPictures();
                break;
            case 7:
                fragment = new FragmentImages();
                break;

//            case 3:
//                fragment = new FragmentForAnyPath();
//                Bundle bundle = new Bundle();
//                KEY_HOME_PATH = CURRENT_TAG;
//                bundle.putString(KEY_HOME_PATH, CURRENT_TAG);
//                bundle.putString();
//                fragment.setArguments(bundle);
            default:
                fragment = new FragmentSdCard0();
                break;
        }
        return fragment;
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu(Menu menu) {

        //mark all of menu items as non selected
        for (int i = 0; i < menu.size(); i++){
            if (!menu.getItem(i).hasSubMenu()){
                menu.getItem(i).setChecked(false);
            }
//            else {
//                selectNavMenu((Menu) menu.getItem(i));
//            }
        }

        //mark only one as selected
        menu.getItem(navItemIndex).setChecked(true);

    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_sdCard0:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_SDCARD;
                        break;
                    case R.id.nav_extSdCard:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_EXTSDCARD;
                        break;
                    case R.id.nav_DCIM:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_DCIM;
                        break;
                    case R.id.nav_Download:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_DOWNLOAD;
                        break;
                    case R.id.nav_Movies:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_music:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_MUSIC;
                        break;
                    case R.id.nav_pictures:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_PICTURES;
                        break;
                    case R.id.nav_images:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_IMAGES;
                        break;

//                    case R.id.nav_about_us:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
//                        drawer.closeDrawers();
//                        return true;
//                    case R.id.nav_privacy_policy:
//                        // launch new intent instead of loading fragment
//                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
//                        drawer.closeDrawers();
//                        return true;

                    default:
                        navItemIndex = 0;
                        break;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_SDCARD;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
//        if (navItemIndex == 0) {
//            getMenuInflater().inflate(R.menu.main, menu);
//        }
//
//        // when fragment is notifications, load the menu created for notifications
//        if (navItemIndex == 3) {
//            getMenuInflater().inflate(R.menu.notifications, menu);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
//        if (navItemIndex == 0)
//            fab.show();
//        else
//            fab.hide();
    }
}
