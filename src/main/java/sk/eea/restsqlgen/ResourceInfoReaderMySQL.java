package sk.eea.restsqlgen;


public class ResourceInfoReaderMySQL extends ResourceInfoReader {
	
	public ResourceInfoReaderMySQL() {
		super();
	}
	
	@Override
	protected String getQueryForTables(String schema) {
		return "SELECT table_name FROM information_schema.tables WHERE table_type = 'base table' AND table_schema='" + schema + "'";
	}

	@Override
	protected String getJDBCUrl(String server, String schema, String user, String password) {
		return "jdbc:mysql://" + server + "/" + schema + "?user=" + user + "&password" + password;
	}

	@Override
	protected String getDriverClassName() {
		return null;		//@todo
	}

	

	
}
