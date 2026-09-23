package ca.jrvs.apps.stockquote.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

  private final String url;
  private final String user;
  private final String password;

  /**
   * Build the JDBC URL from the parameters and store credentials.
   * JDBC URL format: "jdbc:postgresql://host:port/database"
   *
   * @param host     - database host (e.g. "localhost")
   * @param port     - database port (e.g. "5432")
   * @param database - database name (e.g. "stock_quote")
   * @param user     - database username
   * @param password - database password
   */
  public DatabaseConnectionManager(String host, String port, String database,
      String user, String password) {
    this.url = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
    this.user = user;
    this.password = password;
  }

  /**
   * Create and return a new database connection.
   * Hint: Use DriverManager.getConnection(url, user, password)
   *
   * @return a live JDBC Connection
   * @throws SQLException if connection fails
   */
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(this.url, this.user, this.password);
  }
}