package org.esa.s2tbx.lib.openjpeg;

import java.io.DataInputStream;
import java.io.IOException;

public class SIZMarkerSegment extends AbstractMarkerSegment {

	private int lsiz;
	private int rsiz;
	private int xsiz;
	private int ysiz;
	private int x0siz;
	private int y0siz;
	private int xtsiz;
	private int ytsiz;
	private int xt0siz;
	private int yt0siz;
	private int csiz;
	private int[] ssiz;
	private int[] xrsiz;
	private int[] yrsiz;

	/** Component widths */
	private int[] compWidth = null;
	/** Maximum width among all components */
	private int maxCompWidth = -1;
	/** Component heights */
	private int[] compHeight = null;

	public SIZMarkerSegment() {
	}
	
	@Override
	public void readData(DataInputStream jp2FileStream) throws IOException {
		// Read the length of SIZ marker segment (Lsiz)
		this.lsiz = jp2FileStream.readUnsignedShort();

		// Read the capability of the codestream (Rsiz)
		this.rsiz = jp2FileStream.readUnsignedShort();
		if (this.rsiz > 2) {
			throw new Error("Codestream capabilities not JPEG 2000 - Part I" + " compliant");
		}

		// Read image size
		this.xsiz = jp2FileStream.readInt();
		this.ysiz = jp2FileStream.readInt();
		if (this.xsiz <= 0 || this.ysiz <= 0) {
			throw new IOException("JJ2000 does not support images whose " + "width and/or height not in the " + "range: 1 -- (2^31)-1");
		}

		// Read image offset
		this.x0siz = jp2FileStream.readInt();
		this.y0siz = jp2FileStream.readInt();
		if (this.x0siz < 0 || this.y0siz < 0) {
			throw new IOException("JJ2000 does not support images offset " + "not in the range: 0 -- (2^31)-1");
		}

		// Read size of tile
		this.xtsiz = jp2FileStream.readInt();
		this.ytsiz = jp2FileStream.readInt();
		if (this.xtsiz <= 0 || this.ytsiz <= 0) {
			throw new IOException("JJ2000 does not support tiles whose " + "width and/or height are not in  "
					+ "the range: 1 -- (2^31)-1");
		}

		// Read upper-left tile offset
		this.xt0siz = jp2FileStream.readInt();
		this.yt0siz = jp2FileStream.readInt();
		if (this.xt0siz < 0 || this.yt0siz < 0) {
			throw new IOException("JJ2000 does not support tiles whose " + "offset is not in  " + "the range: 0 -- (2^31)-1");
		}

		// Read number of components and initialize related arrays
		int nComp = this.csiz = jp2FileStream.readUnsignedShort();
		if (nComp < 1 || nComp > 16384) {
			throw new IllegalArgumentException("Number of component out of " + "range 1--16384: " + nComp);
		}

		this.ssiz = new int[nComp];
		this.xrsiz = new int[nComp];
		this.yrsiz = new int[nComp];

		// Read bit-depth and down-sampling factors of each component
		for (int i = 0; i < nComp; i++) {
			this.ssiz[i] = jp2FileStream.readUnsignedByte();
			this.xrsiz[i] = jp2FileStream.readUnsignedByte();
			this.yrsiz[i] = jp2FileStream.readUnsignedByte();
		}
	}
	
	public int getCompImgWidth(int c) {
		if (compWidth == null) {
			compWidth = new int[csiz];
			for (int cc = 0; cc < csiz; cc++) {
				compWidth[cc] = (int) (Math.ceil((xsiz) / (double) xrsiz[cc])
						- Math.ceil(x0siz / (double) xrsiz[cc]));
			}
		}
		return compWidth[c];
	}

	public int getCompImgHeight(int c) {
		if (compHeight == null) {
			compHeight = new int[csiz];
			for (int cc = 0; cc < csiz; cc++) {
				compHeight[cc] = (int) (Math.ceil((ysiz) / (double) yrsiz[cc])
						- Math.ceil(y0siz / (double) yrsiz[cc]));
			}
		}
		return compHeight[c];
	}

	public int computeNumTiles() {
		return computeNumTilesX() * computeNumTilesY();
	}

	public int computeNumTilesY() {
			return ((ysiz - yt0siz + ytsiz - 1) / ytsiz);
	}

	public int computeNumTilesX() {
		return ((xsiz - xt0siz + xtsiz - 1) / xtsiz);
	}

	private boolean[] origSigned = null;

	public boolean isComponentOriginSignedAt(int componentIndex) {
		if (origSigned == null) {
			origSigned = new boolean[csiz];
			for (int cc = 0; cc < csiz; cc++) {
				origSigned[cc] = ((ssiz[cc] >>> SSIZ_DEPTH_BITS) == 1);
			}
		}
		return origSigned[componentIndex];
	}

	private int[] origBitDepth = null;

	public int getComponentOriginBitDepthAt(int componentIndex) {
		if (origBitDepth == null) {
			origBitDepth = new int[csiz];
			for (int cc = 0; cc < csiz; cc++) {
				origBitDepth[cc] = (ssiz[cc] & ((1 << SSIZ_DEPTH_BITS) - 1)) + 1;
			}
		}
		return origBitDepth[componentIndex];
	}

	public final int getTileLeftX() {
		return this.xt0siz;
	}

	public final int getTileTopY() {
		return this.yt0siz;
	}

	public final int getImageLeftX() {
		return this.x0siz;
	}

	public final int getImageTopY() {
		return this.y0siz;
	}

	public final int getImageWidth() {
		return this.xsiz - this.x0siz;
	}

	public final int getImageHeight() {
		return this.ysiz - this.y0siz;
	}

	public final int getNumComps() {
		return this.csiz;
	}

	public final int getNominalTileWidth() {
		return this.xtsiz;
	}

	public final int getNominalTileHeight() {
		return this.ytsiz;
	}

	@Override
	public String toString() {
		String str = "\n --- SIZ (" + lsiz + " bytes) ---\n";
		str += " Capabilities : " + rsiz + "\n";
		str += " Image dim.   : " + getImageWidth() + "x" + getImageHeight() + ", (offset=" + x0siz + "," + y0siz + ")\n";
		str += " Tile dim.    : " + getNominalTileWidth() + "x" + getNominalTileHeight() + ", (offset=" + xt0siz + "," + yt0siz + ")\n";
		str += " Component(s) : " + getNumComps() + "\n";
		str += " Orig. depth  : ";
		for (int i = 0; i < getNumComps(); i++) {
			str += getComponentOriginBitDepthAt(i) + " ";
		}
		str += "\n";
		str += " Orig. signed : ";
		for (int i = 0; i < getNumComps(); i++) {
			str += isComponentOriginSignedAt(i) + " ";
		}
		str += "\n";
		str += " Subs. factor : ";
		for (int i = 0; i < getNumComps(); i++) {
			str += getComponentDxAt(i) + "," + getComponentDyAt(i) + " ";
		}
		str += "\n";
		return str;
	}

	public int getComponentDxAt(int componentIndex) {
		return this.xrsiz[componentIndex];
	}

	public int getComponentDyAt(int componentIndex) {
		return this.yrsiz[componentIndex];
	}
}
