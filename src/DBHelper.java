import java.sql.*;

public class DBHelper {
    public static Connection connection;
    public static Statement statement;

    public static void ConnectDB() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:ulearnJava.db");
        Statement statement = connection.createStatement();
    }

    public static void CreateDB() throws SQLException {
        statement = connection.createStatement();

        statement.execute("CREATE TABLE if not exists 'TransactionInfo' ( 'Series_reference' STRING  PRIMARY KEY," +
                "'Magnitude' INTEGER , 'Subject' STRING, 'Group' STRING, 'Series_title_1' STRING," +
                "'Series_title_2' STRING, 'Series_title_3' STRING, 'Series_title_4' STRING, 'Series_title_5' STRING);");
        statement.execute("CREATE TABLE if not exists 'TransactionsTable' ('Id' INTEGER PRIMARY KEY," +
                " 'Series_reference' STRING REFERENCES 'TransactionInfo' (Series_reference), 'Period' TEXT," +
                "'Data_value' STRING, 'Suppressed' STRING, 'Status' STRING, 'Units' STRING );");
    }

    public static void FillTransactionInfo(String queryValue) throws SQLException {
        String query = "INSERT INTO 'TransactionInfo' ('Series_reference', 'Magnitude', 'Subject', 'Group', " +
                "'Series_title_1', 'Series_title_2', 'Series_title_3', 'Series_title_4', 'Series_title_5')";
        query += String.format("VALUES (%s)", queryValue);
        statement.execute(query);
    }

    public static void FillTransactions(String queryValue) throws SQLException {
        String query = "INSERT INTO 'TransactionsTable' ('Id', 'Series_reference', 'Period', 'Data_value', " +
                "'Suppressed', 'Status', 'Units')";
        query += String.format("VALUES (%s)", queryValue);
        statement.execute(query);
    }
}