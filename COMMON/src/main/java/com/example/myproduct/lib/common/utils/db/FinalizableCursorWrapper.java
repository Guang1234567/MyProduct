package com.example.myproduct.lib.common.utils.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.myproduct.lib.common.utils.log.Log;

/**
 * 智能关闭 Cursor 的包装类.
 *
 * 如果忘了关闭, 会帮助开发者关闭, 同时把被忘记关闭的 cursor 所在的调用堆栈打印出来.
 *
 * @author lihanguang
 * @date 2017/10/20 14:27
 */

public class FinalizableCursorWrapper extends CursorWrapper {
    private Throwable mInitWhere;

    private FinalizableCursorWrapper(Cursor cursor) {
        super(cursor);
        mInitWhere = new IllegalStateException();
    }

    public static Cursor create(Cursor cursor) {
        return new FinalizableCursorWrapper(cursor);
    }

    protected void finalize() {
        if (!super.isClosed()) {
            super.close();
            Log.w("Cursor", "Forget close the Cursor(" + String.valueOf(super.getWrappedCursor()) + ") which was initialized at :\n", mInitWhere);
        }
    }
}
