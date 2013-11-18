package sk.eea.restsqlgen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Logger;



/**
 * generate restSQL resources for all tables from schema, one resource file per table
 * 
 */
public class Generator {
	private static Logger logger = Logger.getLogger("Generator");

	private StringBuilder template;
	private Properties props; 
	
	public Generator() {
	}
		
	/**
	 * 
	 * @throws GeneratorException
	 */
	private void initialize() throws GeneratorException {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(getClass().getResourceAsStream("template.tpl"));
			template = new StringBuilder();			
			char[] buff = new char[2048];
			int len = 0;
			while ((len = isr.read(buff, 0, 2048)) != -1) {
				template.append(buff, 0, len);
			}
			
		} catch (IOException e) {
			logger.severe("error reading template:" + e.toString());
			throw new GeneratorException(e);
		} finally {
			try {
				isr.close();
			} catch (IOException e) {
				logger.severe("cannot close reader" + e.toString());	//nehadzem dalej, svet sa nezruti
			}
		}
		
		//read properies
		try {
			props = new Properties();
			props.load(getClass().getResourceAsStream("generator.properties"));
		} catch (IOException e) {
			logger.severe("no generator.properties:" + e.toString());
			throw new GeneratorException(e);
		}
		
	}
	
	
	/**
	 * 
	 * @param content
	 * @param tableName
	 */
	private void writeResource(String content, String tableName, String outPath) throws GeneratorException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(outPath + File.separatorChar + tableName.toUpperCase() + ".xml");
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.severe("error writing resource for:" + tableName + ", e:" + e.toString());
			throw new GeneratorException(e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				logger.severe("cannot close writer" + e.toString());	//nehadzem dalej, svet sa nezruti
			}
		}
	}
	
	/**
	 * 
	 * @param type
	 * @param url
	 * @param user
	 * @param password
	 * @return ResourceCreator
	 * @throws GeneratorException
	 */
	private ResourceInfoReader getResourceInfoReader(String type) throws GeneratorException {
		if ("postgres".equals(type)) {
			return new ResourceInfoReaderPGSQL();
		} else if ("mysql".equals(type)) {
			return new ResourceInfoReaderMySQL();
		} 
		
		throw new GeneratorException("unknown db type:" + type);
	}
	
	/**
	 * 
	 * @param conn
	 */
	private void generate() throws GeneratorException {
		
		
		int count = Integer.parseInt(props.getProperty("database.count", "0"));
		for (int dbDef = 1; dbDef <= count; dbDef++) {
			String server = props.getProperty("database." + dbDef + ".server");
			String schema = props.getProperty("database." + dbDef + ".schema");
			String type = props.getProperty("database." + dbDef + ".type");
			String user = props.getProperty("database." + dbDef + ".user");
			String password = props.getProperty("database." + dbDef + ".password");
			String outPath = props.getProperty("database." + dbDef + ".output");
			if (server == null || schema == null || type == null || user == null || password == null || outPath == null) {
				throw new GeneratorException("invalid generator.properties");
			}
			
			//resrouce creator 
			ResourceInfoReader rc = getResourceInfoReader(type);
			rc.initialize(server, schema, user, password);

			Collection<String> tables = rc.getTables();
			for (String table : tables) {
				String content = new String(template);
				content = content.replaceAll("%%QUERY%%", rc.getResourceQuery(table));
				content = content.replaceAll("%%TABLE%%", table);
				content = content.replaceAll("%%DB%%", schema);
				writeResource(content, table, outPath);
			}
			
		}
		
	}
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
    	try {
        	Generator g = new Generator();
        	g.initialize();
    		g.generate();
    	} catch (GeneratorException e) {
    		logger.severe("fatal error:" + e.toString());
    	}
    	
    }
}
