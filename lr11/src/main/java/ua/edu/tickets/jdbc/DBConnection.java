package ua.edu.tickets.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton, що тримає пул з'єднань (HikariCP), а не одне спільне Connection.
 * Ліниве створення через holder-клас: JVM гарантує потокобезпечну ініціалізацію.
 * Параметри підключення беруться з application.properties; змінні середовища
 * DB_URL, DB_USER, DB_PASSWORD мають пріоритет (щоб пароль не потрапляв у репозиторій).
 */
public final class DBConnection {

    private final HikariDataSource dataSource;

    private DBConnection() {
        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env("DB_URL", props.getProperty("db.url")));
        config.setUsername(env("DB_USER", props.getProperty("db.user")));
        config.setPassword(env("DB_PASSWORD", props.getProperty("db.password", "")));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.size", "5")));
        config.setPoolName("tickets-pool");
        this.dataSource = new HikariDataSource(config);
    }

    private static final class Holder {
        private static final DBConnection INSTANCE = new DBConnection();
    }

    public static DBConnection getInstance() {
        return Holder.INSTANCE;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /** Віддає з'єднання з пулу. close() повертає його в пул, а не закриває фізично. */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        dataSource.close();
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private static Properties loadProperties() {
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new IllegalStateException("application.properties не знайдено в classpath");
            }
            Properties props = new Properties();
            props.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Не вдалося прочитати application.properties", e);
        }
    }
}
