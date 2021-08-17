![](https://github.com/wniemiec-component-java/junit4-runner/blob/master/docs/img/logo/logo.jpg)

<h1 align='center'>JUnit 4 Runner</h1>
<p align='center'>Component for running JUnit 4 tests in code.</p>
<p align="center">
	<a href="https://github.com/wniemiec-component-java/junit4-runner/actions/workflows/windows.yml"><img src="https://github.com/wniemiec-component-java/junit4-runner/actions/workflows/windows.yml/badge.svg" alt=""></a>
	<a href="https://github.com/wniemiec-component-java/junit4-runner/actions/workflows/macos.yml"><img src="https://github.com/wniemiec-component-java/junit4-runner/actions/workflows/macos.yml/badge.svg" alt=""></a>
	<a href="https://github.com/wniemiec-component-java/junit4-runner/actions/workflows/ubuntu.yml"><img src="https://github.com/wniemiec-component-java/junit4-runner/actions/workflows/ubuntu.yml/badge.svg" alt=""></a>
	<a href="https://codecov.io/gh/wniemiec-component-java/junit4-runner"><img src="https://codecov.io/gh/wniemiec-component-java/junit4-runner/branch/master/graph/badge.svg?token=R2SFS4SP86" alt="Coverage status"></a>
	<a href="http://java.oracle.com"><img src="https://img.shields.io/badge/java-8+-D0008F.svg" alt="Java compatibility"></a>
	<a href="https://mvnrepository.com/artifact/io.github.wniemiec-component-java/junit4-runner"><img src="https://img.shields.io/maven-central/v/io.github.wniemiec-component-java/junit4-runner" alt="Maven Central release"></a>
	<a href="https://github.com/wniemiec-component-java/junit4-runner/blob/master/LICENSE"><img src="https://img.shields.io/github/license/wniemiec-component-java/junit4-runner" alt="License"></a>
</p>
<hr />

## ❇ Introduction
Simple component for running JUnit 4 tests in a simplified way in code.
## ❓ How to use
1. Add one of the options below to the pom.xml file: 

#### Using Maven Central (recomended):
```
<dependency>
  <groupId>io.github.wniemiec-component-java</groupId>
  <artifactId>junit4-runner</artifactId>
  <version>LATEST</version>
</dependency>
```

#### Using GitHub Packages:
```
<dependency>
  <groupId>wniemiec.component.java</groupId>
  <artifactId>junit4-runner</artifactId>
  <version>LATEST</version>
</dependency>
```

2. Run
```
$ mvn install
```

3. Use it
```
[...]

import wniemiec.component.java.JUnit4Runner;

[...]

Path workingDirectory = Path.of(".", "bin").toAbsolutePath().normalize();
Path stringUtilsClassPath = workingDirectory.resolve(
		Path.of("api", "util", "StringUtilsTest.class")
);
List&lt;Path> classpaths = List.of(
		workingDirectory.resolve(stringUtilsClassPath)
);

JUnit4Runner junit4runner = new JUnit4Runner.Builder()
	.workingDirectory(workingDirectory)
	.classPath(classpaths)
	.classSignature(classSignature)
	.build();

junit4runner.run();

[...]
```

## 📖 Documentation
|        Property        |Parameter type|Return type|Description|Default parameter value|
|----------------|-------------------------------|-----|------------------------|--------|
|run |`void`|`JUnit4Runner`|Initializes JUnit 4 Runner in a new process| - |
|quit |`void`|`void`|Stops JUnit 4 Runner process| - |
|isRunning | `void`|`boolean`|Checks whether JUnit 4 Runner is running| - |
|getTotalTests | `void`|`int`|Gets the total tests from the executed test set | - |

## 🚩 Changelog
Details about each version are documented in the [releases section](https://github.com/williamniemiec/wniemiec-component-java/junit4-runner/releases).

## 🤝 Contribute!
See the documentation on how you can contribute to the project [here](https://github.com/wniemiec-component-java/junit4-runner/blob/master/CONTRIBUTING.md).

## 📁 Files

### /
|        Name        |Type|Description|
|----------------|-------------------------------|-----------------------------|
|dist |`Directory`|Released versions|
|docs |`Directory`|Documentation files|
|src     |`Directory`| Source files|

## See more
* [JUnit 4](https://junit.org/junit4/)
* [JUnit](https://junit.org/)
