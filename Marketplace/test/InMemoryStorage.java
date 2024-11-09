package wish.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wish.list.storage.InMemoryStorage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryStorageTest {

    private static final int INVALID_ID = 999;
    private static final double PRICE1 = 10.0;
    private static final double PRICE2 = 15.0;
    private static final double PRICE3 = 5.0;

    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryStorage();
    }

    @Test
    void testBuyItemSuccessfully() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        String result = storage.buyItem("bob", itemId);
        assertEquals("Item bought by bob for $10.0", result, "The item should be bought successfully.");

        InMemoryStorage.Item item = storage.getItem(itemId);
        assertTrue(item.getSoldStatus(), "The item should be marked as sold.");
    }

    @Test
    void testBuyItemWhenItemDoesNotExist() {
        String result = storage.buyItem("bob", INVALID_ID);
        assertEquals("Item not found.", result, "Buying a non-existent item should return 'Item not found.'");
    }

    @Test
    void testBuyItemWhenItemIsAlreadySold() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        storage.buyItem("bob", itemId);
        String result = storage.buyItem("charlie", itemId);
        assertEquals("Item already sold.", result,
            "Trying to buy an item that has already been sold should return 'Item already sold.'");
    }

    @Test
    void testAddItemWhenPriceIsNegative() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storage.addItem("alice", "car", -PRICE1);
        });
        assertEquals("Price must be greater than 0.", exception.getMessage());
    }

    @Test
    void testAddItemWhenPriceIsZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storage.addItem("alice", "car", 0.0);
        });
        assertEquals("Price must be greater than 0.", exception.getMessage());
    }

    @Test
    void testAddItemWhenItemNameIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storage.addItem("alice", null, PRICE1);
        });
        assertEquals("Item name cannot be empty.", exception.getMessage());
    }

    @Test
    void testAddItemWhenItemNameIsEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storage.addItem("alice", "", PRICE1);
        });
        assertEquals("Item name cannot be empty.", exception.getMessage());
    }

    @Test
    void testAddItemWhenItemNameIsWhitespace() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storage.addItem("alice", "  ", PRICE1);
        });
        assertEquals("Item name cannot be empty.", exception.getMessage());
    }

    @Test
    void testAddItemWhenItemAddedSuccessfully() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        assertTrue(itemId >= 0, "Item should be added successfully");
    }

    @Test
    void testAddItemWhenItemNameIsOnlyWhitespace() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            storage.addItem("alice", "    ", PRICE1);
        });
        assertEquals("Item name cannot be empty.", exception.getMessage(),
            "Item name with whitespace should throw an exception.");
    }

    @Test
    void testAddMultipleItems() {
        int itemId1 = storage.addItem("alice", "car", PRICE1);
        int itemId2 = storage.addItem("bob", "bike", PRICE2);

        assertNotEquals(itemId1, itemId2, "Each item should have a unique ID");
    }

    @Test
    void testGetItemWhenItemDoesNotExist() {
        InMemoryStorage.Item item = storage.getItem(INVALID_ID);
        assertNull(item, "Item with the given ID should not exist.");
    }

    @Test
    void testRemoveItemSuccessfully() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        String result = storage.removeItem("alice", itemId);
        assertEquals("Item removed by alice", result, "The item should be removed successfully.");
    }

    @Test
    void testRemoveItemWhenItemDoesNotExist() {
        String result = storage.removeItem("alice", INVALID_ID);
        assertEquals("Item not found.", result, "Trying to remove a non-existent item should return 'Item not found.'");
    }

    @Test
    void testRemoveItemWhenItemIsSold() {
        int itemId = storage.addItem("alice", "car", PRICE1);

        InMemoryStorage.Item item = storage.getItem(itemId);
        item.setSoldStatus(true);

        String result = storage.removeItem("alice", itemId);
        assertEquals("Item has already been sold and cannot be removed.", result);
    }

    @Test
    void testRemoveItemWhenNonOwnerAttemptsRemoval() {
        int itemId = storage.addItem("alice", "car", PRICE1);

        String result = storage.removeItem("bob", itemId);
        assertEquals("Only the item owner can remove it.", result, "Non-owner should not be able to remove the item.");
    }

    @Test
    void testListItemsWhenNoItemsExist() {
        Map<Integer, String> items = storage.listItems();
        assertTrue(items.isEmpty(), "There should be no items in the storage.");
    }

    @Test
    void testPlaceBidWhenBidIsLowerThanCurrentPrice() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        String result = storage.placeBid("bob", itemId, PRICE3);
        assertEquals("Bid rejected: your bid of $5.00 is not higher than the current price of $10.00.", result,
            "The bid should be rejected if it's lower than the current price.");
    }

    @Test
    void testPlaceBidSuccessfully() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        String result = storage.placeBid("bob", itemId, PRICE2);
        assertEquals("Bid placed by bob for $15.0", result, "The bid should be placed successfully.");

        InMemoryStorage.Item item = storage.getItem(itemId);
        assertEquals(PRICE2, item.getPrice(), "The item price should be updated to the bid price.");
    }

    @Test
    void testPlaceBidOnOwnItem() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        String result = storage.placeBid("alice", itemId, PRICE2);
        assertEquals("You cannot place a bid on your own item.", result,
            "The owner should not be able to place a bid on their own item.");
    }

    @Test
    void testPlaceBidOnSoldItem() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        storage.buyItem("bob", itemId);

        String result = storage.placeBid("charlie", itemId, PRICE2);
        assertEquals("Cannot place bid: item not found or already sold.", result,
            "Bids cannot be placed on sold items.");
    }

    @Test
    void testPlaceBidWhenItemIsNull() {
        String result = storage.placeBid("alice", INVALID_ID, PRICE1);

        assertEquals("Cannot place bid: item not found or already sold.", result);
    }

    @Test
    void testViewBidsWhenNoBidsExist() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        String result = storage.viewBids(itemId);
        assertEquals("Bids for car:\n", result, "If no bids have been placed, it should show the default message.");
    }

    @Test
    void testViewBidsWhenItemIsNull() {
        String result = storage.viewBids(INVALID_ID);

        assertEquals("Item not found.", result);
    }

    @Test
    void testViewBidsWhenBidsExist() {
        int itemId = storage.addItem("alice", "car", PRICE1);
        storage.placeBid("bob", itemId, PRICE2);
        storage.placeBid("charlie", itemId, PRICE2 + PRICE3);

        String result = storage.viewBids(itemId);
        assertTrue(result.contains("bob - $15.00"), "The bid placed by Bob should be visible.");
        assertTrue(result.contains("charlie - $20.00"), "The bid placed by Charlie should be visible.");
    }

    @Test
    void testListItemsWithSomeSold() {
        int itemId1 = storage.addItem("alice", "car", PRICE1);
        int itemId2 = storage.addItem("bob", "bike", PRICE2);
        storage.buyItem("charlie", itemId1);

        Map<Integer, String> items = storage.listItems();
        assertEquals(1, items.size(), "Only one item should be listed (the bike).");
        assertTrue(items.containsKey(itemId2), "The bike should be in the list of available items.");
    }

    @Test
    void testToStringWhenItemIsNotSold() {
        InMemoryStorage.Item item = new InMemoryStorage.Item("alice", "bicycle", PRICE1);

        String expectedOutput = "bicycle (by alice) - $10.0";
        assertEquals(expectedOutput, item.toString());
    }

    @Test
    void testToStringWhenItemIsSold() {
        InMemoryStorage.Item item = new InMemoryStorage.Item("alice", "bicycle", PRICE1);
        item.setSoldStatus(true);

        String expectedOutput = "bicycle (by alice) - $10.0 [SOLD]";
        assertEquals(expectedOutput, item.toString());
    }

}
