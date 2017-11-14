package com.example.myproduct.lib.common_ui.utils.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.example.myproduct.lib.common.utils.log.Log;

/**
 * 与视图相关的工具类
 *
 * @author lihanguang
 * @date 2016/02/23
 */
public class ViewUtils {
    private static final String TAG = "ViewUtils";

    /**
     * Utility method to make getting a View via findViewById() more safe & simple.
     *
     * @param activity The current Context or Activity that this method is called from
     * @param id       R.id value for view
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(Activity activity, int id) {
        T view = null;
        View genericView = activity.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }

    /**
     * Utility method to make getting a View via findViewById() more safe & simple.
     *
     * @param dialog The current Context or Dialog that this method is called from
     * @param id     R.id value for view
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    public static <T extends View> T findViewById(Dialog dialog, int id) {
        T view = null;
        View genericView = dialog.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }

    /**
     * Utility method to make getting a View via findViewById() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & Loggers invalid casts
     *
     * @param parentView Parent View containing the view we are trying to get
     * @param id         R.id value for view
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findViewById(View parentView, int id) {
        T view = null;
        View genericView = parentView.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }

    /**
     * Utility method to make getting a View via findViewById() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & Loggers invalid casts
     *
     * @param window Window containing the view we are trying to get
     * @param id     R.id value for view
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    public static <T extends View> T findViewById(Window window, int id) {
        T view = null;
        View genericView = window.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return view;
    }

    /**
     * Get text as String from EditView.
     * <b>Note:</b> returns "" for null EditText, not a NullPointerException
     *
     * @param view EditView to get text from
     * @return the text
     */
    public static String getText(TextView view) {
        String text = "";
        if (view != null) {
            text = view.getText().toString();
        }
        return text;
    }

    /**
     * Get text as String from EditView.
     * <b>Note:</b> returns "" for null EditText, not a NullPointerException
     *
     * @param context The current Context or Activity that this method is called from
     * @param id      Id for the TextView/EditView to get text from
     * @return the text
     */
    public static String getText(Activity context, int id) {
        TextView view = findViewById(context, id);

        String text = "";
        if (view != null) {
            text = view.getText().toString();
        }
        return text;
    }

    /**
     * Append given text String to the provided view (one of TextView or EditText).
     *
     * @param view     View to update
     * @param toAppend String text
     */
    public static void appendText(TextView view, String toAppend) {
        String currentText = getText(view);
        view.setText(currentText + toAppend);
    }

    /**
     * Method used to set text for a TextView
     *
     * @param context The current Context or Activity that this method is called from
     * @param field   R.id.xxxx value for the text field.
     * @param text    Text to place in the text field.
     */
    public static void setText(Activity context, int field, String text) {
        View view = context.findViewById(field);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
    }

    /**
     * Method used to set text for a TextView
     *
     * @param parentView The View used to call findViewId() on
     * @param field      R.id.xxxx value for the text field.
     * @param text       Text to place in the text field.
     */
    public static void setText(View parentView, int field, String text) {
        View view = parentView.findViewById(field);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
    }

