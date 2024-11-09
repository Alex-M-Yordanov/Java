package wish.list;

import org.junit.jupiter.api.Test;
import wish.list.command.Command;
import wish.list.command.CommandCreator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommandCreatorTest {

    /**
     * Tests the newCommand method by verifying the command and its arguments.
     */
    @Test
    void testNewCommandSimple() {
        String input = "placeBid item123 100";
        Command result = CommandCreator.newCommand(input);

        assertNotNull(result, "The command should not be null.");
        assertEquals("placeBid", result.command(), "The command should be 'placeBid'.");
        assertArrayEquals(new String[] {"item123", "100"}, result.arguments(),
            "The arguments should be 'item123' and '100'.");
    }

    /**
     * Tests the newCommand method when there are no arguments.
     */
    @Test
    void testNewCommandNoArguments() {
        String input = "listItems";
        Command result = CommandCreator.newCommand(input);

        assertNotNull(result, "The command should not be null.");
        assertEquals("listItems", result.command(), "The command should be 'listItems'.");
        assertEquals(0, result.arguments().length, "There should be no arguments.");
    }

    /**
     * Tests the newCommand method with quoted arguments.
     */
    @Test
    void testNewCommandQuotedArguments() {
        String input = "placeBid \"item 123\" \"100 dollars\"";
        Command result = CommandCreator.newCommand(input);

        assertNotNull(result, "The command should not be null.");
        assertEquals("placeBid", result.command(), "The command should be 'placeBid'.");
        assertArrayEquals(new String[] {"item 123", "100 dollars"}, result.arguments(),
            "The arguments should be 'item 123' and '100 dollars'.");
    }

    /**
     * Tests newCommand with extra spaces.
     */
    @Test
    void testNewCommandExtraSpaces() {
        String input = "placeBid   \"item123\"  100   ";
        Command result = CommandCreator.newCommand(input);

        assertNotNull(result, "The command should not be null.");
        assertEquals("placeBid", result.command(), "The command should be 'placeBid'.");
        assertArrayEquals(new String[] {"item123", "100"}, result.arguments(),
            "The arguments should be 'item123' and '100'.");
    }

    /**
     * Tests newCommand with empty input.
     */
    @Test
    void testNewCommandEmptyInput() {
        String input = "";
        Command result = CommandCreator.newCommand(input);

        assertNotNull(result, "The command should not be null.");
        assertEquals("", result.command(), "The command should be empty.");
        assertEquals(0, result.arguments().length, "There should be no arguments.");
    }
}
