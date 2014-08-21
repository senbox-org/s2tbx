package org.esa.beam.dataio.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * This factory class returns instances of <code>XmlMetadataParser</code>s that have been previously
 * registered with it.
 *
 */
public class XmlMetadataParserFactory {

    private static Map<Class, XmlMetadataParser> parserMap = new HashMap<Class, XmlMetadataParser>();

    /**
     * Registers a parser instance, attached to the given metadata class, to this factory.
     * @param clazz     The metadata class.
     * @param parser    The parser instance.
     * @param <T>       Generic type for metadata class.
     */
    public static <T extends XmlMetadata> void registerParser(Class clazz, XmlMetadataParser<T> parser) {
        if (!parserMap.containsKey(clazz)) {
            parserMap.put(clazz, parser);
        }
    }

    /**
     * Returns a parser instance for the given metadata class. If no parser was previously registered for
     * the class, it will throw an exception.
     * @param clazz     The metadata class.
     * @param <T>       Generic type for the metadata class.
     * @return          The parser instance.
     * @throws InstantiationException   Exception is thrown if no parser was registered for the input class.
     */
    public static <T extends XmlMetadata> XmlMetadataParser<T> getParser(Class clazz) throws InstantiationException {
        XmlMetadataParser<T> parser;
        if (parserMap.containsKey(clazz)) {
            //noinspection unchecked
            parser = parserMap.get(clazz);
        } else {
            throw new InstantiationException("No parser registered for metadata class " + clazz.getName());
        }
        return parser;
    }
}
