package awsomethree.com.townkitchen.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.fragments.MenuFragment;
import awsomethree.com.townkitchen.fragments.OrderFeedbackFragment;
import awsomethree.com.townkitchen.fragments.OrderHistoryFragment;
import awsomethree.com.townkitchen.fragments.ShoppingCartFragment;
import awsomethree.com.townkitchen.fragments.TrackOrderFragment;
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;
import awsomethree.com.townkitchen.layouts.TKNavigationDrawer;


/**
 * Abstract class which holds:
 *  - intializing the navigation drawer
 *  - replace action bar with toolbar
  */
public class MainActivity extends
        ActionBarActivity implements fragmentNavigationInterface{

    public static final String APP = "TownKitchen";

    protected TKNavigationDrawer tkDrawer;
    protected Toolbar toolbar;

    // ORDER OF THE MENU ITEM
    public static final int MENU_DRAWER_POSITION = 0;
    public static final int SHOPPINGCART_DRAWER_POSITION = 1;
    public static final int ORDERHISTORY_DRAWER_POSITION = 2;
    public static final int TRACKMYODER_DRAWER_POSITION = 3;

    // add to cart, calendar in toolbar
    private Menu menuToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.template_main_activity);

        // Setting up toolbar
        toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);

        settingUpDrawerView();

        // set default view
        if (savedInstanceState == null){
            setMainFragment();
        }
    }

    private void settingUpDrawerView() {
        tkDrawer = (TKNavigationDrawer) findViewById(R.id.tkdrawer_layout);
        // setup drawer view
        tkDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar, R.id.flContent, this);
        // adding navigation drawer items
        tkDrawer.addNavItem("Menu", R.mipmap.ic_launcher, "Menu", MenuFragment.class);
        tkDrawer.addNavItem("Shopping Cart", R.mipmap.ic_launcher, "Shopping Cart",
                ShoppingCartFragment.class);
        tkDrawer.addNavItem("Order History", R.mipmap.ic_launcher, "My Order", OrderHistoryFragment.class);
        // Below pages, will not showing the other navigation (calendar and add shopping cart)
        tkDrawer.addNavItem("Track My Order", R.mipmap.ic_launcher, "Track My Order",
                TrackOrderFragment.class, false);
        tkDrawer.addNavItem("Give Feedbacks", R.mipmap.ic_launcher, "Feedback",
                OrderFeedbackFragment.class, false);
    }

    protected void setMainFragment(){
        tkDrawer.selectDrawerItem(0);
    }

    protected void setTitle(String title){
        tkDrawer.setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menuToolbar = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_abstract_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (tkDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }
        // handle the top toolbar menu
        int id = item.getItemId();

        switch (id){
            case R.id.action_shoppingcart :
                tkDrawer.selectDrawerItem(SHOPPINGCART_DRAWER_POSITION);
                break;
            case R.id.action_calendar :
                tkDrawer.selectDrawerItem(MENU_DRAWER_POSITION);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (tkDrawer.isDrawerOpen()){

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        tkDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        tkDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    /**
     * to hide all menu item (toolbar)
     */
    public void hideAllOption(){
        if (menuToolbar != null){
            for (int i=0; i < menuToolbar.size(); i++){
                menuToolbar.getItem(i).setVisible(false);
            }
        }
    }

    public void showAllOption(){
        if (menuToolbar != null){
            for (int i=0; i < menuToolbar.size(); i++){
                menuToolbar.getItem(i).setVisible(true);
            }
        }
    }

    /**
     * to show certain menu item (toolbar)
     * @param id
     */
    public void showMenuOption(int id){
        if (menuToolbar != null){
            MenuItem menuItem = menuToolbar.findItem(id);
            if (menuItem != null){
                menuItem.setVisible(true);
            }
        }
    }

    /**
     * to show certain menu item (toolbar)
     * @param id
     */
    public void hideMenuOption(int id){
        if (menuToolbar != null){
            MenuItem menuItem = menuToolbar.findItem(id);
            if (menuItem != null){
                menuItem.setVisible(false);
            }
        }
    }

    @Override
    public void changeFragmentTo(int fragmentMenuOption) {
        tkDrawer.selectDrawerItem(fragmentMenuOption);
    }
}
