package com.example.myproduct.lib.common.utils.db;

import android.content.ContentValues;
import android.provider.BaseColumns;

/**
 * 所有数据实体的基类
 *
 * @author lihanguang
 * @date 2016/2/23 17:47
 */
public abstract class BaseEntity {

    public final static String ROW_ID = BaseColumns._ID;
    public final static String ROW_COUNT = BaseColumns._COUNT;

    // Optional: When you include an abstract method that returns ContentValues and doesn't have
    // any parameters the extension will implement it for you
    protected abstract ContentValues toCompleteContentValues();

    public final ContentValues toContentValues() {
        ContentValues cvs = toCompleteContentValues();
        cvs.remove(ROW_ID);
        //cvs.remove(ROW_COUNT);
        return cvs;
    }
}
