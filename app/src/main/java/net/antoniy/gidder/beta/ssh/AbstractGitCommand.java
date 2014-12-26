package net.antoniy.gidder.beta.ssh;

import java.io.IOException;

import net.antoniy.gidder.beta.git.GitRepositoryDao;

import org.apache.sshd.server.Environment;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;

import android.content.Context;
import android.util.Log;

public abstract class AbstractGitCommand extends BaseCommand {
	private final static String TAG = AbstractGitCommand.class.getSimpleName();

	private final static String MSG_REPOSITORY_NOT_FOUND = "Repository not found.\r\n";
	private final static String MSG_REPOSITORY_ACCESS_PROBLEM = "Problem accessing the repository.\r\n";

	private String repoPath;

	private GitRepositoryDao gitRepositoryDao;
	protected SshAuthorizationManager sshAuthorizationManager;
	protected Repository repo;

	public AbstractGitCommand(Context context, String repoPath) {
		this.repoPath = repoPath;
		this.gitRepositoryDao = new GitRepositoryDao(context);
		this.sshAuthorizationManager = new SshAuthorizationManager(context);
	}

	public void start(final Environment env) {
		startThread(new Runnable() {
			public void run() {
				AbstractGitCommand.this.service(env);
			}
		});
	}

	private void service(final Environment env) {
		try {
			repo = gitRepositoryDao.openRepository(getRepositoryMapping());
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

	protected String getRepositoryMapping() {
		String mapping = null;
		if (repoPath.startsWith("/")) {
			mapping = repoPath.substring(1);
		} else {
			mapping = repoPath;
		}

		if (mapping.endsWith(Constants.DOT_GIT_EXT)) {
			mapping = mapping.substring(0, mapping.length() - Constants.DOT_GIT_EXT.length());
		}

		return mapping;
	}

	protected abstract void runImpl() throws IOException;
}
