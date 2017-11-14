package com.example.myproduct.app.demo.db.model.pojo;

import android.database.Cursor;
import android.os.Parcelable;

import com.example.myproduct.app.demo.db.model.tables.TodoItem;
import com.example.myproduct.app.demo.db.model.tables.TodoList;
import com.example.myproduct.lib.common.utils.db.DbUtils;
import com.google.auto.value.AutoValue;
import com.joanzapata.utils.Strings;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.functions.Function;

/**
 * @author lihanguang
 * @date 2017/10/19 15:57
 */

@AutoValue
public abstract class TodoListItem implements Parcelable, Comparable<TodoListItem> {

    public static final String ITEM_COUNT = "item_count";

    public abstract TodoList todoList();

    public abstract List<TodoItem> todoItems();

    public abstract int itemCount();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder todoList(TodoList todoList);

        public abstract Builder todoItems(List<TodoItem> todoItems);

        public abstract Builder itemCount(int itemCount);

        public abstract TodoListItem build();
    }

    public static Builder builder() {
        return new AutoValue_TodoListItem.Builder();
    }

    public static final String QUERY_ALL_WITH_ITEM_COUNT_WITHOUT_ITEMS = Strings.format(
            "SELECT" +
                    " {TodoList.ROW_ID}," +
                    " {TodoList.NAME}," +
                    " {TodoList.ARCHIVED}," +
                    " COUNT({TodoItem.ROW_ID})" +
                    " as {ITEM_COUNT}" +
                    " FROM {TodoList.TABLE}" +
                    " LEFT OUTER JOIN {TodoItem.TABLE} " +
                    "ON {TodoList.ROW_ID} = {TodoItem.LIST_ID} " +
                    "GROUP BY {TodoList.ROW_ID}")
            .with("TodoList.ROW_ID", TodoList.fullColumnName(TodoList.ROW_ID))
            .with("TodoList.NAME", TodoList.fullColumnName(TodoList.NAME))
            .with("TodoList.ARCHIVED", TodoList.fullColumnName(TodoList.ARCHIVED))
            .with("TodoItem.ROW_ID", TodoItem.fullColumnName(TodoItem.ROW_ID))
            .with("ITEM_COUNT", ITEM_COUNT)
            .with("TodoList.TABLE", TodoList.TABLE)
            .with("TodoItem.TABLE", TodoItem.TABLE)
            .with("TodoItem.LIST_ID", TodoItem.fullColumnName(TodoItem.LIST_ID))
            .build();

    public static Function<Cursor, TodoListItem> MAPPER_OF_QUERY_ALL_WITH_ITEM_COUNT_WITHOUT_ITEMS = new Function<Cursor, TodoListItem>() {
        @Override
        public TodoListItem apply(Cursor cursor) {
            long id = DbUtils.getLong(cursor, TodoList.ROW_ID);
            String name = DbUtils.getString(cursor, TodoList.NAME);
            boolean archived = DbUtils.getBoolean(cursor, TodoList.ARCHIVED);
            int itemCount = DbUtils.getInt(cursor, ITEM_COUNT);
            return TodoListItem.builder()
                    .todoList(TodoList.builder().rowId(id).name(name).archived(archived).build())
                    .todoItems(new LinkedList<TodoItem>())
                    .itemCount(itemCount)
                    .build();
        }
    };

    @Override
    public int compareTo(TodoListItem o) {
        if (o == null) {
            return 1;
        }
        return Long.compare(Math.max(this.itemCount(), todoItems().size()), Math.max(this.itemCount(), todoItems().size()));
    }
}
