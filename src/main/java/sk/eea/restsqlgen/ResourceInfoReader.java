package sk.eea.restsqlgen;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract  class ResourceInfoReader {
	protected Logger logger = Logger.getLogger("ResourceGenerator"); 
	
	private Map<String, String> tableQueries = new HashMap<String, String>();
	
	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 */
	public ResourceInfoReader() {
	}
	
	
	/**
	 * 
	 * @param tableName
	 * @return String
	 */
	public String getResourceQuery(String tableName) {
		return tableQueries.get(tableName);
	}
	
	/**
	 * 
	 * @return List<String>
	 */
	public Collection<String> getTables() {
		return tableQueries.keySet();
		
	}
	
	
	/**
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @throws GeneratorException
	 */
	public void initialize(String server, String schema, String user, String password) throws GeneratorException {
		Connection conn = null;
		ResultSet rs = null;
		try {
			Class.forName(getDriverClassName());
			conn = DriverManager.getConnection(getJDBCUrl(server, schema, user, password));
			rs = conn.createStatement().executeQuery(getQueryForTables(schema));
			while (rs.next()) {
				String tableName = rs.getString(1);
				logger.info("reading info for:" + tableName);
				String query = prepareResourceQuery(conn, tableName);
				tableQueries.put(tableName, query);
			}
		} catch (SQLException e) {
			logger.severe("error in readTables():" + e.toString());
			throw new GeneratorException(e);
		} catch (ClassNotFoundException e) {
			logger.severe("error in readTables():" + e.toString());
			throw new GeneratorException(e);
		} finally {
			closeResources(conn, rs);
		}
		
	}

	
	
	/**
	 * 
	 * @param conn
	 * @param table
	 * @return
	 * @throws GeneratorException
	 */
	private String prepareResourceQuery(Connection conn, String table) throws GeneratorException {
		ResultSet rs = null;
		StringBuilder query = new StringBuilder("select ");
		try {
			
			rs = conn.createStatement().executeQuery("select * from " + table);
			ResultSetMetaData metaData = rs.getMetaData();
			for (int col = 1; col <= metaData.getColumnCount(); col++) {
				query.append(col == 1 ? "" : ", ").append(table).append(".").append(metaData.getColumnName(col));
			}
			query.append(" from ").append(table);
		} catch (SQLException e) {
			logger.severe("error getResourceQuery:" + e.toString());
			throw new GeneratorException(e);
		} finally {
			closeResources(null, rs);
		}
		return query.toString();
		
	}
	
	/**
	 * 
	 * @param conn
	 * @param rs
	 */
	private void closeResources(Connection conn, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			logger.warning("error closing resources:" + e.toString());
		}
	}

	
	protected abstract String getQueryForTables(String schema);
	protected abstract String getJDBCUrl(String server, String schema, String user, String password);
	protected abstract String getDriverClassName();
	
}
