package org.esa.s2tbx.dataio.openjp2.types;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class EventManagerPointer extends PointerType {

    public EventManagerPointer(Pointer address) {
        super(address);
    }

    public EventManagerPointer() {
        super();
    }
}