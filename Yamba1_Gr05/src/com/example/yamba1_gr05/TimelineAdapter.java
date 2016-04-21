package com.example.yamba1_gr05;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

	static final String[] from = { DbHelper1.C_CREATED_AT, DbHelper1.C_USER,
			DbHelper1.C_TEXT };
	static final int[] to = { R.id.textCreatedAt, R.id.textUser, R.id.textText };

	@SuppressWarnings("deprecation")
	public TimelineAdapter(Context context, Cursor c) {
		super(context, R.layout.row, c, from, to);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		super.bindView(row, context, cursor);
		long timestamp = cursor.getLong(cursor
				.getColumnIndex(DbHelper1.C_CREATED_AT));
		TextView textCreatedAt = (TextView) row
				.findViewById(R.id.textCreatedAt);
		textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp));
	}

}