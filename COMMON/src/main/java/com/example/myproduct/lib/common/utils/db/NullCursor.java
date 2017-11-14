package com.example.myproduct.lib.common.utils.db;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

/**
 * @author lihanguang
 * @date 2017/10/17 9:26
 */

public final class NullCursor implements Cursor {

    private boolean mIsClosed;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public int getPosition() {
        return -1;
    }

    @Override
    public boolean move(int offset) {
        return false;
    }

    @Override
    public boolean moveToPosition(int position) {
        return false;
    }

    @Override
    public boolean moveToFirst() {
        return false;
    }

    @Override
    public boolean moveToLast() {
        return false;
    }

    @Override
    public boolean moveToNext() {
        return false;
    }

    @Override
    public boolean moveToPrevious() {
        return false;
    }

    @Override
    public boolean isFirst() {
        return false;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public boolean isBeforeFirst() {
        return true;
    }

    @Override
    public boolean isAfterLast() {
        return true;
    }

    @Override
    public int getColumnIndex(String columnName) {
        return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
        return -1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return "";
    }

    @Override
    public String[] getColumnNames() {
        return new String[0];
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        return new byte[0];
    }

    @Override
    public String getString(int columnIndex) {
        return "";
    }

    @Override
    public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {

    }

    @Override
    public short getShort(int columnIndex) {
        return Short.MIN_VALUE;
    }

    @Override
    public int getInt(int columnIndex) {
        return Integer.MIN_VALUE;
    }

    @Override
    public long getLong(int columnIndex) {
        return Long.MIN_VALUE;
    }

    @Override
    public float getFloat(int columnIndex) {
        return Float.MIN_VALUE;
    }

    @Override
    public double getDouble(int columnIndex) {
        return Double.MIN_VALUE;
    }

    @Override
    public int getType(int columnIndex) {
        return Cursor.FIELD_TYPE_NULL;
    }

    @Override
    public boolean isNull(int columnIndex) {
        return true;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public boolean requery() {
        return false;
    }

    @Override
    public void close() {
        mIsClosed = true;
    }

    @Override
    public boolean isClosed() {
        return mIsClosed;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) {

    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {

    }

    @Override
    public Uri getNotificationUri() {
        return null;
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    @Override
    public void setExtras(Bundle extras) {

    }

    @Override
    public Bundle getExtras() {
        return null;
    }

    @Override
    public Bundle respond(Bundle extras) {
        return null;
    }
}
