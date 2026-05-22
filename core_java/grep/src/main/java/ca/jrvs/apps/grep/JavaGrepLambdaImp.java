package ca.jrvs.apps.grep;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Instance of JavaGrep that uses lambda functions and streams for more efficient processing.
 */
public class JavaGrepLambdaImp extends JavaGrepImp implements JavaGrepLambda {

  /**
   * Process grep with lambda functions for efficiency.
   *
   * @throws UncheckedIOException File IO exception
   */
  @Override
  public void process() throws IOException {
    logger.debug("Start Processing");
    try (Stream<File> fs = listFilesStream(getRootPath())) {
      writeToFile(fs.flatMap(this::readLinesStream).filter(this::containsPattern));
    }
    logger.debug("End Processing");
  }

  @Override
  public Stream<File> listFilesStream(String rootDir) {
    Path path = Paths.get(rootDir);
    logger.trace("getting stream of files from {}", rootDir);
    try {
      return Files.walk(path)
          .filter(Files::isRegularFile)
          .map(Path::toFile);
    } catch (IOException ex) {
      logger.error("Exception listing files");
    }
    return Stream.empty();
  }

  @Override
  public Stream<String> readLinesStream(File inputFile) throws IllegalArgumentException {
    checkFileRegular(inputFile);
    try {
      logger.trace("Getting stream of lines from file");
      return Files.lines(inputFile.toPath());
    } catch (IOException ex) {
      logger.error("Exception reading line stream from file", ex);
    }
    return Stream.empty();
  }

  @Override
  public void writeToFile(Stream<String> lines) throws IOException {
    logger.trace("Writing to file");
    try (FileWriter fw = new FileWriter(getOutfile());
        BufferedWriter bw = new BufferedWriter(fw)) {
      lines.forEach(line -> {
        try {
          bw.write(line);
          bw.newLine();
        } catch (IOException ex) {
          logger.error("IO Exception on write", ex);
          throw new UncheckedIOException(ex);
        }
      });
    }
    logger.trace("Success writing to file");
  }
}
