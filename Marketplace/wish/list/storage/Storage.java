package wish.list.storage;

import java.util.Map;

/**
 * The {@code Storage} interface defines the contract for a storage system
 * used to manage items in a marketplace. It includes methods for adding,
 * buying, bidding on, removing, and listing items. Implementations of this
 * interface should provide the logic for these operations.
 * <p>
 * This interface allows users to interact with items in the marketplace
 * by providing basic CRUD (Create, Read, Update, Delete) operations.
 * </p>
 */
public interface Storage {

    /**
     * Adds a new item to the storage.
     *
     * @param user     The name of the user who owns the item.
     * @param itemName The name of the item to add.
     * @param price    The price of the item.
     * @return The unique ID assigned to the newly added item.
     * @throws IllegalArgumentException if the item name is empty or the price is not greater than zero.
     */
    int addItem(String user, String itemName, double price);

    /**
     * Buys an item and marks it as sold.
     *
     * @param user   The name of the user buying the item.
     * @param itemId The ID of the item to buy.
     * @return A string message indicating the result of the buy operation.
     */
    String buyItem(String user, int itemId);

    /**
     * Places a bid on an item if the bid is higher than the current price.
     *
     * @param user     The name of the user placing the bid.
     * @param itemId   The ID of the item to bid on.
     * @param bidPrice The bid amount.
     * @return A string message indicating the result of the bidding operation.
     */
    String placeBid(String user, int itemId, double bidPrice);

    /**
     * Views all the bids placed on an item.
     *
     * @param itemId The ID of the item to view bids for.
     * @return A string representation of all the bids placed on the item.
     */
    String viewBids(int itemId);

    /**
     * Removes an item from the storage.
     *
     * @param user   The name of the user requesting the removal.
     * @param itemId The ID of the item to remove.
     * @return A string message indicating the result of the removal operation.
     */
    String removeItem(String user, int itemId);

    /**
     * Lists all available items in the storage that have not been sold.
     *
     * @return A map of item IDs to their string representations.
     */
    Map<Integer, String> listItems();
}
