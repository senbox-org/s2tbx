package org.esa.s2tbx.lib.openjpeg;

public interface IMarkers {

    // ----> Delimiting markers and marker segments <----

    /** Start of codestream (SOC): 0xFF4F */
    public final static short SOC = (short)0xff4f;

    /** Start of tile-part (SOT): 0xFF90 */
    public final static short SOT = (short)0xff90;

    /** Start of data (SOD): 0xFF93 */
    public final static short SOD = (short)0xff93;

    /** End of codestream (EOC): 0xFFD9 */
    public final static short EOC = (short)0xffd9;

    // ----> Fixed information marker segments <----

    // ** SIZ marker **

    /** SIZ marker (Image and tile size): 0xFF51 */
    public final static short SIZ = (short)0xff51;

    /** No special capabilities (baseline) in codestream, in Rsiz field of SIZ
     * marker: 0x00. All flag bits are turned off */
    public final static int RSIZ_BASELINE = 0x00;
    /** Error resilience marker flag bit in Rsiz field in SIZ marker: 0x01 */
    public final static int RSIZ_ER_FLAG = 0x01;
    /** ROI present marker flag bit in Rsiz field in SIZ marker: 0x02 */
    public final static int RSIZ_ROI = 0x02;
    /** Component bitdepth bits in Ssiz field in SIZ marker: 7 */
    public final static int SSIZ_DEPTH_BITS = 7;
    /** The maximum number of component bitdepth */
    public static final int MAX_COMP_BITDEPTH = 38;


    // ----> Functional marker segments <----

    // ** COD/COC marker **

    /** Coding style default (COD): 0xFF52 */
    public final static short COD = (short)0xff52;

    /** Coding style component (COC): 0xFF53 */
    public final static short COC = (short)0xff53;

    /** Precinct used flag */
    public final static int SCOX_PRECINCT_PARTITION = 1;
    /** Use start of packet marker */
    public final static int SCOX_USE_SOP = 2;
    /** Use end of packet header marker */
    public final static int SCOX_USE_EPH = 4;
    /** Horizontal code-block partition origin is at x=1 */
    public final static int SCOX_HOR_CB_PART = 8;
    /** Vertical code-block partition origin is at y=1 */
    public final static int SCOX_VER_CB_PART = 16;
    /** The default size exponent of the precincts */
    public final static int PRECINCT_PARTITION_DEF_SIZE = 0xffff;

    // ** RGN marker segment **
    /** Region-of-interest (RGN): 0xFF5E */
    public final static short RGN = (short)0xff5e;

    /** Implicit (i.e. max-shift) ROI flag for Srgn field in RGN marker
        segment: 0x00 */
    public final static int SRGN_IMPLICIT = 0x00;

    // ** QCDMarkerSegment/QCC markers **

    /** Quantization default (QCDMarkerSegment): 0xFF5C */
    public final static short QCD = (short)0xff5c;

    /** Quantization component (QCC): 0xFF5D */
    public final static short QCC = (short)0xff5d;

    /** Guard bits shift in SQCX field: 5 */
    public final static int SQCX_GB_SHIFT = 5;
    /** Guard bits mask in SQCX field: 7 */
    public final static int SQCX_GB_MSK = 7;
    /** No quantization (i.e. embedded reversible) flag for Sqcd or Sqcc
     * (Sqcx) fields: 0x00. */
    public final static int SQCX_NO_QUANTIZATION = 0x00;
    /** Scalar derived (i.e. LL values only) quantization flag for Sqcd or
     * Sqcc (Sqcx) fields: 0x01. */
    public final static int SQCX_SCALAR_DERIVED = 0x01;
    /** Scalar expounded (i.e. all values) quantization flag for Sqcd or Sqcc
     * (Sqcx) fields: 0x02. */
    public final static int SQCX_SCALAR_EXPOUNDED = 0x02;
    /** Exponent shift in SPQCX when no quantization: 3 */
    public final static int SQCX_EXP_SHIFT = 3;
    /** Exponent bitmask in SPQCX when no quantization: 3 */
    public final static int SQCX_EXP_MASK = (1<<5)-1;
    /** The "SOP marker segments used" flag within Sers: 1 */
    public final static int ERS_SOP = 1;
    /** The "segmentation symbols used" flag within Sers: 2 */
    public final static int ERS_SEG_SYMBOLS = 2;

    // ** Progression order change **
    public final static short POC = (short)0xff5f;

    // ----> Pointer marker segments <----

    /** Tile-part lengths (TLM): 0xFF55 */
    public final static short TLM = (short)0xff55;

    /** Packet length, main header (PLM): 0xFF57 */
    public final static short PLM = (short)0xff57;

    /** Packet length, tile-part header (PLT): 0xFF58 */
    public final static short PLT = (short)0xff58;

    /** Packed packet headers, main header (PPM): 0xFF60 */
    public final static short PPM = (short)0xff60;

    /** Packed packet headers, tile-part header (PPT): 0xFF61 */
    public final static short PPT = (short)0xff61;

    /** Maximum length of PPT marker segment */
    public final static int MAX_LPPT = 65535;

    /** Maximum length of PPM marker segment */
    public final static int MAX_LPPM = 65535;

//
//    // ----> In bit stream markers and marker segments <----
//
//    /** Start pf packet (SOP): 0xFF91 */
//    public final static short SOP = (short)0xff91;
//
//    /** Length of SOP marker (in bytes) */
//    public final static short SOP_LENGTH = 6;
//
//    /** End of packet header (EPH): 0xFF92 */
//    public final static short EPH = (short)0xff92;
//
//    /** Length of EPH marker (in bytes) */
//    public final static short EPH_LENGTH = 2;

    // ----> Informational marker segments <----

    /** Component registration (CRG): 0xFF63 */
    public final static short CRG = (short)0xff63;

    /** Comment (COM): 0xFF64 */
    public final static short COM = (short)0xff64;

//    /** General use registration value (COM): 0x0001 */
//    public final static short RCOM_GEN_USE = (short)0x0001;
}
