package org.esa.s2tbx.dataio.gdal.internal;

/**
 * Created by kraftek on 11/3/2016.
 */
public class BufferTypeDescriptor {
    public int precision;
    public boolean signed;
    public int bandDataType;
    public int dataBufferType;

    public BufferTypeDescriptor(int precision, boolean signed, int bandDataType, int dataBufferType) {
        this.precision = precision;
        this.signed = signed;
        this.bandDataType = bandDataType;
        this.dataBufferType = dataBufferType;
    }
}
