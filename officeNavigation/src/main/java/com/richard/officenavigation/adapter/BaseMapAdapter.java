package com.richard.officenavigation.adapter;

import android.graphics.PointF;
import android.view.View;

import java.util.List;

public interface BaseMapAdapter {
    int ASSETS_MAP = 0;
    int FS_MAP = 1;

    String getSrc();

    Long getHeight();

    Long getWidth();

    Long getId();

    double getScale();

    int getType();

    List<View> getViews();

    PointF getNodePos(Object node);
}
