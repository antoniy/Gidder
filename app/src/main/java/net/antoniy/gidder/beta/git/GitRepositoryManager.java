package net.antoniy.gidder.beta.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

public interface GitRepositoryManager {

	public Git openRepository(String mapping) throws RepositoryNotFoundException;

	public Git createRepository(String mapping) throws RepositoryNotFoundException;

	public void renameRepository(String oldMapping, String newMapping) throws RepositoryNotFoundException;

	public void deleteRepository(String mapping) throws RepositoryNotFoundException;

}
