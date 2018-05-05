package com.richard.officenavigation.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * Master of DAO (schema version 1000): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 1000;

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(IMapDao.class);
        registerDaoClass(INodeDao.class);
        registerDaoClass(IPathDao.class);
        registerDaoClass(IBeaconDao.class);
        registerDaoClass(IRssiDao.class);
        registerDaoClass(IRssiRawDao.class);
        registerDaoClass(IClusterItemDao.class);
        registerDaoClass(IClusterDao.class);
        registerDaoClass(INodeClusterDao.class);
    }

    /**
     * Creates underlying database table using DAOs.
     */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        IMapDao.createTable(db, ifNotExists);
        INodeDao.createTable(db, ifNotExists);
        IPathDao.createTable(db, ifNotExists);
        IBeaconDao.createTable(db, ifNotExists);
        IRssiDao.createTable(db, ifNotExists);
        IRssiRawDao.createTable(db, ifNotExists);
        IClusterItemDao.createTable(db, ifNotExists);
        IClusterDao.createTable(db, ifNotExists);
        INodeClusterDao.createTable(db, ifNotExists);
    }

    /**
     * Drops underlying database table using DAOs.
     */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        IMapDao.dropTable(db, ifExists);
        INodeDao.dropTable(db, ifExists);
        IPathDao.dropTable(db, ifExists);
        IBeaconDao.dropTable(db, ifExists);
        IRssiDao.dropTable(db, ifExists);
        IRssiRawDao.dropTable(db, ifExists);
        IClusterItemDao.dropTable(db, ifExists);
        IClusterDao.dropTable(db, ifExists);
        INodeClusterDao.dropTable(db, ifExists);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
