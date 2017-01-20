package org.esa.snap.utils;

/**
 * @author Jean Coravu.
 */
public class StringHelper {

    /**
     * Tests if the input string is null or empty.
     *
     * @param value the input string to check
     * @return {@code true}  if the input string is null or empty; {@code false} otherwise.
     */
    public static boolean isNullOrEmpty(String value) {
        return (value == null || value.trim().length() == 0);
    }

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

    public static boolean containsIgnoreCase(String input, String value) {
        return (input != null && value != null && input.toLowerCase().contains(value.toLowerCase()));
    }
}
