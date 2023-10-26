import java.sql.*;
public class DBConnection {
    private String connectionUrl = "jdbc:sqlserver://"
            + System.getenv("DBNAME")+";encrypt=false;databaseName=TSQL2012;user="
            + System.getenv("USER")+";password="
            + System.getenv("PASSWORD");
    private Connection connection;
    private Statement statement;

    public Connection getConnection() {
        return connection;
    }

    public DBConnection() {
        try{
            connection = DriverManager.getConnection(connectionUrl);
            statement = connection.createStatement();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    public ResultSet selectAllFromTable(String tablename, boolean includeTop, String paramVal) throws SQLException {
        String request = "SELECT " +
                (includeTop ? "TOP " + paramVal : "") +
                " * FROM " +
                tablename;
        return statement.executeQuery(request);
    }


}
