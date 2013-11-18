package sk.eea.restsqlgen;


public class ResourceInfoReaderPGSQL extends ResourceInfoReader {

	public ResourceInfoReaderPGSQL() {
		super();
	}
	
	@Override
	protected String getQueryForTables(String schema) {
		return "select quote_ident(table_name)  from information_schema.tables "
				+ "where table_type = 'BASE TABLE' and not table_schema ~ '^(information_schema|pg_.*)$'";
	}

	@Override
	protected String getJDBCUrl(String server, String schema, String user, String password) {
		return "jdbc:postgresql://" + server + "/" + schema + "?user=" + user + "&password=" + password;
	}

	protected String getDriverClassName() {
		return "org.postgresql.Driver";
	}
	

	
}
