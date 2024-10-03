import java.util.logging.Formatter;
import java.util.logging.LogRecord;

//custom formatter class to format log messages
public class CustomFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        // Adding a separator line for readability
        sb.append("\n--------------------------------------------------\n");
        // Formatting the log message
        sb.append(String.format("%-7s: %s\n", record.getLevel(), record.getMessage()));
        // Adding a separator line for readability
        sb.append("--------------------------------------------------\n");
        return sb.toString();

    }
}
