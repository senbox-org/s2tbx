package org.esa.s2tbx.lib.openjpeg;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by jcoravu on 10/5/2019.
 */
public class QCDMarkerSegment extends AbstractMarkerSegment {

    private int lqcd;
    private int sqcd;
    private int[][] spqcd;
    private int qType;
    private int gb;

    public QCDMarkerSegment() {
        this.qType = -1;
        this.gb = -1;
    }

    public int getQuantizationType() {
        if (qType == -1) {
            qType = sqcd & ~(SQCX_GB_MSK << SQCX_GB_SHIFT);
        }
        return qType;
    }

    public int getNumGuardBits() {
        if (gb == -1) {
            gb = (sqcd >> SQCX_GB_SHIFT) & SQCX_GB_MSK;
        }
        return gb;
    }

    public int computeNoQuantizationExponent(int rIndex, int sIndex) {
        return (spqcd[rIndex][sIndex] >> SQCX_EXP_SHIFT) & SQCX_EXP_MASK;
    }

    public int computeExponent(int rIndex, int sIndex) {
        return (spqcd[rIndex][sIndex] >> 11) & 0x1f;
    }

    public double computeMantissa(int rIndex, int sIndex, int exponent) {
        return (-1f - ((float) (spqcd[rIndex][sIndex] & 0x07ff)) / (1 << 11)) / (-1 << exponent);
    }

    public int getResolutionLevels() {
        return this.spqcd.length;
    }

    public int getSubbandsAtResolutionLevel(int index) {
        return this.spqcd[index].length;
    }

    @Override
    public String toString() {
        String str = "\n --- QCDMarkerSegment (" + lqcd + " bytes) ---\n";
        str += " Quantization. type    : ";
        int qt = getQuantizationType();
        if (qt == SQCX_NO_QUANTIZATION) str += "No quantization \n";
        else if (qt == SQCX_SCALAR_DERIVED) str += "Scalar derived\n";
        else if (qt == SQCX_SCALAR_EXPOUNDED) str += "Scalar expounded\n";
        str += " Guard bits     : " + getNumGuardBits() + "\n";

        if (qt == SQCX_NO_QUANTIZATION) {
            str += " Exponents   :\n";
            int exp;
            for (int i = 0; i < getResolutionLevels(); i++) {
                for (int j = 0; j < getSubbandsAtResolutionLevel(i); j++) {
                    if (i == 0 && j == 0) {
                        exp = computeNoQuantizationExponent(0, 0);//(spqcd[0][0] >> SQCX_EXP_SHIFT) & SQCX_EXP_MASK;
                        str += "\tr=0 : " + exp + "\n";
                    } else if (i != 0 && j > 0) {
                        exp = computeNoQuantizationExponent(i, j);//(spqcd[i][j] >> SQCX_EXP_SHIFT) & SQCX_EXP_MASK;
                        str += "\tr=" + i + ",s=" + j + " : " + exp + "\n";
                    }
                }
            }
        } else {
            str += " Exp / Mantissa : \n";
            int exp;
            double mantissa;
            for (int i = 0; i < getResolutionLevels(); i++) {
                for (int j = 0; j < getSubbandsAtResolutionLevel(i); j++) {
                    if (i == 0 && j == 0) {
                        exp = computeExponent(0, 0);//(spqcd[0][0] >> 11) & 0x1f;
                        mantissa = computeMantissa(0, 0, exp);//(-1f - ((float) (spqcd[0][0] & 0x07ff)) / (1 << 11)) / (-1 << exp);
                        str += "\tr=0 : " + exp + " / " + mantissa + "\n";
                    } else if (i != 0 && j > 0) {
                        exp = computeExponent(i, j);//(spqcd[i][j] >> 11) & 0x1f;
                        mantissa = computeMantissa(i, j, exp);//(-1f - ((float) (spqcd[i][j] & 0x07ff)) / (1 << 11)) / (-1 << exp);
                        str += "\tr=" + i + ",s=" + j + " : " + exp + " / " + mantissa + "\n";
                    }
                }
            }
        }

        str += "\n";
        return str;
    }

