package wish.list;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wish.list.command.Command;
import wish.list.command.CommandExecutor;
import wish.list.storage.InMemoryStorage;
import wish.list.storage.Storage;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CommandExecutorTest {

    private static final int INVALID_ID = 999;
    private static final int ID = 1;
    private static final double PRICE1 = 6.0;
    private static final double PRICE2 = 5.0;
    private static final double PRICE3 = 4.0;

    @Mock
    private Storage storage;

    @InjectMocks
    private CommandExecutor commandExecutor;

    private AutoCloseable mocks;

    // Initialize mocks before each test
    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    // Closing mocks after each test preventing potential memory leaks
    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testListItemsWhenNoItems() {
        when(storage.listItems()).thenReturn(Map.of());

        String result = commandExecutor.execute(new Command("list-items", new String[] {}));

        assertEquals("No items currently listed.", result);
    }

    @Test
    void testListItemsWhenItemsExist() {
        Map<Integer, String> items = Map.of(
            0, "car (by alice) - $4.0",
            1, "bike (by bob) - $10.0"
        );
        when(storage.listItems()).thenReturn(items);

        String result = commandExecutor.execute(new Command("list-items", new String[] {}));

        assertTrue(result.contains("Items for sale:"));
        assertTrue(result.contains("[0] car (by alice) - $4.0"));
        assertTrue(result.contains("[1] bike (by bob) - $10.0"));
    }

    @Test
    void testListItemWithInvalidArgumentCount() {
        String result = commandExecutor.execute(new Command("list-item", new String[] {"alice", "car"}));

        String expectedMessage = "Invalid count of arguments: \"list-item\" expects 3 arguments. " +
            "Example: \"list-item <username> <item_name> <price>\"";
        assertEquals(expectedMessage, result);
    }

    @Test
    void testListItemWithInvalidPriceFormat() {
        String result = commandExecutor.execute(new Command("list-item", new String[] {"alice", "car", "free"}));

        assertEquals("Invalid price: must be a number.", result);
    }

    @Test
    void testListItem() {
        when(storage.addItem(anyString(), anyString(), anyDouble())).thenReturn(1);

        String result = commandExecutor.execute(new Command("list-item", new String[] {"alice", "car", "4.0"}));

        assertEquals("Item listed with ID 1 by alice for $4.00", result);
    }

    @Test
    void testBuyItemWhenItemExistsAndIsNotSold() {
        InMemoryStorage.Item item = new InMemoryStorage.Item("alice", "car", PRICE3);
        item.setSoldStatus(false);
        when(storage.buyItem(eq("alice"), eq(1))).thenReturn("Item bought by alice for $4.00");

        String result = commandExecutor.execute(new Command("buy-item", new String[] {"alice", "1"}));

        assertEquals("Item bought by alice for $4.00", result);
    }

    @Test
    void testBuyItemWithInvalidArgumentCount() {
        String result = commandExecutor.execute(new Command("buy-item", new String[] {"alice"}));

        String expectedMessage = "Invalid count of arguments: \"buy-item\" expects 2 arguments. " +
            "Example: \"buy-item <username> <item_id>\"";
        assertEquals(expectedMessage, result);
    }

    @Test
    void testBuyItemWithInvalidItemIdFormat() {
        String result = commandExecutor.execute(new Command("buy-item", new String[] {"alice", "abc"}));

        assertEquals("Invalid item ID: must be an integer.", result);
    }

    @Test
    void testBuyItemWhenItemAlreadySold() {
        InMemoryStorage.Item item = new InMemoryStorage.Item("alice", "car", PRICE3);
        item.setSoldStatus(true);
        when(storage.buyItem(eq("bob"), eq(1))).thenReturn("Item already sold.");

        String result = commandExecutor.execute(new Command("buy-item", new String[] {"bob", "1"}));

        assertEquals("Item already sold.", result);
    }

    @Test
    void testBuyItemWhenItemNotFound() {
        when(storage.buyItem(eq("alice"), eq(INVALID_ID))).thenReturn("Item not found.");

        String result = commandExecutor.execute(new Command("buy-item", new String[] {"alice", "999"}));

        assertEquals("Item not found.", result);
    }

    @Test
    void testBidItemWhenBidderIsTheOwner() {
        when(storage.placeBid(eq("alice"), eq(1), eq(PRICE2))).thenReturn("You cannot place a bid on your own item.");

        String result = commandExecutor.execute(new Command("bid-item", new String[] {"alice", "1", "5.0"}));

        assertEquals("You cannot place a bid on your own item.", result);
    }

    @Test
    void testBidItemWhenBidIsEqualToCurrentPrice() {

        when(storage.placeBid(eq("bob"), eq(1), eq(PRICE2))).thenReturn("Bid accepted by bob for $5.00");

        String result = commandExecutor.execute(new Command("bid-item", new String[] {"bob", "1", "5.0"}));

        assertEquals("Bid accepted by bob for $5.00", result);
    }

    @Test
    void testBidItemWithInvalidArgumentCount() {
        String result = commandExecutor.execute(new Command("bid-item", new String[] {"alice", "1"}));

        String expectedMessage =
            "Invalid count of arguments: \"bid-item\" expects 3 arguments. " +
                "Example: \"bid-item <username> <item_id> <bid_price>\"";
        assertEquals(expectedMessage, result);
    }

    @Test
    void testBidItemWithInvalidItemIdFormat() {
        String result = commandExecutor.execute(new Command("bid-item", new String[] {"alice", "abc", "10.00"}));

        assertEquals("Invalid input: item ID must be an integer and bid price a number.", result);
    }

    @Test
    void testBidItemWithInvalidBidPriceFormat() {
        String result = commandExecutor.execute(new Command("bid-item", new String[] {"alice", "1", "ten"}));

        assertEquals("Invalid input: item ID must be an integer and bid price a number.", result);
    }

    @Test
    void testBidItemWhenBidIsLowerThanCurrentPrice() {

        when(storage.placeBid(eq("bob"), eq(1), eq(PRICE3))).thenReturn(
            "Bid rejected: your bid of $4.00 is not higher than the current price of $5.00");

        String result = commandExecutor.execute(new Command("bid-item", new String[] {"bob", "1", "4.0"}));

        assertEquals("Bid rejected: your bid of $4.00 is not higher than the current price of $5.00", result);
    }

    @Test
    void testBidItemWhenBidIsHigherThanCurrentPrice() {

        when(storage.placeBid(eq("bob"), eq(1), eq(PRICE1))).thenReturn("Bid placed by bob for $6.00");

        String result = commandExecutor.execute(new Command("bid-item", new String[] {"bob", "1", "6.0"}));

        assertEquals("Bid placed by bob for $6.00", result);
    }

    @Test
    void testRemoveItemWhenOwnerRemovesItem() {

        when(storage.removeItem(eq("alice"), eq(1))).thenReturn("Item removed by alice");

        String result = commandExecutor.execute(new Command("remove-item", new String[] {"alice", "1"}));

        assertEquals("Item removed by alice", result);
    }

    @Test
    void testRemoveItemWhenNonOwnerAttemptsRemoval() {

        when(storage.removeItem(eq("bob"), eq(1))).thenReturn("Only the item owner can remove it.");

        String result = commandExecutor.execute(new Command("remove-item", new String[] {"bob", "1"}));

        assertEquals("Only the item owner can remove it.", result);
    }

    @Test
    void testRemoveItemWhenItemDoesNotExist() {

        when(storage.removeItem(eq("alice"), eq(INVALID_ID))).thenReturn("Item not found.");

        String result = commandExecutor.execute(new Command("remove-item", new String[] {"alice", "999"}));

        assertEquals("Item not found.", result);
    }

    @Test
    void testRemoveItemWithInvalidArgumentCount() {
        String result = commandExecutor.execute(new Command("remove-item", new String[] {"alice"}));

        String expectedMessage =
            "Invalid count of arguments: \"remove-item\" expects 2 arguments. " +
                "Example: \"remove-item <username> <item_id>\"";
        assertEquals(expectedMessage, result);
    }

    @Test
    void testRemoveItemWithInvalidItemIdFormat() {
        String result = commandExecutor.execute(new Command("remove-item", new String[] {"alice", "abc"}));

        assertEquals("Invalid item ID: must be an integer.", result);
    }

    @Test
    void testViewBidsWhenBidsExist() {
        when(storage.viewBids(ID)).thenReturn("bob - $15.0\ncharlie - $20.0");

        String result = commandExecutor.execute(new Command("view-bids", new String[] {String.valueOf(ID)}));

        assertEquals("bob - $15.0\ncharlie - $20.0", result);
    }

    @Test
    void testViewBidsWhenNoBidsExist() {
        when(storage.viewBids(ID)).thenReturn("No bids placed on this item.");

        String result = commandExecutor.execute(new Command("view-bids", new String[] {String.valueOf(ID)}));

        assertEquals("No bids placed on this item.", result);
    }

    @Test
    void testViewBidsWhenItemDoesNotExist() {
        when(storage.viewBids(INVALID_ID)).thenReturn("Item not found.");

        String result = commandExecutor.execute(new Command("view-bids", new String[] {String.valueOf(INVALID_ID)}));

        assertEquals("Item not found.", result);
    }

    @Test
    void testViewBidsWithInvalidArgumentCount() {
        String result = commandExecutor.execute(new Command("view-bids", new String[] {}));

        String expectedMessage = "Invalid count of arguments: \"view-bids\" expects 1 arguments. " +
            "Example: \"view-bids <item_id>\"";
        assertEquals(expectedMessage, result);
    }

    @Test
    void testViewBidsWithInvalidItemIdFormat() {
        String result = commandExecutor.execute(new Command("view-bids", new String[] {"abc"}));

        assertEquals("Invalid item ID: must be an integer.", result);
    }

    @Test
    void testExecuteWithUnknownCommand() {
        String result = commandExecutor.execute(new Command("unknown-command", new String[] {}));

        assertEquals("Unknown command", result);
    }
}
