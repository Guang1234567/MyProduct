package com.example.myproduct.app.demo.autovalue;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;


/**
 * @author lihanguang
 * @date 2017/5/2 20:03:02
 */
@AutoValue
public abstract class NullableUser implements Parcelable {

    @SerializedName("id")
    public abstract long id();

    @SerializedName("name")
    @Nullable
    public abstract String name();

    protected NullableUser() {
    }

    public static NullableUser create(int id, String name) {
        return new AutoValue_NullableUser(id, name);
    }

    public static TypeAdapter<NullableUser> typeAdapter(Gson gson) {
        return new AutoValue_NullableUser.GsonTypeAdapter(gson)
                /*.setDefaultId(-1)
                .setDefaultName("小明")*/;
    }
}
