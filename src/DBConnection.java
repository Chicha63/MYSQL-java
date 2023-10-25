import java.sql.*;
public class DBConnection {
    private String connectionUrl = "jdbc:sqlserver://"
            + System.getenv("DBNAME")+";encrypt=false;databaseName=TSQL2012;user="
            + System.getenv("USER")+";password="
            + System.getenv("PASSWORD");
    private Connection connection;
    private Statement statement;

    public DBConnection() {
        try{
            connection = DriverManager.getConnection(connectionUrl);
            statement = connection.createStatement();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    public ResultSet selectAllFromTable(String tablename) throws SQLException {
        String request = "SELECT * FROM "+tablename;
        return statement.executeQuery(request);
    }
}
