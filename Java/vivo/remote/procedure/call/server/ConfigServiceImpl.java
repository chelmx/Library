package vivo.remote.procedure.call.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.python.util.PythonInterpreter;

public class ConfigServiceImpl implements ConfigService {
	private static PythonInterpreter interpreter = new PythonInterpreter();

	@Override
	public String read() {
		String encoding = "UTF-8";  
        File file = new File("E:\\Workspace\\Java\\sampler\\py\\config.py");
        Long fileLength = file.length();  
        byte[] fileContent = new byte[fileLength.intValue()];  
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(fileContent);  
            in.close();
            return new String(fileContent, encoding);
        } catch (IOException e) {  
            e.printStackTrace();
            return null;
        }
	}

	@Override
	public void write(String config) {
		File file = new File("E:\\Workspace\\Java\\sampler\\py\\config.py");
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(config.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(String command) {
		interpreter.exec(command);
	}
	
}
