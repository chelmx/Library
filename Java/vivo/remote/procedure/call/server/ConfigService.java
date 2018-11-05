package vivo.remote.procedure.call.server;

public interface ConfigService {
	public String read();
	public void write(String config);
	public void execute(String command);
}
