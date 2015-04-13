package awsomethree.com.townkitchen.activities;

import com.astuetz.PagerSlidingTabStrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.fragments.Tutorial_Craft;
import awsomethree.com.townkitchen.fragments.Tutorial_Deliver;
import awsomethree.com.townkitchen.fragments.Tutorial_Email;
import awsomethree.com.townkitchen.fragments.Tutorial_Order;

public class TutorialActivity extends ActionBarActivity {
    private ViewPager viewPager;
    protected Button mButton;

    public class TutorialPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 4;
        private String tabTitles[] = {"1", "2", "3", "4"};
        private Tutorial_Order mOrder;
        private Tutorial_Email mEmail;
        private Tutorial_Craft mCraft;
        private Tutorial_Deliver mDeliver;

        public TutorialPagerAdapter(FragmentManager fa){
            super(fa);
            mOrder = new Tutorial_Order();
            mEmail = new Tutorial_Email();
            mCraft = new Tutorial_Craft();
            mDeliver = new Tutorial_Deliver();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0 :
                    return mOrder;
                case 1:
                    return mEmail;
                case 2:
                    return mCraft;
                default:
                    return mDeliver;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // setting viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        // set the adapter into the viewpager
        viewPager.setAdapter(new TutorialPagerAdapter(getSupportFragmentManager()));
        // find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // attach tabstrip to the viewpager
        tabStrip.setViewPager(viewPager);

        mButton = (Button) findViewById(R.id.btnTutorialDone);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
