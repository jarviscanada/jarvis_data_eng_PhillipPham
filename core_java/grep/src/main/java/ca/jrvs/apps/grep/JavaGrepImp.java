package ca.jrvs.apps.grep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for JavaGrep interface. Outputs to a file matching regex lines to a user given
 * pattern from lines of files in a directory.
 */
public class JavaGrepImp implements JavaGrep {

  private static final Logger logger = LoggerFactory.getLogger("JavaGrep.class");

  private String regex;
  private String rootPath;
  private String outFile;

  @Override
  public void process() throws IOException {
    logger.debug("Start Processing");
    List<String> matchedLines = new ArrayList<>();
    for (File f : listFiles(getRootPath())) {
      logger.trace("Reading File");
      for (String line : readLines(f)) {
        if (containsPattern(line)) {
          matchedLines.add(line);
        }
      }
    }
    writeToFile(matchedLines);
    logger.debug("End Processing");
  }

  @Override
  public List<File> listFiles(String rootDir) {
    List<File> foundFiles = Collections.emptyList();
    Path path = Paths.get(rootDir);
    logger.trace("getting list of files from {}", rootDir);
    try (Stream<Path> fs = Files.walk(path)) {
      foundFiles = fs.filter(Files::isRegularFile)
          .map(Path::toFile)
          .collect(Collectors.toList());
      logger.debug("Success listing {} files", foundFiles.size());
    } catch (Exception ex) {
      logger.error("Exception listing files");
    }
    return foundFiles;
  }

  @Override
  public List<String> readLines(File inputFile) {
    List<String> lines = Collections.emptyList();
    try {
      lines = Files.readAllLines(inputFile.toPath());
      logger.debug("Success getting all lines of file");
    } catch (FileNotFoundException ex) {
      logger.error("File not found", ex);
    } catch (Exception ex) {
      logger.error("Exception with file", ex);
    }
    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    return Pattern.matches(getRegex(), line);
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    logger.debug("Writing to file");
    try (FileWriter fw = new FileWriter(getOutfile());
        BufferedWriter bw = new BufferedWriter(fw)) {
      for (String line : lines) {
        bw.write(line);
        bw.newLine();
      }
    }
    logger.debug("Success writing to file");
  }

  @Override
  public String getRootPath() {
    return this.rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    logger.debug("Set root path to {}", rootPath);
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return this.regex;
  }

  @Override
  public void setRegex(String regex) {
    logger.debug("Set regex to {}", regex);
    this.regex = regex;
  }

  @Override
  public String getOutfile() {
    return this.outFile;
  }

  @Override
  public void setOutfile(String outFile) {
    logger.debug("Set outfile to {}", outFile);
    this.outFile = outFile;
  }

  /**
   * Starts processes to simulate grep when called from the command line.
   */
  public static void main(String[] args) {

    // logging config
    BasicConfigurator.configure();

    if (args.length != 3) {
      logger.error("Illegal number of arguments: Expected 3, received {}", args.length);
      throw new IllegalArgumentException("Illegal number of arguments: Expected 3, received "
          + args.length);
    }

    // setup instance
    JavaGrep javaGrep = new JavaGrepImp();
    javaGrep.setRegex(args[0]);
    javaGrep.setRootPath(args[1]);
    javaGrep.setOutfile(args[2]);

    try {
      javaGrep.process();
    } catch (Exception ex) {
      logger.error("Error during process", ex);
    }
  }
}
