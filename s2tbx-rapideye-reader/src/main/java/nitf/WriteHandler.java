package nitf;

/**
 * The WriteHandler class is essentially an interface for writing to an
 * IOInterface, but is an abstract class because under the covers it sets up
 * some native code that will facilitate the callback to your write method.
 */
public abstract class WriteHandler extends DestructibleObject
{

    /**
     * Default constructor
     */
    public WriteHandler()
    {
        construct();
    }

    private native void construct();

    /**
     * Write to the given IOHandle. This is user-defined.
     * 
     * @param io
     * @throws NITFException
     */
    public abstract void write(IOInterface io) throws NITFException;

    @Override
    protected MemoryDestructor getDestructor()
    {
        return new Destructor();
    }

    private static class Destructor implements MemoryDestructor
    {
        public native boolean destructMemory(long nativeAddress);
    }

}
