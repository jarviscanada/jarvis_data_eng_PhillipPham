# Introduction
This Java Grep app is a CLI tool that simulates the grep command's functionality to match a regex pattern across all lines of text in a specified directory.

This tool was built using Maven and IntelliJ. The code uses built-in Java libraries, such as the Regex library for pattern matching and the Files library for reading and writing files. Lambda functions and Streams were also used for more memory-efficient processing. Junit5 handles unit tests, and Docker handles deployment for easy user access.

# Quick Start
The easiest way to access the app is to pull the Docker image:
```
docker pull phiqw/grep
```
Then create and run containers from that image:
```
docker run --rm \
-v `pwd`/data:/data -v `pwd`/log:/log \
phiqw/grep .*regexpattern.* /data /log/grep.out
```
> [!NOTE]
> Replace .\*regexpattern.* with your regex, /data with your starting directory, and /log/grep.out with your outfile location as wanted

# Implemenation
## Pseudocode
The app takes the following steps to process files:
1. Read and save arguments for regex, start directory, and outfile  location.
2. Get files recursively from the start directory.
3. Read lines from files.
4. Match and keep the lines for the given regex.
5. Save the lines to the outfile.

## Performance Issue
There was a memory issue where, for large files, it would read all the lines into a list and overload the JVM memory. Using streams, lambda functions, and buffered writers, the program is much more efficient in loading and running the process chunks at a time, but the limits have not yet been tested for, say, a 50GB file.

# Test
End-to-end testing was performed manually. Sample data files were stored in a /data directory, and generated in a file in log/grep_out.txt. Saved output results were compared to the output from running grep itself.

JUnit was used to test certain functions that did not require the file system, like the Regex matching, to ensure these performed correctly in isolation. These tests ran through Maven's build system, which gives a report on how many tests pass.

There's also a slf4 logger that outputs process stages and errors for development purposes.

# Deployment
An image that hosts this app is on Dockerhub with an image tagged `phiqw/grep`. Check the [quickstart](#quick-start) section on how to run it.

The Dockerfile in the root directory is used to build the Docker image for the app. It builds off the Alpine version of AdoptOpenJDK for Java 8 and houses an uberjar of the compiled program. An entry point is set to run the jar, so when you use the docker run command, you just need to set the volumes and feed in the arguments for the app.

If you want to build the app from the source code, run the following commands from the root directory of the project:

```
# compile the code
mvn clean package
# build local docker image
docker build -t <imagetag> .
```
# Improvement
## Better test coverage
Currently, unit testing is limited to certain functions, but better coverage for functions and their edge cases would benefit future development.
## Better logging
Saving and configuring logs could be helpful when users run into issues with processing their data, and could lead to better bug reporting for future updates.
## GUI
Having a version that wraps the app with a graphical interface could be a lot more helpful for users to visualize the processes or onboard users who are not as familiar with CLI tools.