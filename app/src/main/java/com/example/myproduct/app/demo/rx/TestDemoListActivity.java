package com.example.myproduct.app.demo.rx;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.myproduct.app.R;
import com.example.myproduct.app.databinding.ActivityTestDemoListBinding;
import com.example.myproduct.app.ui.AppBaseActivity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TestDemoListActivity extends AppBaseActivity {

    public final ObservableField<String> mToolbarTitle = new ObservableField<>();

    private ActivityTestDemoListBinding mBinding;

    public final static void startActivity(Context from) {
        from.startActivity(new Intent(from, TestDemoListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test_demo_list);
        mBinding.setActivity(this);

        setUpToolBar(mBinding.toolBar);
        setUpList(mBinding.list);
    }

    private void setUpToolBar(Toolbar toolBar) {
        mToolbarTitle.set("测试例子");
    }

    private void setUpList(RecyclerView list) {
        list.setLayoutManager(new LinearLayoutManager(list.getContext()));
        list.setHasFixedSize(false);
        List<Object> initItemDatas = new LinkedList<>();
        initItemDatas.add(new Date());
        initItemDatas.add(new String("RxSensor.onSensorChanged"));
        list.setAdapter(new TestDemoListAdapter(initItemDatas));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBinding.list.setAdapter(null);
    }
}
