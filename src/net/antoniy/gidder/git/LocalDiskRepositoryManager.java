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

package net.antoniy.gidder.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.lib.RepositoryCache.FileKey;
import org.eclipse.jgit.util.FS;

import android.content.Context;

/** Manages Git repositories stored on the local filesystem. */
public class LocalDiskRepositoryManager implements GitRepositoryManager {
	private final static String TAG = LocalDiskRepositoryManager.class.getSimpleName();
	
	private File gitDirOf(String name) {
		return new File(name);
	}

	public Repository openRepository(String name) throws RepositoryNotFoundException {
//		if (isUnreasonableName(name)) {
//			throw new RepositoryNotFoundException("Invalid name: " + name);
//		}

		try {
			final FileKey loc = FileKey.lenient(gitDirOf(name), FS.DETECTED);
			return RepositoryCache.open(loc);
		} catch (IOException e1) {
			final RepositoryNotFoundException e2;
			e2 = new RepositoryNotFoundException("Cannot open repository " + name);
			e2.initCause(e1);
			throw e2;
		}
	}

	public Repository createRepository(String name) throws RepositoryNotFoundException {
		if (isUnreasonableName(name)) {
			throw new RepositoryNotFoundException("Invalid name: " + name);
		}

		try {
			File dir = FileKey.resolve(gitDirOf(name), FS.DETECTED);
			FileKey loc;
			if (dir != null) {
				// Already exists on disk, use the repository we found.
				//
				loc = FileKey.exact(dir, FS.DETECTED);
			} else {
				// It doesn't exist under any of the standard permutations
				// of the repository name, so prefer the standard bare name.
				//
				if (!name.endsWith(Constants.DOT_GIT_EXT)) {
					name = name + Constants.DOT_GIT_EXT;
				}
				loc = FileKey.exact(new File(name), FS.DETECTED);
			}
			return RepositoryCache.open(loc, false);
		} catch (IOException e1) {
			final RepositoryNotFoundException e2;
			e2 = new RepositoryNotFoundException("Cannot open repository " + name);
			e2.initCause(e1);
			throw e2;
		}
	}

	private boolean isUnreasonableName(String name) {
		if (name.length() == 0)
			return true; // no empty paths

		if (name.indexOf('\\') >= 0)
			return true; // no windows/dos stlye paths
		if (name.charAt(0) == '/')
			return true; // no absolute paths
		if (new File(name).isAbsolute())
			return true; // no absolute paths

		if (name.startsWith("../"))
			return true; // no "l../etc/passwd"
		if (name.contains("/../"))
			return true; // no "foo/../etc/passwd"
		if (name.contains("/./"))
			return true; // "foo/./foo" is insane to ask
		if (name.contains("//"))
			return true; // windows UNC path can be "//..."

		return false; // is a reasonable name
	}
}