    /**
     * Sets visibility of the given view to <code>View.GONE</code>.
     *
     * @param context The current Context or Activity that this method is called from
     * @param id      R.id.xxxx value for the view to hide"expected textView to throw a ClassCastException" + textView
     */
    public static void hideView(Activity context, int id) {
        if (context != null) {
            View view = context.findViewById(id);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Sets visibility of the given view to <code>View.VISIBLE</code>.
     *
     * @param context The current Context or Activity that this method is called from
     * @param id      R.id.xxxx value for the view to show
     */
    public static void showView(Activity context, int id) {
        if (context != null) {
            View view = context.findViewById(id);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            } else {
                Log.e(TAG, "View does not exist.  Could not hide it.");
            }
        }
    }

    /**
     * Utility method to make getting a View via inflate() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & Loggers invalid casts
     *
     * @param context               context object
     * @param layoutResId           R.layout value for view
     * @param root                  parent view of layoutResId
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    public static <T extends View> T inflate(Context context, @LayoutRes int layoutResId, ViewGroup root) {
        return (T) LayoutInflater.from(context).inflate(layoutResId, root);
    }

    /**
     * Utility method to make getting a View via inflate() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & Loggers invalid casts
     *
     * @param layoutInflater        LayoutInflater object
     * @param layoutResId           R.layout value for view
     * @param root                  parent view of layoutResId
     * @return View object, cast to appropriate type based on expected return value.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    public static <T extends View> T inflate(LayoutInflater layoutInflater, @LayoutRes int layoutResId, ViewGroup root) {
        return (T) layoutInflater.inflate(layoutResId, root);
    }

    /**
     * Utility method to make getting a View via inflate() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & Loggers invalid casts
     *
     * @param context               context object
     * @param layoutResId           R.layout value for view
     * @param root                  parent view of layoutResId
     * @param attachToRoot          whether add layoutResId to root.
     * @return View object, cast to appropriate type based on expected return value. If attachToRoot is true, return root.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    public static <T extends View> T inflate(Context context, @LayoutRes int layoutResId, ViewGroup root, boolean attachToRoot) {
        return (T) LayoutInflater.from(context).inflate(layoutResId, root, attachToRoot);
    }

    /**
     * Utility method to make getting a View via inflate() more safe & simple.
     * <p/>
     * - Casts view to appropriate type based on expected return value
     * - Handles & Loggers invalid casts
     *
     * @param layoutInflater LayoutInflater object
     * @param layoutResId    R.layout value for view
     * @param root           parent view of layoutResId
     * @param attachToRoot   whether add layoutResId to root.
     * @return View object, cast to appropriate type based on expected return value. If attachToRoot is true, return root.
     * @throws ClassCastException if cast to the expected type breaks.
     */
    public static <T extends View> T inflate(LayoutInflater layoutInflater, @LayoutRes int layoutResId, ViewGroup root, boolean attachToRoot) {
        return (T) layoutInflater.inflate(layoutResId, root, attachToRoot);
    }

    /**
     * Returns this view's tag.
     *
     * @return the Object stored in this view as a tag, or {@code null} if not
     * set
     * @see #setTag(View, Object)
     * @see #getTag(View, int)
     */
    public static <T> T getTag(final View view) {
        return (T) view.getTag();
    }

    /**
     * Sets the tag associated with this view. A tag can be used to mark
     * a view in its hierarchy and does not have to be unique within the
     * hierarchy. Tags can also be used to store data within a view without
     * resorting to another data structure.
     *
     * @param tag an Object to tag the view with
     * @see #getTag(View)
     * @see #setTag(View, int, Object)
     */
    public static void setTag(final View view, final Object tag) {
        view.setTag(tag);
    }

    /**
     * Returns the tag associated with this view and the specified key.
     *
     * @param key The key identifying the tag
     * @return the Object stored in this view as a tag, or {@code null} if not
     * set
     * @see #setTag(View, int, Object)
     * @see #getTag(View)
     */
    public static <T> T getTag(final View view, @IdRes int key) {
        return (T) view.getTag(key);
    }

    /**
     * Sets a tag associated with this view and a key. A tag can be used
     * to mark a view in its hierarchy and does not have to be unique within
     * the hierarchy. Tags can also be used to store data within a view
     * without resorting to another data structure.
     * <p>
     * The specified key should be an id declared in the resources of the
     * application to ensure it is unique (see the <a
     * href={@docRoot}guide/topics/resources/more-resources.html#Id">ID resource type</a>).
     * Keys identified as belonging to
     * the Android framework or not associated with any package will cause
     * an {@link IllegalArgumentException} to be thrown.
     *
     * @param key The key identifying the tag
     * @param tag An Object to tag the view with
     * @throws IllegalArgumentException If they specified key is not valid
     * @see #setTag(View, Object)
     * @see #getTag(View, int)
     */
    public static void setTag(final View view, @IdRes int key, final Object tag) {
        view.setTag(key, tag);
    }
}
