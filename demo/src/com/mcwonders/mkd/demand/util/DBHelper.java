package com.mcwonders.mkd.demand.util;

import android.util.Log;

import org.xutils.DbManager;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;

/**
 * Created by ZJ on 2016/12/2.
 */
public class DBHelper {

    private static DbManager dbManager = null;

    public static DbManager getDbManager() {
        if (dbManager == null) {
            DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                    .setDbDir(new File("/mnt/sdcard/"))
                    .setDbName("maikeding.db")
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            db.getDatabase().enableWriteAheadLogging();//开启数据库多线程
                        }
                    })
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            if (newVersion > oldVersion) {
                                try {
                                    db.dropDb();
                                } catch (DbException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .setDbVersion(5)
                    .setTableCreateListener(new DbManager.TableCreateListener() {
                        @Override
                        public void onTableCreated(DbManager db, TableEntity<?> table) {
                            Log.d("zzz------", "onTableCreated<<" + table.getName());
                        }
                    });
            dbManager = x.getDb(daoConfig);
            return dbManager;
        } else {
            return dbManager;
        }
    }
}
