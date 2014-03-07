package net.antoniy.gidder.beta.ssh;

import java.io.IOException;

import org.apache.sshd.server.Environment;
import org.eclipse.jgit.lib.Constants;

public class SendMessageCommand extends BaseCommand {
	
	private String message;
	private int exitCode = CODE_OK;
	
	public SendMessageCommand() {
	}
	
	public SendMessageCommand(String message, int exitCode) {
		this.message = message;
		this.exitCode = exitCode;
	}

	public void start(final Environment env) throws IOException {
		String message;
		message = getMessage(env.getEnv().get(Environment.ENV_USER));
		err.write(Constants.encodeASCII(message.toString()));
		err.flush();

		in.close();
		out.close();
		err.close();
		onExit(exitCode);
	}

	private String getMessage(String user) {
		if(message != null && !"".equals(message)) {
			return message;
		}
		
		StringBuilder msg = new StringBuilder();

		msg.append("\r\n");
		msg.append("  ****    Welcome to the Android Git Server   ****\r\n");
		msg.append("\r\n");

		String name = user;
		msg.append("  Hi ");
		msg.append(name);
		msg.append(", you have successfully connected over SSH.");
		msg.append("\r\n");
		msg.append("\r\n");

		msg.append("  Unfortunately, interactive shells are disabled.\r\n");
		msg.append("  To clone a hosted Git repository, use:\r\n");
		msg.append("\r\n");

		msg.append("  git clone ssh://");
		msg.append(user);
		msg.append("@<HOST>");
		msg.append("/");
		msg.append("<REPOSITORY_NAME>.git");
		msg.append("\r\n");

		msg.append("\r\n");
		
		return msg.toString();
	}

	public void destroy() {
	}
}
