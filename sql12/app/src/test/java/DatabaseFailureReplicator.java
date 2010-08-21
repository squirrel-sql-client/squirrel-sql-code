import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DatabaseFailureReplicator
{

    public static void main(String[] args) throws Throwable
    {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection con1 = DriverManager.getConnection("jdbc:oracle:thin:@cumberland:1521:csuite", "belair40", "password");

        DatabaseMetaData metaData1 = con1.getMetaData();

        Thread[] threads = new Thread[5];

        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(new MetaDataTestCase(metaData1));
        }

        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++)
        {
            threads[i].join();
        }
    }

    private static class MetaDataTestCase implements Runnable
    {
        DatabaseMetaData _metaData;

        public MetaDataTestCase(DatabaseMetaData metaData)
        {
            _metaData = metaData;
        }

        public void run()
        {
            try
            {
                ResultSet columnMetaData = _metaData.getColumns(null, null, "CS_ACL", null);
                dumpResultSet(columnMetaData);
                columnMetaData.close();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
        
        protected void dumpResultSet(ResultSet resultSet) throws SQLException
        {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int totalColumnCount = resultSetMetaData.getColumnCount();

            while (resultSet.next())
            {
                System.out.println("next");
                for (int columnCount = 1; columnCount <= totalColumnCount; columnCount++)
                {
                    System.out.print(resultSet.getObject(columnCount));

                    if (columnCount < totalColumnCount)
                    {
                        System.out.print(", ");
                    }
                }

            }
        }
        
    }
}
