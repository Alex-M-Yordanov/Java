package wish.list.command;

import wish.list.storage.Storage;

import java.util.Map;

/**
 * {@code CommandExecutor} is responsible for executing commands related to managing a wish list.
 * It processes the commands and interacts with the storage to perform actions like listing items,
 * buying items, placing bids, and more.
 * <p>
 * The class contains logic for validating input arguments and performing appropriate actions based
 * on the command received. Each command corresponds to an action related to the wish list, and the
 * response is returned as a string.
 * </p>
 */
public class CommandExecutor {

    // Error message format for invalid argument count
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
        "Invalid count of arguments: \"%s\" expects %d arguments. Example: \"%s\"";

    // Command names
    private static final String LIST_ITEM = "list-item";
    private static final String LIST_ITEMS = "list-items";
    private static final String BUY_ITEM = "buy-item";
    private static final String BID_ITEM = "bid-item";
    private static final String VIEW_BIDS = "view-bids";
    private static final String REMOVE_ITEM = "remove-item";

    // Constants for argument counts
    private static final int ARG_COUNT_ONE = 1;
    private static final int ARG_COUNT_TWO = 2;
    private static final int ARG_COUNT_THREE = 3;

    private final Storage storage;

    /**
     * Creates a new {@code CommandExecutor} that interacts with the specified storage.
     *
     * @param storage The storage system to interact with for managing items and bids.
     */
    public CommandExecutor(Storage storage) {
        this.storage = storage;
    }

    /**
     * Executes the given command based on its type and arguments.
     * <p>
     * The command is processed using a switch statement, and the appropriate
     * method is called based on the command type.
     * </p>
     *
     * @param cmd The command to execute, containing the command name and its arguments.
     * @return A string response indicating the result of executing the command.
     */
    public String execute(Command cmd) {
        // Using the "command" getter from the Command record to determine the action
        return switch (cmd.command()) {
            case LIST_ITEM -> listItem(cmd.arguments());
            case LIST_ITEMS -> listItems();
            case BUY_ITEM -> buyItem(cmd.arguments());
            case BID_ITEM -> bidItem(cmd.arguments());
            case VIEW_BIDS -> viewBids(cmd.arguments());
            case REMOVE_ITEM -> removeItem(cmd.arguments());
            default -> "Unknown command";
        };
    }

    /**
     * Lists a new item for sale in the storage system.
     * <p>
     * This method expects three arguments: username, item name, and price.
     * It validates the arguments and interacts with the storage to list the item.
     * </p>
     *
     * @param args The arguments passed with the command. Should contain the username, item name, and price.
     * @return A response indicating the result of listing the item.
     */
    private String listItem(String[] args) {
        if (args.length != ARG_COUNT_THREE) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, LIST_ITEM, ARG_COUNT_THREE,
                LIST_ITEM + " <username> <item_name> <price>");
        }

        String user = args[0];
        String itemName = args[1];
        double price;
        try {
            price = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            return "Invalid price: must be a number.";
        }

        int itemId = storage.addItem(user, itemName, price);
        return String.format("Item listed with ID %d by %s for $%.2f", itemId, user, price);
    }

    /**
     * Lists all items currently available in the storage.
     * <p>
     * This method retrieves all items and formats them into a readable string for display.
     * </p>
     *
     * @return A string containing all the items for sale, or a message indicating that no items are listed.
     */
    private String listItems() {
        Map<Integer, String> items = storage.listItems();
        if (items.isEmpty()) {
            return "No items currently listed.";
        }

        StringBuilder response = new StringBuilder("Items for sale:\n");
        items.forEach((id, item) -> response.append(String.format("[%d] %s%n", id, item)));
        return response.toString();
    }

    /**
     * Buys an item from the storage system.
     * <p>
     * This method expects two arguments: username and item ID. It validates the arguments and interacts with
     * the storage to complete the purchase.
     * </p>
     *
     * @param args The arguments passed with the command. Should contain the username and item ID.
     * @return A response indicating the result of buying the item.
     */
    private String buyItem(String[] args) {
        if (args.length != ARG_COUNT_TWO) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, BUY_ITEM, ARG_COUNT_TWO,
                BUY_ITEM + " <username> <item_id>");
        }

        String user = args[0];
        int itemId;
        try {
            itemId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return "Invalid item ID: must be an integer.";
        }

        return storage.buyItem(user, itemId);
    }

    /**
     * Places a bid on an item.
     * <p>
     * This method expects three arguments: username, item ID, and bid price. It validates the arguments and
     * interacts with the storage to place the bid.
     * </p>
     *
     * @param args The arguments passed with the command. Should contain the username, item ID, and bid price.
     * @return A response indicating the result of placing the bid.
     */
    private String bidItem(String[] args) {
        if (args.length != ARG_COUNT_THREE) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, BID_ITEM, ARG_COUNT_THREE,
                BID_ITEM + " <username> <item_id> <bid_price>");
        }

        String user = args[0];
        int itemId;
        double bidPrice;
        try {
            itemId = Integer.parseInt(args[1]);
            bidPrice = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            return "Invalid input: item ID must be an integer and bid price a number.";
        }

        return storage.placeBid(user, itemId, bidPrice);
    }

    /**
     * Views the bids placed on an item.
     * <p>
     * This method expects one argument: item ID. It validates the argument and interacts with the storage
     * to retrieve the bids for the item.
     * </p>
     *
     * @param args The arguments passed with the command. Should contain the item ID.
     * @return A response indicating the result of viewing the bids.
     */
    private String viewBids(String[] args) {
        if (args.length != ARG_COUNT_ONE) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, VIEW_BIDS, ARG_COUNT_ONE, VIEW_BIDS + " <item_id>");
        }

        int itemId;
        try {
            itemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return "Invalid item ID: must be an integer.";
        }

        return storage.viewBids(itemId);
    }

    /**
     * Removes an item from the storage system.
     * <p>
     * This method expects two arguments: username and item ID. It validates the arguments and interacts with
     * the storage to remove the item.
     * </p>
     *
     * @param args The arguments passed with the command. Should contain the username and item ID.
     * @return A response indicating the result of removing the item.
     */
    private String removeItem(String[] args) {
        if (args.length != ARG_COUNT_TWO) {
            return String.format(INVALID_ARGS_COUNT_MESSAGE_FORMAT, REMOVE_ITEM, ARG_COUNT_TWO
                ,
                REMOVE_ITEM + " <username> <item_id>");
        }

        String user = args[0];
        int itemId;
        try {
            itemId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            return "Invalid item ID: must be an integer.";
        }

        return storage.removeItem(user, itemId);
    }
}
