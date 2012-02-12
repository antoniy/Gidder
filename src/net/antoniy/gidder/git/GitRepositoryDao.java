package net.antoniy.gidder.git;

import java.sql.SQLException;

import net.antoniy.gidder.db.DBHelper;
import net.antoniy.gidder.db.entity.Repository;
import net.antoniy.gidder.ui.util.PrefsConstants;

import org.eclipse.jgit.errors.RepositoryNotFoundException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class GitRepositoryDao {
	private final static String TAG = GitRepositoryDao.class.getSimpleName();
	
	private GitRepositoryManager repositoryManager;
	private DBHelper dbHelper;
	
	public GitRepositoryDao(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String gitRepoDir = prefs.getString(PrefsConstants.GIT_REPOSITORIES_DIR.getKey(), PrefsConstants.GIT_REPOSITORIES_DIR.getDefaultValue());
		
		repositoryManager = new SDCardRepositoryManager(gitRepoDir);
		dbHelper = new DBHelper(context);
	}
	
	public org.eclipse.jgit.lib.Repository openRepository(final String mapping) throws RepositoryNotFoundException {
		return repositoryManager.openRepository(mapping).getRepository();
	}
	
	public void renameRepository(final int repositoryId, final String newMapping) {
		if(newMapping == null || "".equals(newMapping.trim())) {
			return;
		}
		
		Repository repository = null;
		try {
			repository = dbHelper.getRepositoryDao().queryForId(repositoryId);
		} catch (SQLException e) {
			Log.e(TAG, "Problem while retrieving repository by ID.", e);
			return;
		}
		
		if(newMapping.equals(repository.getMapping())) {
			return;
		}
		
		try {
			repositoryManager.renameRepository(repository.getMapping(), newMapping);
		} catch (RepositoryNotFoundException e) {
			Log.e(TAG, "Problem while renaming repository.", e);
			// TODO: Put some dialog message here.
		}
	}
	
	public org.eclipse.jgit.lib.Repository createRepository(final String mapping) throws RepositoryNotFoundException {
//		new Thread() {
//			@Override
//			public void run() {
//				try {
		return repositoryManager.createRepository(mapping).getRepository();
//				} catch (RepositoryNotFoundException e) {
//					Log.e(TAG, "Problem while creating repository.", e);
//					// TODO: Put some dialog message here perhaps.
//				}
//			}
//		}.start();
	}
	
	public void deleteRepository(final String mapping) {
		new Thread() {
			@Override
			public void run() {
				try {
					repositoryManager.deleteRepository(mapping);
				} catch (RepositoryNotFoundException e) {
					Log.e(TAG, "Problem while deleting repository.", e);
				}
			}
		}.start();
	}
	
//	public int getNumberOfBranches(final String mapping) {
//		ReporepositoryManager.openRepository(mapping);
//		
//		return 0;
//	}
}
