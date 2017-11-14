package com.example.myproduct.app.demo.rx;

import android.hardware.SensorEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myproduct.app.databinding.ListItemDateBinding;
import com.example.myproduct.lib.common.utils.log.Log;
import com.example.myproduct.lib.common.utils.rx.RxSensor;
import com.example.myproduct.lib.common_ui.ui.adapters.BaseRecyclerViewAdapter;
import com.example.myproduct.lib.common_ui.utils.view.ToastUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author lihanguang
 * @date 2017/5/13 17:32
 */
public class TestDemoListAdapter extends BaseRecyclerViewAdapter {


    private List<Object> mItemDatas;

    public TestDemoListAdapter(List<Object> initItemDatas) {
        mItemDatas = new LinkedList<>();
        if (initItemDatas != null && !initItemDatas.isEmpty()) {
            mItemDatas.addAll(initItemDatas);
        }
        /*setHasStableIds(true);*/
    }

    /*@Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }*/

    @Override
    public int getItemCount() {
        return mItemDatas == null ? 0 : mItemDatas.size();
    }

    @Override
    protected Object getItem(int position) {
        return mItemDatas == null ? null : mItemDatas.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        Object itemData = getItem(position);
        if (itemData instanceof String) {
            return 11;
        } else if (itemData instanceof Date) {
            return 22;
        }
        return super.getItemViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 11:
                return StringViewHolder.create(parent);
            case 22:
                return DateViewHolder.create(parent);
        }
        return null;
    }

    public static class StringViewHolder extends BaseRecyclerViewAdapter.RxBaseDataBindingViewHolder<String, TestDemoListAdapter, ListItemDateBinding> {

        private StringViewHolder(ListItemDateBinding binding) {
            super(binding);
        }

        static StringViewHolder create(ViewGroup parent) {
            ListItemDateBinding binding = ListItemDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new StringViewHolder(binding);
        }

        @Override
        protected void onViewRecycled(TestDemoListAdapter outerAdapter) {
            Log.i("main", "onViewRecycled() 方法被调用");
            super.onViewRecycled(outerAdapter); // 这里会发出 ViewHolderEvent.RECYCLED 事件, 故一定要调用 super.onViewRecycled(...)

            mBinding.title.setText(null);
            mBinding.title.setOnClickListener(null);
        }

        @Override
        protected void onBindViewHolder(String item, int position, TestDemoListAdapter outerAdapter) {
            super.onBindViewHolder(item, position, outerAdapter);// 这里会发出 ViewHolderEvent.BIND 事件, 故一定要调用 super.onBindViewHolder(...)

            mBinding.title.setText(String.valueOf(item));
            mBinding.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    RxSensor.onSensorChanged(itemView.getContext())
                            .doOnCancel(new Action() {
                                @Override
                                public void run() throws Exception {
                                    Log.i("main", "在 onViewRecycled() 方法被调用的时候, 在 onBindViewHolder() 里创建的跟 RxSensor.onSensorChanged(...) 相关的资源被释放");
                                }
                            })
                            .compose(StringViewHolder.this.<SensorEvent>bindUntilEvent(ViewHolderEvent.RECYCLED)) // 确定释放资源的时机
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .subscribe(new Consumer<SensorEvent>() {
                                @Override
                                public void accept(SensorEvent sensorEvent) throws Exception {
                                    final float[] floats = sensorEvent.values;
                                    Log.w("main", "RxSensor.onSensorChanged : " + Arrays.toString(floats));
                                    ToastUtils.show(mBinding.title.getContext(), Arrays.toString(floats), Toast.LENGTH_SHORT);
                                }
                            });
                }
            });
        }

        @Override
        protected void onBindViewHolder(String item, int position, List<Object> payloads, TestDemoListAdapter outerAdapter) {
            super.onBindViewHolder(item, position, payloads, outerAdapter); // 这里会发出 ViewHolderEvent.BIND 事件, 故一定要调用 super.onBindViewHolder(...)

        }
    }

    /*@BindingMethods({
            @BindingMethod(type = TextView.class,
                    attribute = "android:text",
                    method = "getTiltle"),
    })*/
    public static class DateViewHolder extends BaseRecyclerViewAdapter.BaseDataBindingViewHolder<Date, TestDemoListAdapter, ListItemDateBinding> {

        private DateViewHolder(ListItemDateBinding binding) {
            super(binding);
        }

        static DateViewHolder create(ViewGroup parent) {
            ListItemDateBinding binding = ListItemDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new DateViewHolder(binding);
        }

        @Override
        protected void onViewRecycled(TestDemoListAdapter outerAdapter) {
            mBinding.title.setText(null);
            mBinding.title.setOnClickListener(null);
        }

        @Override
        protected void onBindViewHolder(Date item, int position, TestDemoListAdapter outerAdapter) {
            mBinding.title.setText("测试 DataBinding : " + String.valueOf(item));
            mBinding.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.show(v.getContext(), mBinding.title.getText(), Toast.LENGTH_SHORT);
                }
            });
        }

        @Override
        protected void onBindViewHolder(Date item, int position, List<Object> payloads, TestDemoListAdapter outerAdapter) {

        }
    }
}
