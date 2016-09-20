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
}
