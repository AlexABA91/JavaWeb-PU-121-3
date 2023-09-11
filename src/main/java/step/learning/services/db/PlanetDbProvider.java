package step.learning.services.db;

import com.google.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanetDbProvider implements DbProvider {
   private Connection connection;
   private final Logger logger;

   @Inject
    public PlanetDbProvider(Logger logger) {
        this.logger = logger;
    }

    @Override
    public Connection getConnection() {
        if( connection == null ) {
            try {
                Map<String, String> dbProperties = getDbProperties() ;

                // Class.forName( "com.mysql.cj.jdbc.Driver" ) ;
                Driver mysqlDriver = new com.mysql.cj.jdbc.Driver() ;
                DriverManager.registerDriver( mysqlDriver ) ;

                connection = DriverManager.getConnection(
                        dbProperties.get( "url" ),
                        dbProperties.get( "user" ),
                        dbProperties.get( "password" )
                ) ;
            }
            catch( Exception ex ) {
                logger.log( Level.SEVERE, ex.getMessage() ) ;
                throw new RuntimeException( "Connection failed" ) ;
            }
        }
        return  connection;
    }
    private Map<String, String> getDbProperties() throws IOException {
        try( InputStream stream =
                     this.getClass().getClassLoader()
                             .getResourceAsStream("planetdb.properties")
        ) {
            if (stream == null) {
                throw new IOException("Resource 'planetdb.properties' not found");
            }
            // аналог StringBuilder тільки для байтів
            ByteArrayOutputStream arr = new ByteArrayOutputStream(2048);
            byte[] buf = new byte[2048];
            int len;
            while ((len = stream.read(buf)) > 0) {
                arr.write(buf, 0, len);
            }
            String properties = arr.toString(StandardCharsets.UTF_8.name());
            String[] lines = properties.split("[\r\n]+");
            Map<String, String> dbProperties = new HashMap<>();
            for (String line : lines) {
                String[] pair = line.split("=", 2);
                dbProperties.put(pair[0], pair[1]);
            }
            return dbProperties ;
        }
    }
}
