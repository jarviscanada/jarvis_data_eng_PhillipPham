package ca.jrvs.apps.grep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runner for Java Grep instances.
 */
public class Run {

  /**
   * Starts processes to simulate grep when called from the command line.
   */
  public static void main(String[] args) {

    // setup instance
    JavaGrep javaGrep = new JavaGrepLambdaImp();
    Logger logger = LoggerFactory.getLogger("Run.class");

    if (args.length != 3) {
      logger.error("Illegal number of arguments: Expected 3, received {}", args.length);
      throw new IllegalArgumentException("Illegal number of arguments: Expected 3, received "
          + args.length);
    }
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
