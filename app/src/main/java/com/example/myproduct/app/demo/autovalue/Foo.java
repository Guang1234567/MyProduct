package com.example.myproduct.app.demo.autovalue;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * @author lihanguang
 * @date 2017/5/4 20:51:57
 */

@AutoValue
public abstract class Foo<A, B, C> {

    abstract A data();

    abstract List<B> dataList();

    abstract Map<String, List<C>> dataMap();

    public static <A, B, C> TypeAdapter<Foo<A, B, C>> typeAdapter(Gson gson,
                                                                  TypeToken<? extends Foo> typeToken) {
        return new AutoValue_Foo.GsonTypeAdapter(gson, typeToken);
    }
}