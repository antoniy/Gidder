package net.antoniy.gidder.beta.db;

import android.provider.BaseColumns;

public final class DBC {
	public static final class users {
		public final static String table_name 			= "users";
		public final static String column_id 			= BaseColumns._ID;
		public final static String column_fullname 		= "g_fullname";
		public final static String column_email 		= "g_email";
		public final static String column_username 		= "g_username";
		public final static String column_password 		= "g_password";
                public final static String column_publickey     = "g_publickey";
		public final static String column_active 		= "g_active";
		public final static String column_create_date	= "g_create_date";
	}

	public static final class repositories {
		public final static String table_name 			= "repositories";
		public final static String column_id 			= BaseColumns._ID;
		public final static String column_name 			= "g_name";
		public final static String column_mapping 		= "g_mapping";
		public final static String column_description 	= "g_description";
		public final static String column_active 		= "g_active";
		public final static String column_create_date	= "g_create_date";
	}

	public static final class permissions {
		public final static String table_name 			= "permissions";
		public final static String column_id 			= BaseColumns._ID;
		public final static String column_user_id		= "g_user_id";
		public final static String column_repository_id = "g_repository_id";
		public final static String column_read_only 	= "g_read_only";
//		public final static String column_allow_pull 	= "g_allowpull";
//		public final static String column_allow_push 	= "g_allowpush";
	}
}
