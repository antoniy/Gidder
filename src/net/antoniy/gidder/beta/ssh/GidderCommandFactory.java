package net.antoniy.gidder.beta.ssh;

import java.util.regex.Pattern;

import net.antoniy.gidder.beta.exception.InvalidGitParameterException;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;

import android.content.Context;
import android.util.Log;

public class GidderCommandFactory implements CommandFactory {
	private final static String TAG = GidderCommandFactory.class.getSimpleName();
	private final static Pattern pathPattern = Pattern.compile("^[\\w-\\.]+$");
	
	private final static String MSG_COMMAND_NOT_FOUND = "Command not found.\r\n";
	
	private Context context;
	
	public GidderCommandFactory(Context context) {
		this.context = context;
	}
	
	@Override
	public Command createCommand(String command) {
		Log.i(TAG, "Process command: " + command);
		
		// Session manager works. I can do authorization somewhere inhere or use other class for authorization.
//		SshSessionManager sessionManager = SshSessionManager.getInstance();
		
		try {
			return processCommand(command);
		} catch (InvalidGitParameterException e) {
			Log.e(TAG, "Invalid git parameter.", e);
			return new SendMessageCommand(MSG_COMMAND_NOT_FOUND, SendMessageCommand.CODE_ERROR);
		}
	}
	
	private Command processCommand(String command) throws InvalidGitParameterException {
		if(command == null || "".equals(command.trim())) {
			return null;
		}
		
		command = command.trim();
		
		String[] words = command.split(" ");
		if(words.length != 2) {
			throw new InvalidGitParameterException("Command not found: " + command);
		}
		
		if("git-upload-pack".equals(words[0]) || "upload-pack".equals(words[0])) {
			return new Upload(context, parseRepoPath(words[1]));
		} else if ("git-receive-pack".equals(words[0]) || "receive-pack".equals(words[0])) {
			return new Receive(context, parseRepoPath(words[1]));
		} else {
			throw new InvalidGitParameterException("Command not found: " + command);
		}
	}
	
	private String parseRepoPath(String path) throws InvalidGitParameterException {
		path = path.replace("'", "").replace("/", "");
		
		if(pathPattern.matcher(path).find()) {
			Log.i(TAG, "Parsed repoPath: " + path);
			return path;
		}
		
		throw new InvalidGitParameterException("Git parameter is invalid: " + path);
	}

}
