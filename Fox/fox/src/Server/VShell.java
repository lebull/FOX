/**
 * Based on http://www.mkyong.com/java/how-to-execute-shell-command-from-java/
 */

package Server;

import java.io.InputStream;
import java.io.OutputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.Scanner;

//import java.lang.Thread;

public class VShell implements Runnable{
	
	private String startCmd;
	private Process p;
	
	private OutputStream stdin; // <- Eh?
	private InputStream stdout;
	
	private boolean alive;
	
	//private boolean isWindows = true;
	
	public VShell(String inCmd)
	{
		//this.startCmd = inCmd;
		//this.startCmd = "ping -c 3 google.com";
		this.startCmd = "python";
		this.alive = false;
	}

	public void run() {
 
		StringBuffer output = new StringBuffer();
		
		try {
			
			//Replaced Process Runtime with ProcessBuilder
			//http://stackoverflow.com/questions/3643939/java-process-with-input-output-stream
			//Everywhere I see this being used, this constructor uses raw strings, but I can only use a single array of strings. Strange...
			String[] finalCmd = {this.startCmd};
			ProcessBuilder builder = new ProcessBuilder(finalCmd);

			//Redirect stderr to the stream stdout is using.
			builder.redirectErrorStream(true);
			this.p = builder.start();

			this.alive = true;
			
			this.stdin  = this.p.getOutputStream();
			this.stdout = this.p.getInputStream();
			
			
			this.sendCmd("print 'wat'");
			//this.sendCmd("print 3 + 4");
			//this.sendCmd("quit()");
			
			/*
			if(this.isAlive()){
				try {
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.stdin));
					writer.write("print 'wat'");
					//writer.write('\n');
					writer.newLine();
					
					String outMsg = this.readOutput();
					System.out.println(outMsg);
					
					writer.write("print 2 + 4");
					writer.write('\n');
					//writer.newLine();
					writer.flush();
					writer.close();
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/
			
			
			
			this.p.destroy();
			String outMsg = this.readOutput();
			System.out.println(outMsg);

			System.out.println("Exit: " + String.valueOf(p.exitValue()));
			this.p.waitFor();
			this.alive = false;
			System.out.println("All done!");
		
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	

	
	public String readOutput(){
		
		//I don't think we have to make a new reader every time to read.
		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
		
		String returnString = "";
		
		//Write the output.
		Scanner scanner = new Scanner(reader);
		while (scanner.hasNextLine()) {
			returnString += scanner.nextLine() + "\n";
		}
		
		return returnString;
	}
	
	
	public void sendCmd(String inCmd){
		if(this.isAlive()){
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.stdin));
				writer.write(inCmd);
				//writer.write('\n');
				writer.newLine();
				writer.flush();
				//writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			throw new RuntimeException("Command sent to dead process.");
		}
	}
	
	public boolean isAlive()
	{
		return this.alive;
	}
	
	public void destroy(){
		this.p.destroy();
	}
}
