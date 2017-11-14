package com.example.myproduct.lib.common.core;

import android.content.Context;

import com.example.myproduct.lib.common.utils.log.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author lihanguang
 * @date 2017/4/8 23:16
 */

public class StartStopGroup<T> extends BaseStartStopable {

    private T mOwner;
    private List<IStartStopable> mStartStopableList;

    private StartStopGroup() {
        mStartStopableList = new LinkedList<>();
    }

    public static <T> StartStopGroup<T> create(T owner) {
        StartStopGroup group = new StartStopGroup();
        group.mOwner = owner;
        return group;
    }

    public StartStopGroup add(IStartStopable ss) {
        mStartStopableList.add(ss);
        return this;
    }

    public StartStopGroup remove(IStartStopable ss) {
        mStartStopableList.remove(ss);
        return this;
    }

    @Override
    protected void onStart(Context context) {
        //顺序初始化
        Iterator<IStartStopable> iterator = mStartStopableList.iterator();
        while (iterator.hasNext()) {
            IStartStopable next = iterator.next();
            if (next != null && next != this && !next.equals(mOwner)) {
                String logTag = next.getLogTag();
                Log.e(logTag, "--------------------------------------------------------------------------------------------");
                Log.w(logTag, "#start");
                Log.e(logTag, "--------------------------------------------------------------------------------------------");
                try {
                    next.start(context);
                } catch (Throwable tr) {
                    Log.e(logTag, "--------------------------------------------------------------------------------------------");
                    Log.w(logTag, "#start : 此环节出现错误,将不继续往下执行!\n错误原因:", tr);
                    Log.e(logTag, "--------------------------------------------------------------------------------------------");
                    throw tr; // re-throw
                }
            }
        }
    }

    @Override
    protected void onStop(Context context) {
        // 逆序释放
        ListIterator<IStartStopable> iterator = mStartStopableList.listIterator(mStartStopableList.size());
        while (iterator.hasPrevious()) {
            IStartStopable previous = iterator.previous();
            if (previous != null && previous != this && !previous.equals(mOwner)) {
                String logTag = previous.getLogTag();
                try {
                    previous.stop(context);
                } catch (Throwable tr) {
                    Log.e(logTag, "--------------------------------------------------------------------------------------------");
                    Log.w(logTag, "#stop : 此环节出现错误,但程序将继续往下执行,保证释放所有资源!\n错误原因:", tr);
                    Log.e(logTag, "--------------------------------------------------------------------------------------------");
                } finally {
                    Log.e(logTag, "--------------------------------------------------------------------------------------------");
                    Log.w(logTag, "#stop");
                    Log.e(logTag, "--------------------------------------------------------------------------------------------");
                }
            }
        }
    }
}
