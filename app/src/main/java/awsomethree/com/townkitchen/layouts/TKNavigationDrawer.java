package awsomethree.com.townkitchen.layouts;

import com.parse.ParseUser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.adapters.TKDrawerListAdapter;
import awsomethree.com.townkitchen.models.NavDrawerItem;

/**
 * Created by smulyono on 3/22/15.
 */
public class TKNavigationDrawer extends DrawerLayout {
    private MainActivity mainActivity;
    private ActionBarDrawerToggle drawerToggle;
    private int drawerContainerRes;
    private ListView lvDrawer;
    private Toolbar toolbar;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private ArrayList<FragmentNavItem> drawerNavItems;
    private TKDrawerListAdapter drawerAdapter;


    public TKNavigationDrawer(Context context) {
        super(context);
    }

    public TKNavigationDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TKNavigationDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     *
     * @param drawerListView, list view of the menu
     * @param drawerToolbar, toolbar
     * @param drawerContainerResId, fragment id to shows up the selected menu
     */
    public void setupDrawerConfiguration(ListView drawerListView, Toolbar drawerToolbar, int drawerContainerResId, MainActivity parentMain){
        this.mainActivity = parentMain;
        // initialization of the arraylist for menu and adapter
        navDrawerItems = new ArrayList<NavDrawerItem>(); // contains all menu items
        drawerNavItems = new ArrayList<FragmentNavItem>(); // contains the fragments

        // setup the container id
        drawerContainerRes = drawerContainerResId;
        // setup the list view
        lvDrawer = drawerListView;
        // when menu is clicked...
        lvDrawer.setOnItemClickListener(new FragmentDrawerItemListener());
        // Set the adapter for the list view
        drawerAdapter = new TKDrawerListAdapter(getActivity(), navDrawerItems);
        lvDrawer.setAdapter(drawerAdapter);

        // setup list view on click listener

        // setup toolbar
        toolbar = drawerToolbar;

        // set action buttons
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        drawerToggle = setupDrawerToggle();
        setDrawerListener(drawerToggle);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(), this, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    // addNavItem("First", R.mipmap.ic_one, "First Fragment", FirstFragment.class)
    public void addNavItem(String navTitle, int icon, String windowTitle, Class<? extends Fragment> fragmentClass) {
        addNavItem(navTitle, icon, windowTitle, fragmentClass, true);
    }

    // addNavItem("First", R.mipmap.ic_one, "First Fragment", FirstFragment.class)
    public void addNavItem(String navTitle, int icon, String windowTitle, Class<? extends Fragment> fragmentClass
            , boolean showToolbar) {
        addNavItem(navTitle, icon, windowTitle, fragmentClass, showToolbar, true);
    }

    // addNavItem("First", R.mipmap.ic_one, "First Fragment", FirstFragment.class)
    public void addNavItem(String navTitle, int icon, String windowTitle, Class<? extends Fragment> fragmentClass
            , boolean showToolbar, boolean visible) {
        if (visible){
            // adding nav drawer items to array
            navDrawerItems.add(new NavDrawerItem(navTitle, icon));
        }

        // setup the title and fragment to call, this just holds the metadata
        drawerNavItems.add(new FragmentNavItem(windowTitle, fragmentClass, null, showToolbar));
    }

    /**
     * Swaps fragments in the main content view
     */
    public void selectDrawerItem(int position){
        selectDrawerItem(position, null);
    }
    public void selectDrawerItem(int position, Bundle args) {
        // Create a new fragment and specify the planet to show based on
        // position
        mainActivity.showAllOption();

        FragmentNavItem navItem = drawerNavItems.get(position);

        // Special case for Logout
        if (navItem.getTitle().equalsIgnoreCase(getActivity().getString(R.string.menu_logout))){
            Toast.makeText(getActivity(), "LOGOUT", Toast.LENGTH_SHORT).show();
            ParseUser.logOut();
            // TODO... need to redirect back to login page
        } else {
            Bundle fragmentArgs = navItem.getFragmentArgs();
            if (args != null) {
                fragmentArgs = args;
            }
            renderFragment(navItem.getFragmentClass(), fragmentArgs);
        }

        // Highlight the selected item, update the title, and close the drawer
        // ONLY if if it is visible in drawer
        if (navItem.isVisibleInDrawer()){
            lvDrawer.setItemChecked(position, true);
            setTitle(navItem.getTitle());
            closeDrawer(lvDrawer);
        }

        if (!navItem.isShowToolbar()){
            mainActivity.hideAllOption();
        }

    }

    public void renderFragment(Class<? extends Fragment> newFragment, Bundle args){
        Fragment fragment = null;
        try {
            fragment = newFragment.newInstance();
            if (args != null) {
                fragment.setArguments(args);
            }

            if (fragment != null){
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                // default animations
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right_, R.anim.slide_out_left,
                                R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(drawerContainerRes, fragment)
//                        .addToBackStack(null)
                        .commit();
            }
        } catch (Exception e) {
        }
    }

    public boolean isDrawerOpen() {
        return isDrawerOpen(lvDrawer);
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }
    private FragmentActivity getActivity() {
        return (FragmentActivity) getContext();
    }

    private ActionBar getSupportActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private class FragmentDrawerItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectDrawerItem(position);
        }
    }

    private class FragmentNavItem {
        private Class<? extends Fragment> fragmentClass;
        private boolean showToolbar;
        private boolean visibleInDrawer;
        private String title;
        private Bundle fragmentArgs;

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass) {
            this(title, fragmentClass, null, true);
        }

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass, Bundle args) {
            this(title, fragmentClass, args, true);
        }

        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass, Bundle args, boolean showToolbar) {
            this(title, fragmentClass, args, showToolbar, true);
        }
        public FragmentNavItem(String title, Class<? extends Fragment> fragmentClass, Bundle args, boolean showToolbar, boolean visibleInDrawer) {
            this.fragmentClass = fragmentClass;
            this.fragmentArgs = args;
            this.title = title;
            this.showToolbar = showToolbar;
            this.visibleInDrawer = visibleInDrawer;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return fragmentClass;
        }

        public String getTitle() {
            return title;
        }

        public Bundle getFragmentArgs() {
            return fragmentArgs;
        }

        public boolean isShowToolbar() { return showToolbar; }
        public boolean isVisibleInDrawer() { return visibleInDrawer;}
    }
}
