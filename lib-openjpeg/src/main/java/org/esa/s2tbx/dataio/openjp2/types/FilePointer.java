package org.esa.s2tbx.dataio.openjp2.types;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class FilePointer extends PointerType {

    public FilePointer(Pointer address) {
        super(address);
    }

    public FilePointer() {
        super();
    }
}
