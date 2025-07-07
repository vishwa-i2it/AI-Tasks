# Module 1: AI Error Analysis & Stack Trace Debugging

## Goal
- Master AI Error Analysis
- Use AI to understand and resolve cryptic errors
- Develop prevention strategies
- Gain proficiency with IDE debugging tools

## Steps
1. AI-Enhanced Error Understanding
2. Prevention Strategy
3. IDE Debugging Tools Mastery

## Case Study: Debugging `NoSuchFieldError: qualid` During Maven Compile

### Example Error
```
Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.10.1:compile (default-compile) on project some-service: Fatal error compiling: java.lang.NoSuchFieldError: Class com.sun.tools.javac.tree.JCTree$JCImport does not have member field 'com.sun.tools.javac.tree.JCTree qualid' -> [Help 1]
```

### 1. What is Causing This Error?
- **Simple Explanation:**
  - Your build is trying to use a field (`qualid`) in a Java compiler class (`JCTree$JCImport`) that does not exist in the version of the Java compiler (`javac`) you are using.
  - This usually means there is a mismatch between the Java version your code (or a plugin) expects and the Java version actually being used.

### 2. Most Common Reasons
- Mixing different Java versions (e.g., compiling with Java 11 but a plugin expects Java 8 internals).
- Using a tool or plugin (like a code analysis or annotation processor) that relies on internal Java compiler APIs, which have changed between Java versions.
- Having multiple or conflicting versions of the JDK or `tools.jar` on your classpath.

### 3. Step-by-Step Debugging Approach

**Step 1: Check Your Java Version**
```sh
java -version
javac -version
```
- Make sure both commands report the same, expected version (e.g., Java 11).

**Step 2: Check Your Maven Compiler Plugin Configuration**
- Open your `pom.xml` and look for the `maven-compiler-plugin` section:
```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.10.1</version>
  <configuration>
    <source>11</source>
    <target>11</target>
  </configuration>
</plugin>
```
- Ensure `<source>` and `<target>` match your installed JDK.

**Step 3: Check for Plugins Using Internal Java APIs**
- Look for plugins like Lombok, annotation processors, or code analyzers in your `pom.xml`.
- Example:
```xml
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
</dependency>
```
- Check their documentation for Java version compatibility.

**Step 4: Clean and Rebuild**
```sh
mvn clean
mvn compile
```
- This removes old build artifacts that might cause conflicts.

**Step 5: Check for Multiple JDKs**
```sh
which java
which javac
```
- Make sure both point to the same JDK installation.

**Step 6: Exclude Conflicting Dependencies**
- If a dependency brings in an old or conflicting `tools.jar`, exclude it in your `pom.xml`:
```xml
<dependency>
  <groupId>some.group</groupId>
  <artifactId>some-artifact</artifactId>
  <exclusions>
    <exclusion>
      <groupId>com.sun</groupId>
      <artifactId>tools</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

### 4. Permanent Fix
- Align your JDK version, Maven compiler plugin, and all plugins/dependencies to be compatible.
- Avoid using plugins that depend on internal Java APIs, or use versions that support your JDK.
- Document and standardize the Java version for your project/team.

### 5. Prevention Strategies
- Use a build tool (like Maven Toolchains) to enforce a single JDK version for all builds.
- Add CI checks to verify Java version consistency.
- Regularly update plugins and dependencies to versions compatible with your JDK.
- Avoid relying on internal Java APIs in your own code or plugins.
