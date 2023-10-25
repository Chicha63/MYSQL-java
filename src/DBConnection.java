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
            this.connection = DriverManager.getConnection(connectionUrl);
            statement = connection.createStatement();
        }catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    public void selectAllFromEmployees(){
        try {
            String request = "SELECT * FROM HR.Employees";
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()){
                System.out.println(resultSet.getString("firstname")+ " " +resultSet.getString("lastname"));
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
