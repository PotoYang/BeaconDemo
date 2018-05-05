package com.richard.officenavigation.constants;

import android.os.Environment;

import java.io.File;

public class C {
    public static final boolean DEBUG = true;
    public static final String PREFERENCES_MANAGE = "office_navi_config";

    public static final String APP_FOLDER = Environment
            .getExternalStorageDirectory().getPath()
            + File.separator
            + "OfficeNavi";
    public static final String DB_NAME = "office-navi-db";

    public static final class main {
        public static final int HOME_PAGE_INDEX = 0;
        public static final int SEARCH_PAGE_INDEX = 1;
        public static final int MAP_PAGE_INDEX = 2;
        public static final int MANAGE_PAGE_INDEX = 3;
    }

    public static final class manage {
        public static final String KEY_BOOTSTART = "key_boot_start";
        public static final String KEY_RECV_MSG = "key_recv_smsg";
        public static final String KEY_VIBRATION = "key_vibration";
        public static final boolean DEFVAL_BOOTSTART = true;
        public static final boolean DEFVAL_RECV_MSG = true;
        public static final boolean DEFVAL_VIBRATION = true;
    }

    public static final class map {

        public static final long DEFAULT_MAP_ID = -1L;
        public static final String DEFAULT_MAP_SRC = "tiles/plans";
        public static final String DEFAULT_MAP_NAME = "default";
        public static final long DEFAULT_MAP_WIDTH = 2292L;
        public static final long DEFAULT_MAP_HEIGHT = 1310L;
        public static final double DEFAULT_MAP_SCALE = 5.0;

        public static final long DEFAULT_SELECTED_MAP_ID = 0L;

        public static final String DIR = "Maps";
        public static final String EXTRA_SELECTED_MAP_PATH = "extra_map_path";
        public static final String EXTRA_MAP_PX_WIDTH = "extra_map_width_px";
        public static final String EXTRA_MAP_PX_HEIGHT = "extra_map_height_px";
        public static final String EXTRA_MAP_MM_PX_SCALE = "extra_map_mm_px_scale";

        public static final String EXTRA_SELECTED_MAP_ID = "extra_selected_mapid";
        public static final String EXTRA_SELECTED_NODE_ID = "extra_selected_nodeid";

        public static final String KEY_CURRENT_MAPID = "current_mapid";

        public static final String KEY_ACTION = "map_action";
        public static final int ACTION_NAVIGATION = 0x01;
        public static final int ACTION_LOCATE = 0x02;
        public static final int ACTION_SEARCH = 0x04;
        public static final String KEY_NAVI_ARG_NAME = "navi_arg_name";

        public static final int ORIENT_EAST = 0x0;
        public static final int ORIENT_SORTH = 0x1;
        public static final int ORIENT_WEST = 0x2;
        public static final int ORIENT_NORTH = 0x3;
        public static final int ORIENT_SORTHEAST = 0x4;
        public static final int ORIENT_SORTHWEST = 0x5;
        public static final int ORIENT_NORTHEAST = 0x6;
        public static final int ORIENT_NORTHWEST = 0x7;

        public static final int DEFAULT_TRAINING_TIME = 180;

        public static final int CLUSTER_Q_VAL = 3;

        public static final int DISTRIBUTION_K_VAL = 4;
    }
}
