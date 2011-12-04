// Copyright (C) 2008 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.antoniy.gidder.ssh;

import java.io.IOException;

import net.antoniy.gidder.git.GitRepositoryManager;
import net.antoniy.gidder.git.LocalDiskRepositoryManager;

import org.apache.sshd.server.Environment;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;

import android.content.Context;
import android.util.Log;

public abstract class AbstractGitCommand extends BaseCommand {
	private final static String TAG = AbstractGitCommand.class.getSimpleName();
	
	private final static String MSG_REPOSITORY_NOT_FOUND = "Repository not found.\r\n";
	private final static String MSG_REPOSITORY_ACCESS_PROBLEM = "Problem accessing the repository.\r\n";
	
	private String repoPath;
	private Context context;

//	protected String projectName() {
//		String projectName = repoPath;
//
//		if (projectName.endsWith(".git")) {
//			// Be nice and drop the trailing ".git" suffix, which we never keep
//			// in our database, but clients might mistakenly provide anyway.
//			//
//			projectName = projectName.substring(0, projectName.length() - 4);
//		}
//
//		if (projectName.startsWith("/")) {
//			// Be nice and drop the leading "/" if supplied by an absolute path.
//			// We don't have a file system hierarchy, just a flat namespace in
//			// the database's Project entities. We never encode these with a
//			// leading '/' but users might accidentally include them in Git
//			// URLs.
//			//
//			projectName = projectName.substring(1);
//		}
//
//		return projectName;
//	}

	private GitRepositoryManager repoManager = new LocalDiskRepositoryManager();
	protected Repository repo;

	public AbstractGitCommand(Context context, String repoPath) {
		this.repoPath = repoPath;
		this.context = context;
	}
	
	public void start(final Environment env) {
		startThread(new Runnable() {
			public void run() {
				AbstractGitCommand.this.service(env);
			}
		});
	}

	private void service(final Environment env) {

		// TODO: Get user, get real repository path from repoPath from database and use that.
//		android.os.Environment.getExternalStorageState();
//		android.os.Environment.getExternalStorageDirectory()
		
		try {
			repo = repoManager.openRepository(repoPath);
		} catch (RepositoryNotFoundException e1) {
			Log.w(TAG, "Repository not found.", e1);
			onExit(CODE_ERROR, MSG_REPOSITORY_NOT_FOUND);
			return;
		}

		try {
			runImpl();
			
			out.flush();
			err.flush();
		} catch (IOException e) {
			Log.e(TAG, "I/O repository problem.", e);
			onExit(CODE_ERROR, MSG_REPOSITORY_ACCESS_PROBLEM);
			return;
		} finally {
			repo.close();
			try {
				in.close();
				out.close();
				err.close();
			} catch (IOException e) {
				Log.w(TAG, "Error closing the streams.", e);
				onExit(CODE_ERROR, MSG_REPOSITORY_ACCESS_PROBLEM);
				return;
			}
			
			onExit(CODE_OK);
		}
	}

	protected abstract void runImpl() throws IOException;
}
