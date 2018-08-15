package com.rultech.contactdemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jiyyo on 15/08/18.
 */
public class ContactsAdaptor extends ResourceCursorAdapter {
    private final String[] mFromColumns;

    public ContactsAdaptor(Context context, int layout, Cursor mCursor, String[] mFromColumns, int[] toIds, int flags) {
        super(context, layout, mCursor, flags);
        this.mFromColumns = mFromColumns;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView txtName = view.findViewById(android.R.id.text1);
        final TextView txtNumber = view.findViewById(R.id.txtnumber);
        ImageView imageView = view.findViewById(R.id.imageContact);
        final String contactId =
                cursor.getString(cursor.getColumnIndex(mFromColumns[0]));
        String mContactPhotoUri = cursor.getString(cursor.getColumnIndex(mFromColumns[1]));
        String mContactName = cursor.getString(cursor.getColumnIndex(mFromColumns[2]));
        if (mContactName != null && mContactName.length() > 0) {
            txtName.setText(mContactName);
        }
        if (mContactPhotoUri != null && mContactPhotoUri.length() > 0) {
            imageView.setImageURI(Uri.parse(mContactPhotoUri));
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> arrNumbers = getContactNumbers(contactId);
                if (arrNumbers != null && arrNumbers.size() > 0) {
                    String mStrNumbers = "";
                    for (String mNumber : arrNumbers) {
                        mStrNumbers = mNumber + "\n" + mStrNumbers;
                    }
                    final String finalMStrNumbers = mStrNumbers;
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtNumber.setText(finalMStrNumbers);
                        }
                    });

                }
            }
        }).start();
//        String item1 = cursor.getString(cursor.getColumnIndex(mFromColumns[0]));


    }

    private ArrayList<String> getContactNumbers(String mContactId) {
        ArrayList<String> arrNumbers = new ArrayList<>();
        ContentResolver cr = mContext.getContentResolver();
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + mContactId, null, null);
        while (phones.moveToNext()) {
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (number != null && number.length() > 0 && !arrNumbers.contains(number)) {
                arrNumbers.add(number);
            }
//            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//            switch (type) {
//                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                    // do something with the Home number here...
//                    break;
//                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                    // do something with the Mobile number here...
//                    break;
//                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                    // do something with the Work number here...
//                    break;
//            }
        }
        phones.close();
        return arrNumbers;
    }
}
