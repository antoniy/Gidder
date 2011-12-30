package net.antoniy.gidder.git;

import java.sql.SQLException;

import net.antoniy.gidder.db.DBHelper;
import net.antoniy.gidder.db.entity.Repository;

import org.eclipse.jgit.errors.RepositoryNotFoundException;

import android.content.Context;
import android.util.Log;

public class GitRepositoryDao {
	private final static String TAG = GitRepositoryDao.class.getSimpleName();
	
	private GitRepositoryManager repositoryManager;
	private DBHelper dbHelper;
	
	public GitRepositoryDao(Context context) {
		repositoryManager = new SDCardRepositoryManager();
		dbHelper = new DBHelper(context);
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
	
	public void createRepository(final String mapping) {
		new Thread() {
			@Override
			public void run() {
				try {
					repositoryManager.createRepository(mapping);
				} catch (RepositoryNotFoundException e) {
					Log.e(TAG, "Problem while creating repository.", e);
					// TODO: Put some dialog message here perhaps.
				}
			}
		}.start();
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
}
