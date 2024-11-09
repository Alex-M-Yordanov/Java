package wish.list.command;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code CommandCreator} is a utility class responsible for creating {@link Command} objects
 * from raw input strings. It parses the input, extracting the command and its associated arguments.
 * <p>
 * The class provides methods for splitting command strings into individual arguments while
 * handling quoted arguments correctly (e.g., arguments that contain spaces within quotes).
 * </p>
 */
public class CommandCreator {
    // https://stackoverflow.com/a/14656159 - modified

    /**
     * Parses a raw input string into a list of arguments, handling quoted arguments.
     * <p>
     * The input string is split into tokens, and any quoted segments are treated as single arguments,
     * even if they contain spaces. For example, the input:
     * <pre>
     * placeBid "item123" "100"
     * </pre>
     * will be split into two arguments: "item123" and "100".
     * </p>
     *
     * @param input The raw input string to be parsed.
     * @return A list of argument strings extracted from the input.
     */
    private static List<String> getCommandArguments(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        boolean insideQuote = false;

        for (char c : input.toCharArray()) {
            if (c == '"') {
                insideQuote = !insideQuote;
            }
            if (c == ' ' && !insideQuote) {
                if (!sb.isEmpty()) {
                    tokens.add(sb.toString().replace("\"", ""));
                    sb.delete(0, sb.length());
                }

            } else {
                sb.append(c);
            }
        }

        if (!sb.isEmpty()) {
            tokens.add(sb.toString().replace("\"", ""));
        }

        return tokens;
    }

    /**
     * Creates a new {@link Command} object from a raw input string.
     * <p>
     * This method splits the input into a command and its associated arguments, then creates a
     * {@link Command} object. The first token is treated as the command, and the remaining tokens
     * are treated as the arguments for the command.
     * </p>
     *
     * @param clientInput The raw input string from the client.
     * @return A new {@link Command} object containing the parsed command and arguments.
     */
    public static Command newCommand(String clientInput) {
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);

        if (tokens.isEmpty()) {
            return new Command("", new String[0]);
        }
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return new Command(tokens.getFirst(), args);
    }
}
