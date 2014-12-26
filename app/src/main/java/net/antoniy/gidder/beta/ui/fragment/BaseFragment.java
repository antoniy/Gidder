package net.antoniy.gidder.beta.ui.fragment;

import net.antoniy.gidder.beta.db.DBHelper;
import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

public abstract class BaseFragment extends SherlockFragment {
	
	private volatile DBHelper helper;
	private volatile boolean created = false;
	private volatile boolean destroyed = false;
	protected ActionMode actionMode;
	
	public void disableActionMode() {
		if(actionMode != null) {
			actionMode.finish();
			actionMode = null;
		}
	}

	public void reloadData() {
	}
	
	/**
	 * Get a helper for this action.
	 */
	public DBHelper getHelper() {
		if (helper == null) {
			if (!created) {
				throw new IllegalStateException("A call has not been made to onCreate() yet so the helper is null");
			} else if (destroyed) {
				throw new IllegalStateException("A call to onDestroy has already been made and the helper cannot be used after that point");
			} else {
				throw new IllegalStateException("Helper is null for some unknown reason");
			}
		} else {
			return helper;
		}
	}

	/**
	 * Get a connection source for this action.
	 */
	public ConnectionSource getConnectionSource() {
		return getHelper().getConnectionSource();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (helper == null) {
			helper = getHelperInternal(getActivity());
			created = true;
		}
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseHelper(helper);
		destroyed = true;
	}
	
	/**
	 * This is called internally by the class to populate the helper object instance. This should not be called directly
	 * by client code unless you know what you are doing. Use {@link #getHelper()} to get a helper instance. If you are
	 * managing your own helper creation, override this method to supply this activity with a helper instance.
	 * 
	 * <p>
	 * <b> NOTE: </b> If you override this method, you most likely will need to override the
	 * {@link #releaseHelper(OrmLiteSqliteOpenHelper)} method as well.
	 * </p>
	 */
	protected DBHelper getHelperInternal(Context context) {
		DBHelper newHelper = OpenHelperManager.getHelper(context, DBHelper.class);
		return newHelper;
	}

	/**
	 * Release the helper instance created in {@link #getHelperInternal(Context)}. You most likely will not need to call
	 * this directly since {@link #onDestroy()} does it for you.
	 * 
	 * <p>
	 * <b> NOTE: </b> If you override this method, you most likely will need to override the
	 * {@link #getHelperInternal(Context)} method as well.
	 * </p>
	 */
	protected void releaseHelper(DBHelper helper) {
		OpenHelperManager.releaseHelper();
		this.helper = null;
	}
	
}
