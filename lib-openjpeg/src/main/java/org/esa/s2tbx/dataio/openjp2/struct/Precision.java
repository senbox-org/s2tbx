package org.esa.s2tbx.dataio.openjp2.struct;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class Precision extends Structure {
    /** C type : OPJ_UINT32 */
    public int prec;
    /** C type : opj_precision_mode */
    public int mode;

    public Precision() {
        super();
    }
    /**
     * @param prec C type : OPJ_UINT32<br>
     * @param mode C type : opj_precision_mode
     */
    public Precision(int prec, int mode) {
        super();
        this.prec = prec;
        this.mode = mode;
    }
    public Precision(Pointer peer) {
        super(peer);
    }

    protected List<? > getFieldOrder() {
        return Arrays.asList("prec", "mode");
    }

    public static class ByReference extends Precision implements Structure.ByReference { }
    public static class ByValue extends Precision implements Structure.ByValue { }
}