    @Override
    public void readData(DataInputStream jp2FileStream) throws IOException {
    }

    public void readData(DataInputStream jp2FileStream, int numberOfLevels) throws IOException {
        // Lqcd (length of QCD field)
        this.lqcd = jp2FileStream.readUnsignedShort();

        // Sqcd (quantization style)
        this.sqcd = jp2FileStream.readUnsignedByte();

        int qType = this.getQuantizationType();

        // If the main header is being read set default value of dequantization spec
        switch (qType) {
            case SQCX_NO_QUANTIZATION:
                break;
            case SQCX_SCALAR_DERIVED:
                break;
            case SQCX_SCALAR_EXPOUNDED:
                break;
            default:
                throw new InvalidContiguousCodestreamException("Unknown or " + "unsupported " + "quantization style " + "in Sqcd field, QCD " + "marker main header");
        }

        if (qType == SQCX_NO_QUANTIZATION) {
            int maxrl = numberOfLevels;//((Integer) decSpec.dls.getDefault()).intValue();
            int minb, maxb, hpd;
            int tmp;

            int[][] exp = new int[maxrl + 1][];
            this.spqcd = new int[maxrl + 1][4];

            for (int rl = 0; rl <= maxrl; rl++) { // Loop on resolution levels
                // Find the number of subbands in the resolution level
                if (rl == 0) { // Only the LL subband
                    minb = 0;
                    maxb = 1;
                } else {
                    // Dyadic decomposition
                    hpd = 1;
                    // Adapt hpd to resolution level
                    if (hpd > maxrl - rl) {
                        hpd -= maxrl - rl;
                    } else {
                        hpd = 1;
                    }
                    // Determine max and min subband index
                    minb = 1 << ((hpd - 1) << 1); // minb = 4^(hpd-1)
                    maxb = 1 << (hpd << 1); // maxb = 4^hpd
                }
                // Allocate array for subbands in resolution level
                exp[rl] = new int[maxb];

                for (int j = minb; j < maxb; j++) {
                    tmp = this.spqcd[rl][j] = jp2FileStream.readUnsignedByte();
                    exp[rl][j] = (tmp >> SQCX_EXP_SHIFT) & SQCX_EXP_MASK;
                }
            } // end for rl
        } else {
            int maxrl = (qType == SQCX_SCALAR_DERIVED) ? 0 : numberOfLevels/*((Integer) decSpec.dls.getDefault()).intValue()*/;
            int minb, maxb, hpd;
            int tmp;

            int[][] exp = new int[maxrl + 1][];
            float[][] nStep = new float[maxrl + 1][];
            this.spqcd = new int[maxrl + 1][4];

            for (int rl = 0; rl <= maxrl; rl++) { // Loop on resolution levels
                // Find the number of subbands in the resolution level
                if (rl == 0) { // Only the LL subband
                    minb = 0;
                    maxb = 1;
                } else {
                    // Dyadic decomposition
                    hpd = 1;

                    // Adapt hpd to resolution level
                    if (hpd > maxrl - rl) {
                        hpd -= maxrl - rl;
                    } else {
                        hpd = 1;
                    }
                    // Determine max and min subband index
                    minb = 1 << ((hpd - 1) << 1); // minb = 4^(hpd-1)
                    maxb = 1 << (hpd << 1); // maxb = 4^hpd
                }
                // Allocate array for subbands in resolution level
                exp[rl] = new int[maxb];
                nStep[rl] = new float[maxb];

                for (int j = minb; j < maxb; j++) {
                    tmp = this.spqcd[rl][j] = jp2FileStream.readUnsignedShort();
                    exp[rl][j] = (tmp >> 11) & 0x1f;
                    // NOTE: the formula below does not support more than 5
                    // bits for the exponent, otherwise (-1<<exp) might
                    // overflow (the - is used to be able to represent 2**31)
                    nStep[rl][j] = (-1f - ((float) (tmp & 0x07ff)) / (1 << 11)) / (-1 << exp[rl][j]);
                }
            } // end for rl
        } // end if (qType != SQCX_NO_QUANTIZATION)
    }
}
