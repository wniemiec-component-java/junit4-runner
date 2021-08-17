package wniemiec.component.java;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class JUnit4RunnerTest {

	@Test
	public void testRunStringUtilsTest() throws IOException, InterruptedException {
		Path workingDirectory = Path.of(".", "target", "test-classes").normalize().toAbsolutePath();
		Path classpath = workingDirectory.resolve(
				Path.of("wniemiec", "component", "java", "testfiles", "SimpleTest.class")
		);
		List<Path> classpaths = List.of(
				workingDirectory.resolve(classpath)
		);

		JUnit4Runner junit4Runner = new JUnit4Runner.Builder()
				.workingDirectory(workingDirectory)
				.classPath(classpaths)
				.classSignature("wniemiec.component.java.testfiles.SimpleTest")
				.displayVersion(true)
				.build();
		
			junit4Runner.run();
	}
}
