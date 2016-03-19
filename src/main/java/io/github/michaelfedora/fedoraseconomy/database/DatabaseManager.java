package io.github.michaelfedora.fedoraseconomy.database;

import io.github.michaelfedora.fedoraseconomy.FedorasEconomy;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Created by Michael on 3/18/2016.
 */
public final class DatabaseManager {
    private DatabaseManager() { }

    public static final String DB_ID = "jdbc:h2:./mods/FedorasData/economy.db";
    public static final String DB_TABLE = "data";

    private static SqlService SQL_SERVICE;
    public static DataSource getDataSource(String jdbcURL) throws SQLException {

        if(SQL_SERVICE == null)
            SQL_SERVICE = Sponge.getServiceManager().provide(SqlService.class).orElseThrow(() -> new SQLException("Could not get SqlService!"));

        return SQL_SERVICE.getDataSource(jdbcURL);
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource(DB_ID).getConnection();
    }

    public static boolean initialize() {

        try(Connection conn = getConnection()) {

            conn.prepareCall("CREATE TABLE IF NOT EXISTS " + DB_TABLE + DatabaseQuery.makeConstructor()).execute();

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + DB_TABLE).executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            FedorasEconomy.getLogger().info("Table [" + DB_TABLE + "]: ");
            FedorasEconomy.getLogger().info(resultSet.toString());

            StringBuilder sb = new StringBuilder();

            for(int i = 1; i <= metaData.getColumnCount(); i++) {

                sb.append(metaData.getColumnName(i));
                if(i < metaData.getColumnCount())
                    sb.append(" | ");
            }
            FedorasEconomy.getLogger().info(sb.toString());

            while(resultSet.next()) {

                sb.setLength(0);
                for(int i = 1; i <= metaData.getColumnCount(); i++) {

                    sb.append(resultSet.getObject(i));
                    if(i < metaData.getColumnCount())
                        sb.append(" | ");
                }
                FedorasEconomy.getLogger().info(sb.toString());
            }

        } catch(SQLException e) {
            FedorasEconomy.getLogger().error("SQL Error", e);
            return false;
        }

        return true;
    }

    public static ResultSet selectWithMore(Connection conn, String columns, String name, String more) throws SQLException {

        String statement = "SELECT " + columns + " FROM " + DB_TABLE + " WHERE name=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, name);

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String columns, String more) throws SQLException {

        String statement = "SELECT " + columns + " FROM " + DB_TABLE + " " + more;

        return conn.prepareStatement(statement).executeQuery();
    }

    public static ResultSet selectAllWithMore(Connection conn, String name, String more) throws SQLException {
        return selectWithMore(conn, "*", name, more);
    }


    public static ResultSet selectAllWithMore(Connection conn, String more) throws SQLException {
        return selectWithMore(conn, "*", more);
    }

    public static ResultSet select(Connection conn, String columns, String name) throws SQLException {
        return selectWithMore(conn, columns, name, "");
    }

    public static ResultSet select(Connection conn, String columns) throws SQLException {
        return selectWithMore(conn, columns, "");
    }

    public static ResultSet selectAll(Connection conn, String name) throws SQLException {
        return selectWithMore(conn, "*", name, "");
    }

    public static ResultSet selectAll(Connection conn) throws SQLException {
        return selectWithMore(conn, "*", "");
    }

    public static boolean update(Connection conn, Object data, String name, DatabaseCategory category) {

        String statement = "UPDATE " + DB_TABLE + " SET data=? WHERE name=?";
    }
}
