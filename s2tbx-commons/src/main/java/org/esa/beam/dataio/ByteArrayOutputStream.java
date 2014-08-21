package org.esa.beam.dataio;

/**
 * This class is just a simple variation of <code>ByteArrayOutputStream</code>.
 * The difference consists in the way the internal buffer is returned.
 * In <code>java.io.ByteArrayOutputStream</code>, a copy (i.e. new array instance) of the buffer is returned.
 * This can lead, if called very frequently, to unnecessary memory allocations.
 * In this class, a reference to the internal buffer is returned.
 *
 * @see java.io.ByteArrayOutputStream
 * @author  Cosmin Cara
 */
public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {
    @Override
    public synchronized byte[] toByteArray() {
        return buf;
    }
}
