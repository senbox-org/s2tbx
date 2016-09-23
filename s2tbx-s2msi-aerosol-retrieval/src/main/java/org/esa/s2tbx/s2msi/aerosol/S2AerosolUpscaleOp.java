package org.esa.s2tbx.s2msi.aerosol;

import org.esa.snap.core.gpf.Operator;
import org.esa.snap.core.gpf.OperatorException;
import org.esa.snap.core.gpf.OperatorSpi;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 22.09.2016
 * Time: 14:29
 *
 * @author olafd
 */
public class S2AerosolUpscaleOp extends Operator {

    // todo: implement

    @Override
    public void initialize() throws OperatorException {

    }

    public static class Spi extends OperatorSpi {
        public Spi() {
            super(S2AerosolUpscaleOp.class);
        }
    }
}
