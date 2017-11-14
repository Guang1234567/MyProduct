package com.example.myproduct.app.demo.autovalue;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcelable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.ryanharter.auto.value.parcel.ParcelAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Function;
/**
 * @author lihanguang
 * @date 2017/5/2 19:42:52
 */
@AutoValue
public abstract class User implements Parcelable {

    @SerializedName("id")
    public abstract long id();

    @SerializedName("name")
    public abstract String name();

    @SerializedName("timestamp")
    @ParcelAdapter(DateParcelTypeAdapter.class)
    @ColumnName("last_modified_timestamp")
    @ColumnAdapter(DateCursorTypeAdapter.class)
    public abstract Date date();

    @SerializedName("email")
    @ColumnName("email_address")
    abstract String email();

    /**
     * auto-value 不处理的字段
     */
    private Map mExtras;

    protected User() {
        mExtras = new HashMap(7);
    }

    public static TypeAdapter<User> typeAdapter(Gson gson) {
        return new $AutoValue_User.GsonTypeAdapter(gson)
                /*.setDefaultId(-1)
                .setDefaultName("小明")
                .setDefaultDate(new Date(0))
                .setDefaultEmail("default@163.com")*/;
    }

    public static Builder builder() {
        return new $$$$AutoValue_User.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(long id);

        public abstract Builder name(String name);

        public abstract Builder date(Date date);

        public abstract Builder email(String email);

        public abstract User build();
    }

    public static User create(long id, String name, Date date, String email) {
        return builder()
                .id(id)
                .name(name)
                .date(date)
                .email(email)
                .build();
    }

    public static User create(User src) {
        return builder()
                .id(src.id())
                .name(src.name())
                .date(src.date())
                .email(src.email())
                .build();
    }

    public static User create(Cursor cursor) {
        return AutoValue_User.createFromCursor(cursor);
    }

    // Optional: if your project includes RxJava the extension will generate a Func1<Cursor, User>
    /*public static Func1<Cursor, User> mapper() {
        return AutoValue_User.MAPPER;
    }*/

    // Optional: if your project includes RxJava 2 the extension will generate a Function<Cursor, User>
    public static Function<Cursor, User> mapper() {
        return AutoValue_User.MAPPER_FUNCTION;
    }

    // Optional: When you include an abstract method that returns ContentValues and doesn't have
    // any parameters the extension will implement it for you
    abstract ContentValues toContentValues();
}