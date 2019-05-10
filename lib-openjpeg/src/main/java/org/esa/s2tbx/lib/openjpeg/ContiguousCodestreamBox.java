package org.esa.s2tbx.lib.openjpeg;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ContiguousCodestreamBox implements IMarkers {

	private SIZMarkerSegment siz;
	private CODMarkerSegment cod;
	private QCDMarkerSegment qcd;

	public ContiguousCodestreamBox(IRandomAccessFile jp2FileStream) throws IOException {
		if (jp2FileStream.readShort() == IMarkers.SOC) {
			short marker = jp2FileStream.readShort();
			if (marker == SIZ) {
				Map<Short, DataInputStream> result = new HashMap<Short, DataInputStream>();

				DataInputStream sizInputStream = readBytes(marker, jp2FileStream);
				result.put(SIZ, sizInputStream);

				while ((marker = jp2FileStream.readShort()) != SOT) {
					boolean existingMarker = result.containsKey(marker);
					validateMarkerSegment(marker, existingMarker);
					DataInputStream inputStream = readBytes(marker, jp2FileStream);
					result.put(marker, inputStream);
				}

				DataInputStream dataInputStream = result.get(SIZ);
				if (dataInputStream != null) {
					readSIZ(dataInputStream);
				}

				dataInputStream = result.get(COD);
				if (dataInputStream != null) {
					readCOD(dataInputStream);
				}

				dataInputStream = result.get(QCD);
				if (dataInputStream != null) {
					readQCD(dataInputStream);
				}
			} else {
				throw new InvalidContiguousCodestreamException("First marker after " + "SOC " + "must be SIZ " + Integer.toHexString(marker));
			}
		} else {
			throw new InvalidContiguousCodestreamException("SOC marker segment not " + " found at the " + "beginning of the " + "codestream.");
		}
	}

	public String toStringMainHeader() {
		return siz + " " + cod + " " + qcd;
	}

	public final int getNumComps() {
		return siz.getNumComps();
	}

	public SIZMarkerSegment getSiz() {
		return siz;
	}

	public CODMarkerSegment getCod() {
		return cod;
	}

	public QCDMarkerSegment getQcd() {
		return qcd;
	}

	private void readSIZ(DataInputStream jp2FileStream) throws IOException {
		this.siz = new SIZMarkerSegment();
		this.siz.readData(jp2FileStream);
	}

	private void readQCD(DataInputStream jp2FileStream) throws IOException {
		this.qcd = new QCDMarkerSegment();
		int numberOfLevels = this.cod.getNumberOfLevels();
		this.qcd.readData(jp2FileStream, numberOfLevels);
	}

	private void readCOD(DataInputStream jp2FileStream) throws IOException {
		this.cod = new CODMarkerSegment();
		this.cod.readData(jp2FileStream);
	}

	private static void validateMarkerSegment(short marker, boolean existingMarker) throws IOException {
		if (marker == SIZ) {
			throw new InvalidContiguousCodestreamException("More than one SIZ marker segment found in main header");
		} else if (marker == SOD) {
			throw new InvalidContiguousCodestreamException("SOD found in main header");
		} else if (marker == EOC) {
			throw new InvalidContiguousCodestreamException("EOC found in main header");
		} else if (marker == COD) {
			if (existingMarker) {
				throw new InvalidContiguousCodestreamException("More than one COD marker found in main header");
			}
		} else if (marker == QCD) {
			if (existingMarker) {
				throw new InvalidContiguousCodestreamException("More than one QCD marker found in main header");
			}
		} else if (marker == CRG) {
			if (existingMarker) {
				throw new InvalidContiguousCodestreamException("More than one CRG " + "marker " + "found in main header");
			}
		} else if (marker == TLM) {
			if (existingMarker) {
				throw new InvalidContiguousCodestreamException("More than one TLM " + "marker " + "found in main header");
			}
		} else if (marker == PLM) {
			if (existingMarker) {
				throw new InvalidContiguousCodestreamException("More than one PLM " + "marker " + "found in main header");
			}
		} else if (marker == POC) {
			if (existingMarker) {
				throw new InvalidContiguousCodestreamException("More than one POC " + "marker segment found " + "in main header");
			}
		} else if (marker == PLT) {
			throw new InvalidContiguousCodestreamException("PLT found in main header");
		} else if (marker == PPT) {
			throw new InvalidContiguousCodestreamException("PPT found in main header");
		}
	}

	private static DataInputStream readBytes(short marker, IRandomAccessFile jp2FileStream) throws IOException {
		if (marker < 0xffffff30 || marker > 0xffffff3f) {
			// Read marker segment length and create corresponding byte buffer
			int markSegLen = jp2FileStream.readUnsignedShort();
			byte[] buf = new byte[markSegLen];

			// Copy data (after re-insertion of the marker segment length);
			buf[0] = (byte) ((markSegLen >> 8) & 0xFF);
			buf[1] = (byte) (markSegLen & 0xFF);
			jp2FileStream.readFully(buf, 2, markSegLen - 2);

			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			return new DataInputStream(bais);
		}
		return null;
	}
}
