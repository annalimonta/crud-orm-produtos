import java.sql.*;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

/**
 * Gerencia a conexao com o banco de dados SQLite.
 *
 * O driver JDBC do SQLite cria o arquivo do banco automaticamente na primeira
 * conexao, por isso nao e preciso instalar nenhum servidor de banco de dados.
 * Classe igual a apresentada no tutorial de ORM.
 */
public class Database
{
   private String databaseName = null;
   private JdbcConnectionSource connection = null;

   public Database(String databaseName) {
       this.databaseName = databaseName;
   }

   public JdbcConnectionSource getConnection() throws SQLException {
      if ( databaseName == null ) {
          throw new SQLException("database name is null");
      }
      if ( connection == null ) {
          try {
              connection = new JdbcConnectionSource("jdbc:sqlite:"+databaseName);
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            System.out.println("Opened database successfully");
      }
      return connection;
   }

   public String getDatabaseName() {
       return databaseName;
   }

   public void close() {
       if ( connection != null ) {
           try {
               connection.close();
               this.connection = null;
           } catch (java.lang.Exception e) {
               System.err.println(e);
           }
       }
   }
}
