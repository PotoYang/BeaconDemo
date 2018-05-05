package com.richard.officenavigation;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;

import com.richard.officenavigation.adapter.BottomTabAdapter;
import com.richard.officenavigation.constants.C;
import com.richard.officenavigation.fragment.HomepageFragment;
import com.richard.officenavigation.fragment.ManageFragment;
import com.richard.officenavigation.fragment.MapFragment;
import com.richard.officenavigation.fragment.TabPagerFragment;
import com.viewpagerindicator.CustomIconPosTabPageIndicator;

import org.altbeacon.beacon.BeaconManager;

import java.io.File;

public class MainActivity extends BaseActivity implements OnPageChangeListener {

    private static final String[] TITLES = new String[]{"首页", "寻找", "地图",
            "管理"};
    private static final int[] ICONS = new int[]{
            R.drawable.tab_bottom_homepage, R.drawable.tab_bottom_search,
            R.drawable.tab_bottom_map, R.drawable.tab_bottom_manage,};
    private static final Fragment[] FRAGMENTS = new Fragment[]{
            new HomepageFragment(), new HomepageFragment(), new MapFragment(),
            new ManageFragment(),};

    private CustomIconPosTabPageIndicator mTabPageIndicator;
    private ViewPager mVpMain;
    private FragmentPagerAdapter mFPAdapter;

    @Override
    protected void findViews() {
        setContentView(R.layout.activity_main);
        verifyBluetooth();
        mVpMain = (ViewPager) findViewById(R.id.pager);
        mTabPageIndicator = (CustomIconPosTabPageIndicator) findViewById(R.id.indicator);
    }

    @Override
    protected void setupViews() {
        mFPAdapter = new BottomTabAdapter(getSupportFragmentManager(), TITLES,
                ICONS, FRAGMENTS);
        mVpMain.setAdapter(mFPAdapter);

        mTabPageIndicator
                .setTabIconLocation(CustomIconPosTabPageIndicator.LOCATION_UP);
        mTabPageIndicator.setViewPager(mVpMain);
        mTabPageIndicator.setOnPageChangeListener(this);
    }

    @Override
    protected void initDatas(Bundle savedInstanceState) {
        ensureAppDirExists();
    }

    private void verifyBluetooth() {
        try {
            if (!BeaconManager.getInstanceForApplication(this)
                    .checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        this);
                builder.setTitle(R.string.title_bluetooth_not_enable);
                builder.setMessage(R.string.msg_bluetooth_should_enable);
                builder.setPositiveButton(R.string.confirm, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(R.string.confirm, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();
        }
    }

    private void ensureAppDirExists() {
        File appDir = new File(C.APP_FOLDER);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File mapDir = new File(appDir.getAbsolutePath() + File.separator
                + C.map.DIR);
        if (!mapDir.exists()) {
            mapDir.mkdir();
        }
    }

    private void setCurrentPage(int item) {
        mTabPageIndicator.setCurrentItem(item);
    }

    public void jump(int page, Bundle data) {
        TabPagerFragment pager = (MapFragment) mFPAdapter.getItem(page);
        switch (page) {
            case C.main.MAP_PAGE_INDEX:
                setCurrentPage(page);
                pager.onJumpTo(data);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mVpMain.getCurrentItem() != C.main.HOME_PAGE_INDEX) {
                    mTabPageIndicator.setCurrentItem(C.main.HOME_PAGE_INDEX);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageSelected(int position) {
        ActionBar actionBar = getActionBar();
        if (position == C.main.HOME_PAGE_INDEX) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

    }
}
