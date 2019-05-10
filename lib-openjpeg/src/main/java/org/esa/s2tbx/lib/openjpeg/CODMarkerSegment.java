package org.esa.s2tbx.lib.openjpeg;

import java.io.DataInputStream;
import java.io.IOException;

public class CODMarkerSegment extends AbstractMarkerSegment {

	private int lcod;
	private int scod;
	private int sgcod_po; // Progression order
	private int sgcod_nl; // Number of layers
	private int sgcod_mct; // Multiple component transformation
	private int spcod_ndl; // Number of decomposition levels
	private int spcod_cw; // Code-blocks width
	private int spcod_ch; // Code-blocks height
	private int spcod_cs; // Code-blocks style
	private int[] spcod_t = new int[1]; // Transformation
	private int[] spcod_ps; // Precinct size
	/** Is the precinct partition used */
	private boolean precinctPartitionIsUsed;

	public CODMarkerSegment() {
	}

	@Override
	public void readData(DataInputStream jp2FileStream) throws IOException {
		// Lcod (marker length)
		this.lcod = jp2FileStream.readUnsignedShort();

		// Scod (block style)
		// We only support wavelet transformed data
		this.scod = jp2FileStream.readUnsignedByte();

		int cstyle = this.scod;
		if ((cstyle & SCOX_PRECINCT_PARTITION) != 0) {
			precinctPartitionIsUsed = true;
			// Remove flag
			cstyle &= ~(SCOX_PRECINCT_PARTITION);
		} else {
			precinctPartitionIsUsed = false;
		}

		// SOP markers

		if ((cstyle & SCOX_USE_SOP) != 0) {
			// SOP markers are used
			// Remove flag
			cstyle &= ~(SCOX_USE_SOP);
		} else {
			// SOP markers are not used
		}

		// EPH markers
		if ((cstyle & SCOX_USE_EPH) != 0) {
			// EPH markers are used
			// Remove flag
			cstyle &= ~(SCOX_USE_EPH);
		} else {
			// EPH markers are not used
		}

		// SGcod
		// Read the progressive order
		this.sgcod_po = jp2FileStream.readUnsignedByte();

		// Read the number of layers
		this.sgcod_nl = jp2FileStream.readUnsignedShort();
		if (this.sgcod_nl <= 0 || this.sgcod_nl > 65535) {
			throw new InvalidContiguousCodestreamException("Number of layers out of " + "range: 1--65535");
		}

		// Multiple component transform
		this.sgcod_mct = jp2FileStream.readUnsignedByte();

		// SPcod
		// decomposition levels
		this.spcod_ndl = jp2FileStream.readUnsignedByte();
		if (this.spcod_ndl > 32) {
			throw new InvalidContiguousCodestreamException("Number of decomposition " + "levels out of range: " + "0--32");
		}

		// Read the code-blocks dimensions
		this.spcod_cw = jp2FileStream.readUnsignedByte();
		this.spcod_ch = jp2FileStream.readUnsignedByte();
		
		// Style of the code-block coding passes
		this.spcod_cs = jp2FileStream.readUnsignedByte();

		// read the filter id
		jp2FileStream.readUnsignedByte();

		this.spcod_ps = new int[this.spcod_ndl + 1];
		if (this.precinctPartitionIsUsed) {
			for (int rl = this.spcod_ndl; rl >= 0; rl--) {
				this.spcod_ps[this.spcod_ndl - rl] = jp2FileStream.readUnsignedByte();
			}
		} else {
			for (int rl = this.spcod_ndl; rl >= 0; rl--) {
				this.spcod_ps[this.spcod_ndl - rl] = IMarkers.PRECINCT_PARTITION_DEF_SIZE;
			}
		}

		this.precinctPartitionIsUsed = true;
	}

	public int getMultipleComponenTransform() {
		return this.sgcod_mct;
	}
	
	@Override
	public String toString() {
		String str = "\n --- COD (" + lcod + " bytes) ---\n";
		str += " Coding style   : ";
		if (scod == 0) {
			str += "Default";
		} else {
			if ((scod & SCOX_PRECINCT_PARTITION) != 0)
				str += "Precints ";
			if ((scod & SCOX_USE_SOP) != 0)
				str += "SOP ";
			if ((scod & SCOX_USE_EPH) != 0)
				str += "EPH ";
			int cb0x = ((scod & SCOX_HOR_CB_PART) != 0) ? 1 : 0;
			int cb0y = ((scod & SCOX_VER_CB_PART) != 0) ? 1 : 0;
			if (cb0x != 0 || cb0y != 0) {
				str += "Code-blocks offset";
				str += "\n Cblk partition : " + cb0x + "," + cb0y;
			}
		}
		str += "\n";
		str += " Cblk style     : ";
		if (spcod_cs == 0) {
			str += "Default";
		} else {
			if ((spcod_cs & 0x1) != 0)
				str += "Bypass ";
			if ((spcod_cs & 0x2) != 0)
				str += "Reset ";
			if ((spcod_cs & 0x4) != 0)
				str += "Terminate ";
			if ((spcod_cs & 0x8) != 0)
				str += "Vert_causal ";
			if ((spcod_cs & 0x10) != 0)
				str += "Predict ";
			if ((spcod_cs & 0x20) != 0)
				str += "Seg_symb ";
		}
		str += "\n";
		str += " Number of levels : " + getNumberOfLevels() + "\n";
		str += " Progress type : "+ getProgressiveOrder() + "\n";
		str += " Num. of layers : " + getNumberOfLayers() + "\n";
		str += " Cblk dimension : " + (1 << (spcod_cw + 2)) + "x" + (1 << (spcod_ch + 2)) + "\n";
		str += " Filter         : " + spcod_t[0] + "\n";
		str += " Multi comp transform : " + getMultipleComponenTransform() + "\n";
		if (spcod_ps != null) {
			str += " Precincts      : ";
			for (int i = 0; i < getBlockCount(); i++) {
				str += (1 << computeBlockWidthExponentOffset(i)) + "x" + (1 << computeBlockHeightExponentOffset(i)) + " ";
			}
		}
		str += "\n";
		return str;
	}

	public int getProgressiveOrder() {
		return this.sgcod_po;
	}

	public int getBlockCount() {
		return this.spcod_ps.length;
	}

	public int computeBlockWidthExponentOffset(int index) {
		return (spcod_ps[index] & 0x000F);
	}

	public int computeBlockHeightExponentOffset(int index) {
		return ((spcod_ps[index] & 0x00F0) >> 4);
	}

	public int getNumberOfLayers() {
		return this.sgcod_nl;
	}

	public int getNumberOfLevels() {
		return this.spcod_ndl;
	}
}
