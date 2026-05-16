package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Additional method definitions for JavaGrep that support.
 */
public interface JavaGrepLambda extends JavaGrep {

  /**
   * Gets a stream of all files within a directory traversed from the rootDir.
   *
   * @param rootDir the root directory to search
   * @return Stream containing files
   */
  Stream<File> listFilesStream(String rootDir);

  /**
   * Return a stream of strings of all lines in the input file.
   *
   * @param inputFile file to read
   * @return stream of lines in the file
   */
  Stream<String> readLinesStream(File inputFile);

  /**
   * Writes from stream of strings to file, separated by newlines.
   *
   * @param lines Strings to write
   * @throws IOException on write error/file error
   */
  void writeToFile(Stream<String> lines) throws IOException;
}
