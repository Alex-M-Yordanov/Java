package wish.list.command;

/**
 * The {@code Command} record represents a command issued by a client or user.
 * It consists of a command string and an array of arguments associated with the command.
 * <p>
 * A {@link Command} is immutable, meaning its fields cannot be changed once created. This ensures
 * the integrity of the command and its arguments throughout its lifecycle. The {@code equals()},
 * {@code hashCode()}, and {@code toString()} methods are automatically provided for ease of comparison
 * and debugging.
 * </p>
 *
 * <p>Example:</p>
 * <pre>
 * Command command = new Command("placeBid", new String[] {"item123", "100"});
 * </pre>
 * In this example, the command is "placeBid" and the arguments are an item ID and bid amount.
 * </p>
 *
 * @param command   The command string (e.g., "placeBid", "buyItem").
 * @param arguments An array of arguments related to the command (e.g., item IDs, bid amounts).
 */
public record Command(String command, String[] arguments) {
}
