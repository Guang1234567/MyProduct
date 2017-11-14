/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.myproduct.app.demo.db.model.tables;

import android.database.Cursor;
import android.os.Parcelable;

import com.example.myproduct.lib.common.utils.db.BaseEntity;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import io.reactivex.functions.Function;

@AutoValue
public abstract class TodoItem extends BaseEntity implements Parcelable {
    public static final String TABLE = "todo_item";

    public static final String LIST_ID = "todo_list_id";
    public static final String DESCRIPTION = "description";
    public static final String COMPLETE = "complete";

    public static final String CREATE_ITEM = ""
            + "CREATE TABLE " + TABLE + "("
            + ROW_ID + " INTEGER NOT NULL PRIMARY KEY,"
            + ROW_COUNT + " INTEGER NOT NULL DEFAULT 0,"
            + LIST_ID + " INTEGER NOT NULL REFERENCES " + TodoList.TABLE + "(" + TodoList.ROW_ID + "),"
            + DESCRIPTION + " TEXT NOT NULL,"
            + COMPLETE + " INTEGER NOT NULL DEFAULT 0"
            + ")";

    public static final String CREATE_ITEM_LIST_ID_INDEX =
            "CREATE INDEX item_list_id ON " + TABLE + " (" + LIST_ID + ")";

    public static String fullColumnName(String shortColumnName) {
        return TABLE + '.' + String.valueOf(shortColumnName);
    }

    @ColumnName(ROW_ID)
    public abstract long rowId();

    @ColumnName(ROW_COUNT)
    public abstract long rowCount();

    @ColumnName(LIST_ID)
    public abstract long listId();

    @ColumnName(DESCRIPTION)
    public abstract String description();

    @ColumnName(COMPLETE)
    public abstract boolean complete();

    // Optional: if your project includes RxJava 2 the extension will generate a Function<Cursor, User>
    public static Function<Cursor, TodoItem> mapper() {
        return AutoValue_TodoItem.MAPPER_FUNCTION;
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder rowId(long id);

        public abstract Builder rowCount(long rowCount);

        public abstract Builder listId(long listId);

        public abstract Builder description(String description);

        public abstract Builder complete(boolean complete);

        public abstract TodoItem build();
    }

    public static Builder builder() {
        return new AutoValue_TodoItem.Builder().rowId(-1L).rowCount(0L);
    }
}
