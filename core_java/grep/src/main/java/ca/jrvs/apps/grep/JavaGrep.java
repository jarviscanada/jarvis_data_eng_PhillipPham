package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Outputs to a file matching regex lines to a user given pattern
 * from lines of files in a directory.
 */
public interface JavaGrep {

  /**
   * Top level search workflow.
   *
   * @throws IOException file error during processing
   */
  void process() throws IOException;

  /**
   * Traverse and list files in a root directory.
   *
   * @param rootDir directory to search
   * @return list of files in the directory
   */
  List<File> listFiles(String rootDir);

  /**
   * Read a file and return all lines.
   *
   * @param inputFile file to read
   * @return list of lines from a file
   * @throws IllegalArgumentException if inputFile is not file
   */
  List<String> readLines(File inputFile);

  /**
   * Returns true if line contains regex pattern given by user.
   *
   * @param line string to check
   * @return true if pattern matches line
   */
  boolean containsPattern(String line);

  /**
   * Writes lines to a file, separated by newlines.
   *
   * @param lines Strings to write
   * @throws IOException on write error/file error
   */
  void writeToFile(List<String> lines) throws IOException;

  String getRootPath();

  void setRootPath(String rootPath);

  String getRegex();

  void setRegex(String regex);

  String getOutfile();

  void setOutfile(String outFile);
}
