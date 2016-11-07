package org.esa.snap.utils;

/**
 * @author Jean Coravu.
 */
public class StringHelper {

    /**
     * Tests if this string starts with the specified prefix, ignoring the case sensitive.
     *
     * @param inputValue the input string to test
     * @param prefix the prefix
     * @return {@code true} if the input string is a prefix; {@code false} otherwise.
     */
    public static boolean startsWithIgnoreCase(String inputValue, String prefix) {
        return inputValue.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * Tests if this string ends with any of the given suffixes, ignoring the case sensitive.
     *
     * @param input the input string to test
     * @param suffixes the list of suffixes
     * @return {@code true} if the input string is a prefix; {@code false} otherwise.
     */
    public static boolean endsWithIgnoreCase(String input, String...suffixes) {
        boolean found = true;
        String lowerInput = input.toLowerCase();
        if (suffixes != null && suffixes.length > 0) {
            for (String suffix : suffixes) {
                found = lowerInput.endsWith(suffix.toLowerCase());
                if (found)
                    break;
            }
        }
        return found;
    }

    /**
     * Returns true if and only if the first string contains the second string, ignoring the case.
     *
     * @param input the input string to be inspected
     * @param value the string to search for
     */
    public static boolean containsIgnoreCase(String input, String value) {
        return (input != null && value != null && input.toLowerCase().contains(value.toLowerCase()));
    }

    public static int indexOfIgnoreCase(String input, String value) {
        return indexOfIgnoreCase(input, value, 0);
    }

    public static int indexOfIgnoreCase(String input, String value, int fromIndex) {
        if (input == null || value == null || fromIndex < 0 || fromIndex >= input.length())
            return -1;
        return input.toLowerCase().indexOf(value.toLowerCase(), fromIndex);
    }
}
