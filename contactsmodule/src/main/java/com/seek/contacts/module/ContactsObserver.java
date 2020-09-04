package com.seek.contacts.module;

import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * Created by chunyang on 2018/6/8.
 */

public class ContactsObserver extends ContentObserver {

    public static final int WHAT_CONTACTS_CHANGE = 1;
    private Handler mHandler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public ContactsObserver(Handler handler) {
        super(handler);
        this.mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d("ContactsObserver", "onChange ");
        mHandler.sendEmptyMessage(WHAT_CONTACTS_CHANGE);
    }
}
