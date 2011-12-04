package net.antoniy.gidder.db;

import android.provider.BaseColumns;

public final class DBC {
	public static final class users {
		public final static String table_name 		= "users";
		public final static String column_id 		= BaseColumns._ID;
		public final static String column_fullname 	= "g_fullname";
		public final static String column_email 	= "g_email";
		public final static String column_username 	= "g_username";
		public final static String column_password 	= "g_password";
	}
}
