package net.antoniy.gidder.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import android.util.Log;

class SDCardRepositoryManager implements GitRepositoryManager {
	private final static String TAG = SDCardRepositoryManager.class.getSimpleName();
	private final String baseRepositoriesPath;

	public SDCardRepositoryManager(String baseRepositoriesPath) {
		if(!baseRepositoriesPath.endsWith("/")) {
			this.baseRepositoriesPath = baseRepositoriesPath + "/";
		} else {
			this.baseRepositoriesPath = baseRepositoriesPath;
		}
	}
	
	@Override
	public Repository openRepository(String name) throws RepositoryNotFoundException {
		Repository repository = null;
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			builder.setGitDir(getRepositoryPath(name + Constants.DOT_GIT_EXT));
			builder.setBare();	
			builder.setup();
			
			repository = builder.build();
			
			Log.i(TAG, "Repository created: " + repository.getDirectory().getCanonicalPath());
		} catch (IllegalArgumentException e) {
			throw new RepositoryNotFoundException("Error while configuring repository.", e);
		} catch (IOException e) {
			throw new RepositoryNotFoundException("Error while reading the repository.", e);
		} finally {
			if(repository != null) {
				repository.close();
			}
		}
		
		return repository;
	}

	@Override
	public void createRepository(String name) throws RepositoryNotFoundException {
		Repository repository = null;
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			builder.setGitDir(getRepositoryPath(name + Constants.DOT_GIT_EXT));
			builder.setBare();	
			builder.setup();
			
			repository = builder.build();
			repository.create(true);

			Log.i(TAG, "Repository created: " + repository.getDirectory().getCanonicalPath());
		} catch (IllegalArgumentException e) {
			throw new RepositoryNotFoundException("Error while configuring repository.", e);
		} catch (IOException e) {
			throw new RepositoryNotFoundException("Error while reading the repository.", e);
		} finally {
			if(repository != null) {
				repository.close();
			}
		}
		
		return;
	}
	
	public void renameRepository(String oldMapping, String newMapping) throws RepositoryNotFoundException {
		if(oldMapping == null 
				|| newMapping == null || "".equals(oldMapping.trim()) 
				|| "".equals(newMapping.trim()) || oldMapping.equals(newMapping)) {
			return;
		}
		
		File repoPath = new File(baseRepositoriesPath + oldMapping + Constants.DOT_GIT_EXT);
		if(!repoPath.exists()) {
			throw new RepositoryNotFoundException("Error while renaming repository - repository does not exists.");
		}
		
		boolean renameResult = repoPath.renameTo(new File(baseRepositoriesPath + newMapping + Constants.DOT_GIT_EXT));
		if(!renameResult) {
			throw new RepositoryNotFoundException("Renaming repository failed.");
		}
	}
	
	public void deleteRepository(String mapping) throws RepositoryNotFoundException {
		if(mapping == null || "".equals(mapping.trim())) {
			return;
		}
		
		File repoPath = new File(baseRepositoriesPath + mapping + Constants.DOT_GIT_EXT);
		if(!repoPath.exists()) {
			throw new RepositoryNotFoundException("Error while deleting repository - repository does not exists.");
		}
		
		if(!deleteDir(repoPath)) {
			Log.e(TAG, "Failed deleting repository: " + repoPath.getAbsolutePath());
		}
	}
	
	private boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

	private File getRepositoryPath(String repoMapping) {
		File basePath = new File(baseRepositoriesPath);
		if(!basePath.exists()) {
			if(!basePath.mkdirs()) {
				Log.w(TAG, "Repository path already exist or there is a problem creating folders.");
			}
		}
		
		File repoPath = new File(baseRepositoriesPath + repoMapping);
		
		return repoPath;
	}
}
