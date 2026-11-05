package ua.edu.tickets.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Виконує init.sql з classpath під час старту програми.
 * Скрипт ідемпотентний (CREATE ... IF NOT EXISTS), тому повторний запуск безпечний.
 */
public final class SchemaInitializer {

    private SchemaInitializer() {}

    public static void init() {
        List<String> statements = readStatements("init.sql");
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement st = conn.createStatement()) {
            for (String sql : statements) {
                st.execute(sql);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Не вдалося виконати init.sql", e);
        }
    }

    static List<String> readStatements(String resource) {
        try (InputStream in = SchemaInitializer.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException(resource + " не знайдено в classpath");
            }
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            StringBuilder cleaned = new StringBuilder();
            for (String line : text.split("\\R")) {
                String trimmed = line.strip();
                if (!trimmed.startsWith("--")) {
                    cleaned.append(line).append('\n');
                }
            }
            List<String> result = new ArrayList<>();
            for (String part : cleaned.toString().split(";")) {
                if (!part.isBlank()) {
                    result.add(part.strip());
                }
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Не вдалося прочитати " + resource, e);
        }
    }
}
