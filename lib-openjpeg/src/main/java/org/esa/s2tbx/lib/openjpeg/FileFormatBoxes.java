package org.esa.s2tbx.lib.openjpeg;

public interface FileFormatBoxes {

    /** JP2 Box Types */
	public static final int JP2_SIGNATURE_BOX = 0x6a502020;

	public static final int JP2_SIGNATURE_BOX_CONTENT = 0x0D0A870A; // <CR><LF><0x87><LF> (0x0D0A 870A)
	
    public static final int FILE_TYPE_BOX       = 0x66747970;

    public static final int JP2_HEADER_BOX   = 0x6a703268;

    public static final int CONTIGUOUS_CODESTREAM_BOX = 0x6a703263;
    
    public static final int INTELLECTUAL_PROPERTY_BOX = 0x64703269;
    
    public static final int XML_BOX                   = 0x786d6c20;

    public static final int UUID_BOX                  = 0x75756964;

    public static final int UUID_INFO_BOX             = 0x75696e66;

    /** JPX Box Types */
    public static final int ASSOCIATION_BOX             = 0x61736f63;

    /** File Type Fields */
    public static final int FT_BR = 0x6a703220;
}
