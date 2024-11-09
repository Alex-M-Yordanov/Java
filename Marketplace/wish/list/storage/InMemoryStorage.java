package wish.list.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * InMemoryStorage is an implementation of the {@link Storage} interface that
 * provides an in-memory data structure to store and manage items in a
 * marketplace system.
 * <p>
 * This class supports operations for adding, removing, listing, and
 * managing items, including placing bids and marking items as sold.
 * </p>
 */
public class InMemoryStorage implements Storage {

    // A map to store items with their unique item IDs as keys.
    private final Map<Integer, Item> items = new HashMap<>();
    private int nextItemId = 0;

    /**
     * Gets an item by its ID.
     *
     * @param itemId The ID of the item to retrieve.
     * @return The item associated with the given ID, or {@code null} if no such item exists.
     */
    public Item getItem(int itemId) {
        return items.get(itemId);
    }

    /**
     * Adds a new item to the storage.
     *
     * @param user     The name of the user who owns the item.
     * @param itemName The name of the item to add.
     * @param price    The price of the item.
     * @return The unique ID assigned to the newly added item.
     * @throws IllegalArgumentException if the item name is empty or the price is not greater than zero.
     */
    @Override
    public int addItem(String user, String itemName, double price) {
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }

        Item item = new Item(user, itemName, price);
        int id = nextItemId++;
        items.put(id, item);
        return id;
    }

    /**
     * Buys an item and marks it as sold.
     *
     * @param user   The name of the user buying the item.
     * @param itemId The ID of the item to buy.
     * @return A string message indicating the result of the buy operation.
     * @throws IllegalStateException if the item has already been sold or does not exist.
     */
    @Override
    public String buyItem(String user, int itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            return "Item not found.";
        }
        if (item.sold) {
            return "Item already sold.";
        }
        item.sold = true;
        return "Item bought by " + user + " for $" + item.price;
    }

    /**
     * Places a bid on an item if the bid is higher than the current price.
     *
     * @param user     The name of the user placing the bid.
     * @param itemId   The ID of the item to bid on.
     * @param bidPrice The bid amount.
     * @return A string message indicating the result of the bidding operation.
     */
    @Override
    public String placeBid(String user, int itemId, double bidPrice) {
        Item item = items.get(itemId);
        if (item == null || item.sold) {
            return "Cannot place bid: item not found or already sold.";
        }

        if (item.owner.equals(user)) {
            return "You cannot place a bid on your own item.";
        }

        if (bidPrice <= item.getPrice()) {
            return String.format("Bid rejected: your bid of $%.2f is not higher than the current price of $%.2f.",
                bidPrice, item.getPrice());
        }

        // Update the item price to the new bid price
        item.setPrice(bidPrice);

        // Record the bid for bid history tracking
        item.bids.add(new Bid(user, bidPrice));

        return "Bid placed by " + user + " for $" + bidPrice;
    }

    /**
     * Views all the bids placed on an item.
     *
     * @param itemId The ID of the item to view bids for.
     * @return A string representation of all the bids placed on the item.
     */
    @Override
    public String viewBids(int itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            return "Item not found.";
        }

        StringBuilder response = new StringBuilder("Bids for " + item.name + ":\n");
        item.bids.forEach(bid -> response.append(bid.user).append(" - $")
            .append(String.format("%.2f", bid.amount)).append("\n"));

        return response.toString();
    }

    /**
     * Removes an item from the storage.
     *
     * @param user   The name of the user requesting the removal.
     * @param itemId The ID of the item to remove.
     * @return A string message indicating the result of the removal operation.
     */
    @Override
    public String removeItem(String user, int itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            return "Item not found.";
        }

        if (!item.owner.equals(user)) {
            return "Only the item owner can remove it.";
        }

        if (item.getSoldStatus()) {
            return "Item has already been sold and cannot be removed.";
        }

        items.remove(itemId);
        return "Item removed by " + user;
    }

    /**
     * Lists all available items in the storage that have not been sold.
     *
     * @return A map of item IDs to their string representations.
     */
    @Override
    public Map<Integer, String> listItems() {
        Map<Integer, String> availableItems = new HashMap<>();
        for (var entry : items.entrySet()) {
            if (!entry.getValue().sold) {
                availableItems.put(entry.getKey(), entry.getValue().toString());
            }
        }
        return availableItems;
    }

    /**
     * Represents an item in the marketplace with details such as the owner,
     * name, price, and sale status.
     */
    public static class Item {
        String owner;
        String name;
        double price;
        boolean sold;
        List<Bid> bids = new ArrayList<>();

        /**
         * Constructor to create an item.
         *
         * @param owner The owner of the item.
         * @param name  The name of the item.
         * @param price The price of the item.
         */
        public Item(String owner, String name, double price) {
            this.owner = owner;
            this.name = name;
            this.price = price;
            this.sold = false;
        }

        /**
         * Gets the sold status of the item.
         *
         * @return true if the item is sold, false otherwise.
         */
        public boolean getSoldStatus() {
            return this.sold;
        }

        /**
         * Sets the sold status of the item.
         *
         * @param status true if the item is sold, false otherwise.
         */
        public void setSoldStatus(boolean status) {
            this.sold = status;
        }

        /**
         * Gets the price of the item.
         *
         * @return The price of the item.
         */
        public double getPrice() {
            return this.price;
        }

        /**
         * Sets the price of the item.
         *
         * @param price The new price of the item.
         */
        public void setPrice(double price) {
            this.price = price;
        }

        /**
         * Returns a string representation of the item.
         *
         * @return A string representing the item and its details.
         */
        @Override
        public String toString() {
            return name + " (by " + owner + ") - $" + price + (sold ? " [SOLD]" : "");
        }
    }

    /**
     * Represents a bid placed on an item in the auction system.
     *
     * <p>Each bid has a bidder, the item ID it's associated with, and the bid amount.
     * Bids can be compared to determine the highest bid for an item.
     * </p>
     */
    public static class Bid {
        String user;
        double amount;

        /**
         * Constructor to create a bid.
         *
         * @param user   The user placing the bid.
         * @param amount The bid amount.
         */
        public Bid(String user, double amount) {
            this.user = user;
            this.amount = amount;
        }
    }
}
