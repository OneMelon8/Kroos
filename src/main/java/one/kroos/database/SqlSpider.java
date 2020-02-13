package one.kroos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import one.kroos.utils.EnviroHandler;
import one.kroos.utils.LogUtil;

public class SqlSpider {

	private static boolean connected = false;
	private static Connection connection;

	private static final String DATABASE_URL = "jdbc:mysql://sql3.freesqldatabase.com:3306/sql3322547";

	/**
	 * Attempts to connect to the Arknights database
	 * 
	 * @return boolean if connection is successful
	 */
	public static boolean connect() {
		try {
			if (connected) {
				LogUtil.warning("Current SQL connection not closed, attempting to close connection...");
				connection.close();
				connected = false;
				LogUtil.info("SQL connection successfully closed!");
			}

			LogUtil.info("Attempting to open SQL connection with default credentials...");
			String[] login = EnviroHandler.getSqlAccount();
			connection = DriverManager.getConnection(DATABASE_URL, login[0], login[1]);
			connected = true;
		} catch (SQLException ex) {
			LogUtil.error("SQLException caught when connecting to database:");
			ex.printStackTrace();
			return false;
		}
		LogUtil.info("SQL connection successfully opened!");
		return true;
	}

	public static void close() {
		if (!connected) {
			LogUtil.warning("Attempting to close an unopened connection, skipping the operation!");
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			LogUtil.error("SQLException caught when closing the database:");
			e.printStackTrace();
		}
		connected = false;
		LogUtil.info("SQL connection successfully closed!");
	}

	public static ResultSet query(String command) {
		connectIfNotConnected();
		try {
			LogUtil.info("Running query command \"" + command + "\"...");
			Statement s = connection.createStatement();
			return s.executeQuery(command);
		} catch (SQLException ex) {
			LogUtil.error("SQLException caught when querying database:");
			ex.printStackTrace();
			return null;
		}
	}

	public static int update(String command) {
		connectIfNotConnected();
		try {
			LogUtil.info("Running update command \"" + command + "\"...");
			Statement s = connection.createStatement();
			return s.executeUpdate(command);
		} catch (SQLException ex) {
			LogUtil.error("SQLException caught when updating database:");
			ex.printStackTrace();
			return -1;
		}
	}

	public static boolean connectIfNotConnected() {
		if (connected)
			return true;
		return connect();
	}

	public static boolean isConnected() {
		return connected;
	}

	public static void driverTest() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
	}

	public static void main(String[] args) throws SQLException {
		connect();
		ResultSet rs = query("SELECT * FROM ArknightsRecruit");
		while (rs.next())
			System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
		close();
	}
}
