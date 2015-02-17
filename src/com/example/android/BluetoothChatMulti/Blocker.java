package com.example.android.BluetoothChatMulti;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.TelephonyManager;

public class Blocker extends BroadcastReceiver {
	private static final int MODE_WORLD_READABLE = 1;
	private ITelephony telephonyService;
	private SharedPreferences myPrefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		myPrefs = context.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
		String blockingMode = myPrefs.getString("mode", "not retrieved");
		System.out.println(blockingMode);

		if (blockingMode.equals("all")) {
			Bundle bb = intent.getExtras();
			String state = bb.getString(TelephonyManager.EXTRA_STATE);
			if ((state != null)
					&& (state
							.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING))) {
				blockCall(context, bb);

			}
		}
	}

	public void blockCall(Context c, Bundle b) {

		TelephonyManager telephony = (TelephonyManager) c
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Class cls = Class.forName(telephony.getClass().getName());
			Method m = cls.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(telephony);
			// telephonyService.silenceRinger();
			telephonyService.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getContactDisplayNameByNumber(String number, Context c) {

		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));
		String name = "?";
		String data = null;
		ContentResolver contentResolver = c.getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] {
				BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME },
				null, null, null);

		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				data = contactLookup.getString(contactLookup
						.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
				// String contactId =
				// contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();

			}
		}

		return data;
	}
}
