/**
 * Copyright (c) William Niemiec.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package wniemiec.component.java;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wniemiec.io.java.ArgumentFile;
import wniemiec.util.java.StringUtils;

/**
 * Responsible for executing JUnit 4 tests.
 */
public class JUnit4Runner {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final String JUNIT4_CORE_LIB = "junit-4.13.jar";
	private int totalTests;
	private Process process;
	private ProcessBuilder processBuilder;
	private BufferedReader output;
	private BufferedReader outputError;
	private boolean stopped;
	private boolean displayVersion;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private JUnit4Runner(Path workingDirectory, String classPath, 
						 String classSignature, boolean displayVersion) {
		this.displayVersion = displayVersion;

		this.processBuilder =  new ProcessBuilder(
			"java", 
				"-cp", classPath, 
				"org.junit.runner.JUnitCore", classSignature
		);
			
		this.processBuilder.directory(workingDirectory.toFile());
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	public static class Builder	{
		
		private Path argumentFile; 
		private Path workingDirectory;
		private List<Path> classPath;
		private String classSignature;
		private boolean displayVersion = false;
		
		
		public Builder argumentFile(Path argumentFile) {
			this.argumentFile = argumentFile;
			
			return this;
		}
		
		public Builder workingDirectory(Path workingDirectory) {
			this.workingDirectory = workingDirectory;
			
			return this;
		}
		
		public Builder classPath(List<Path> classPath) {
			this.classPath = new ArrayList<>(classPath);
			
			return this;
		}
		
		public Builder classSignature(String classSignature) {
			this.classSignature = classSignature;
			
			return this;
		}
		
		public Builder displayVersion(boolean displayVersion) {
			this.displayVersion = displayVersion;
			
			return this;
		}
		
		/**
		 * Creates JUnit 4 Runner with provided information. It is necessary to 
		 * provide all required fields. The required fields are: <br>
		 * <ul>
		 * 	<li>Class path</li>
		 * 	<li>Class signature</li>
		 * </ul>
		 * 
		 * @return		JUnit4Runner with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public JUnit4Runner build() {
			checkRequiredFields();
			includeJUnitCore();
			includeJavaClasspath();
			createArgumentFileFromClassPath();	// Avoids 'CreateProcess error=206'
			
			if (argumentFile == null) {
				return new JUnit4Runner(
						workingDirectory, 
						StringUtils.implode(relativizeClassPaths(), File.pathSeparator),
						classSignature,
						displayVersion
				);
			}
			else {
				return new JUnit4Runner(
						workingDirectory, 
						"@" + argumentFile, 
						classSignature,
						displayVersion
				);
			}
		}

		private void checkRequiredFields() {
			if ((classPath == null) || classPath.isEmpty())
				throw new IllegalStateException("Class path cannot be empty");
			
			if ((classSignature == null) || classSignature.isBlank())
				throw new IllegalStateException("Class signature cannot be empty");
		}

		private void includeJUnitCore() {
			classPath.add(
					Path.of(".", "lib", JUNIT4_CORE_LIB)
						.toAbsolutePath()
						.normalize()
			);
		}

		private void includeJavaClasspath() {
			for (String path : System.getProperty("java.class.path").split("\\" + File.pathSeparator)) {
				classPath.add(Path.of(path));
			}
		}
		
		private void createArgumentFileFromClassPath() {
			ArgumentFile argFile = new ArgumentFile(
				Path.of(System.getProperty("java.io.tmpdir")),
				"argfile-junit4"
			);

			try {
				argumentFile = argFile.create(classPath);
			} 
			catch (IOException e) {
				argumentFile = null;
			}
		}
		
		private List<Path> relativizeClassPaths() {
			if (classPath == null) 
				return new ArrayList<>();
			
			List<Path> relativizedClassPaths = new ArrayList<>();
			Path relativizedPath;
			
			for (int i = 0; i < classPath.size(); i++) {
				if (classPath.get(i).isAbsolute())
					relativizedPath = workingDirectory.relativize(classPath.get(i));
				else
					relativizedPath = classPath.get(i);
					
				relativizedClassPaths.add(i, relativizedPath);
			}
			
			return relativizedClassPaths;
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Initializes JUnit 4 Runner in a new process.
	 * 
	 * @throws		IOException If JUnit 4 Runner cannot be initialized 
	 * @throws 		InterruptedException If the current thread of JUnit 4 Runner 
	 * process is interrupted by another thread while it is running.
	 */
	public void run() throws IOException, InterruptedException {	
		initializeCLI();	
		checkErrors();
		parseCLI();
		closeCLI();
	}

	private void initializeCLI() throws IOException, InterruptedException {
		process = processBuilder.start();
		Thread.sleep(3000);
		
		stopped = false;			
		totalTests = 0;
		
		output = new BufferedReader(
				new InputStreamReader(process.getInputStream())
		);
		outputError = new BufferedReader(new InputStreamReader(
				process.getErrorStream())
		);
	}
	
	private boolean checkErrors() throws IOException {
		boolean error = false;
		String line;
		
		while (outputError.ready() && (line = outputError.readLine()) != null) {
			System.err.println(line);
			error = true;
		}
		
		return error;
	}

	private void parseCLI() throws IOException {
		String line;
		boolean fatalError = false;
		
		while (!fatalError && !stopped && (line = output.readLine()) != null) {
			boolean error = checkErrors();
			
			if (line.contains("OK (")) {
				totalTests = Integer.valueOf(extractNumbers(line));
			}
			else if (line.contains("JUnit version")) {
				if (displayVersion)
					System.out.println(line);
			}
			else if (!error){
				System.out.println(line);
			}
			
			System.out.flush();
			
			fatalError = line.equals("FAILURES!!!");
		}
	}
	
	private String extractNumbers(String line) {
		Pattern patternNumbers = Pattern.compile("[0-9]+");
		Matcher m = patternNumbers.matcher(line);
		
		if (!m.find())
			return "";
		
		return m.group();
	}
	
	private void closeCLI() throws IOException, InterruptedException {
		if (stopped)
			return;
		
		closeOutput();
		process.waitFor();
	}
	
	/**
	 * Stops JUnit 4 Runner process.
	 * 
	 * @throws		IOException If JUnit 4 Runner cannot be stopped 
	 */
	public void quit() throws IOException {
		if (process == null)
			return;
		
		stopped = true;
		process.destroyForcibly();
		closeOutput();
	}
	
	private void closeOutput() throws IOException {
		if (output != null)
			output.close();
			
		if (outputError != null)
			outputError.close();
	}
	
	/**
	 * Checks whether JUnit 4 Runner is running.
	 * 
	 * @return		True if JUnit 4 Runner is running; false otherwise
	 */
	public boolean isRunning() {
		return	(process != null) 
				&& process.isAlive();
	}
	
	/**
	 * Gets the total tests from the executed test set.
	 * 
	 * @return		 Total tests from the executed test set
	 */
	public int getTotalTests() {
		return totalTests;
	}
}
