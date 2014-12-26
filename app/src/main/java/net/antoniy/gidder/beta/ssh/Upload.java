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

package net.antoniy.gidder.beta.ssh;

import java.io.IOException;

import net.antoniy.gidder.beta.exception.SshAuthorizationException;

import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.UploadPack;

import android.content.Context;
import android.util.Log;

/** Publishes Git repositories over SSH using the Git upload-pack protocol. */
public final class Upload extends AbstractGitCommand {
	private final static String TAG = Upload.class.getSimpleName();
	private final static String MSG_REPOSITORY_PERMISSIONS = "[Gidder] Don't have permissions to PULL from this repository.\r\n";
	
	public Upload(Context context, String repoPath) {
		super(context, repoPath);
	}
	
	@Override
	protected void runImpl() throws IOException {
		if(!hasPermission()) {
			err.write(MSG_REPOSITORY_PERMISSIONS.getBytes());
			err.flush();
			onExit(CODE_OK, MSG_REPOSITORY_PERMISSIONS);
			return;
		}
		
		Config config = new Config();
//		int timeout = Integer.parseInt(config.getString("transfer", null,
//				"timeout"));
		int timeout = 10;
		
		PackConfig packConfig = new PackConfig();
		packConfig.setDeltaCompress(false);
		packConfig.setThreads(1);
		packConfig.fromConfig(config);

		final UploadPack up = new UploadPack(repo);
		up.setPackConfig(packConfig);
		up.setTimeout(timeout);
		up.upload(in, out, err);
	}

	private boolean hasPermission() {
		String username = session.getUsername();
		
		boolean hasPermission = false;
		try {
			hasPermission = sshAuthorizationManager.hasRepositoryPullPermission(username, getRepositoryMapping());
		} catch (SshAuthorizationException e) {
			Log.w(TAG, "Problem with user authorization.", e);
			return false;
		}
		
		return hasPermission;
	}
	
}
