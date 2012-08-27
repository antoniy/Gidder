package net.antoniy.gidder.beta.ssh;


import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;

/**
 * Dummy shell which prints a message and terminates.
 * <p>
 * This implementation is used to ensure clients who try to SSH directly to this
 * server without supplying a command will get a reasonable error message, but
 * cannot continue further.
 */
public class NoShell implements Factory<Command> {

	public NoShell() {
	}

	public Command create() {
		return new SendMessageCommand();
	}

}
