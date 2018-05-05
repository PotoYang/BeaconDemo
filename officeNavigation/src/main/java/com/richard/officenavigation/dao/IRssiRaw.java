package com.richard.officenavigation.dao;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END

/**
 * Entity mapped to table IRSSI_RAW.
 */
public class IRssiRaw {

    private Long id;
    private long nodeId;
    private int minor;
    private int orientation;
    private int xVal;
    private int yVal;
    private long mapId;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public IRssiRaw() {
    }

    public IRssiRaw(Long id) {
        this.id = id;
    }

    public IRssiRaw(Long id, long nodeId, int minor, int orientation, int xVal, int yVal, long mapId) {
        this.id = id;
        this.nodeId = nodeId;
        this.minor = minor;
        this.orientation = orientation;
        this.xVal = xVal;
        this.yVal = yVal;
        this.mapId = mapId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getNodeId() {
        return nodeId;
    }

    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public int getXVal() {
        return xVal;
    }

    public void setXVal(int xVal) {
        this.xVal = xVal;
    }

    public int getYVal() {
        return yVal;
    }

    public void setYVal(int yVal) {
        this.yVal = yVal;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
