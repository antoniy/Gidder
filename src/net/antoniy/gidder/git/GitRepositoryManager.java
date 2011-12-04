// Copyright (C) 2009 The Android Open Source Project
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

package net.antoniy.gidder.git;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Repository;


/**
 * Manages Git repositories for the Gerrit server process.
 * <p>
 * Implementations of this interface should be a {@link Singleton} and
 * registered in Guice so they are globally available within the server
 * environment.
 */
public interface GitRepositoryManager {
  /** Note tree listing commits we refuse {@code refs/meta/reject-commits} */
  public static final String REF_REJECT_COMMITS = "refs/meta/reject-commits";

  /**
   * Get (or open) a repository by name.
   *
   * @param name the repository name, relative to the base directory.
   * @return the cached Repository instance. Caller must call {@code close()}
   *         when done to decrement the resource handle.
   * @throws RepositoryNotFoundException the name does not denote an existing
   *         repository, or the name cannot be read as a repository.
   */
  public abstract Repository openRepository(String name)
      throws RepositoryNotFoundException;

  /**
   * Create (and open) a repository by name.
   *
   * @param name the repository name, relative to the base directory.
   * @return the cached Repository instance. Caller must call {@code close()}
   *         when done to decrement the resource handle.
   * @throws RepositoryNotFoundException the name does not denote an existing
   *         repository, or the name cannot be read as a repository.
   */
  public abstract Repository createRepository(String name)
      throws RepositoryNotFoundException;

}
