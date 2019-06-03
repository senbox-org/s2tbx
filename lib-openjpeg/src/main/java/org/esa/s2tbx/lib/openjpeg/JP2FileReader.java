package org.esa.s2tbx.lib.openjpeg;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JP2FileReader implements FileFormatBoxes {

	private static final Set<Integer> BLOCK_TERMINATORS = new HashSet<Integer>();
	static {
		BLOCK_TERMINATORS.add(0);
		BLOCK_TERMINATORS.add(7);
	}

	private ContiguousCodestreamBox contiguousCodestreamBox;
	private List<String> xmlMetadata;

	public JP2FileReader() {
	}

	public ContiguousCodestreamBox getHeaderDecoder() {
		return contiguousCodestreamBox;
	}

	public List<String> getXmlMetadata() {
		return xmlMetadata;
	}

	public void readFileFormat(Path file, int bufferSize, boolean canSetFilePosition) throws IOException {
		BufferedRandomAccessFile jp2FileStream = new BufferedRandomAccessFile(file, bufferSize, canSetFilePosition);

		readJP2SignatureBox(jp2FileStream);

		readFileTypeBox(jp2FileStream);

		// read all remaining boxes
		long fileSizeInBytes = jp2FileStream.getLength();
		long positionBeforeHeader = jp2FileStream.getPosition();
		boolean jp2HeaderBoxFound = false;
		boolean lastBoxFound = false;
		while (!lastBoxFound) {
			long boxPosition = jp2FileStream.getPosition();

			long boxLength = jp2FileStream.readUnsignedInt();
			int boxType = jp2FileStream.readInt();
			long boxExtendedLength = 0;
			if (boxLength == 0) {
				lastBoxFound = true;
			} else if (boxLength == 1) {
				boxExtendedLength = jp2FileStream.readLong();
				boxLength = boxExtendedLength;
			}

			if ((boxPosition + boxLength) == fileSizeInBytes) {
				lastBoxFound = true;
			}
			if (lastBoxFound) {
				boxLength = fileSizeInBytes - jp2FileStream.getPosition();
			}

			if (boxType == JP2_HEADER_BOX) {
				if (jp2HeaderBoxFound) {
					throw new IOException("Invalid JP2 file: Multiple JP2Header boxes found.");
				} else {
					readJP2HeaderBox(boxLength, boxExtendedLength, jp2FileStream);
					jp2HeaderBoxFound = true;
				}
			} else if (boxType == CONTIGUOUS_CODESTREAM_BOX) {
				if (jp2HeaderBoxFound) {
					readContiguousCodeStreamBox(boxLength, boxExtendedLength, jp2FileStream);
				} else {
					throw new IOException("Invalid JP2 file: JP2Header box not found before Contiguous codestream box.");
				}
			} else if (boxType == INTELLECTUAL_PROPERTY_BOX) {
				readIntellectualPropertyBox(boxLength);
			} else if (boxType == XML_BOX) {
				readXMLBox(boxLength, jp2FileStream);
			} else if (boxType == UUID_BOX) {
				readUUIDBox(boxLength);
			} else if (boxType == UUID_INFO_BOX) {
				readUUIDInfoBox(boxLength);
			} else if (boxType == ASSOCIATION_BOX) {
				// the association box contains the xml box sometimes
				readAssociationBox(boxLength);
			} else {
				//System.out.println("Unknown box-type: 0x" + Integer.toHexString(boxType));
			}
			if (!lastBoxFound) {
				jp2FileStream.seek(boxPosition + boxLength);
			}
		}

		if (this.contiguousCodestreamBox == null) {
			// Not a valid JP2 file or codestream
			throw new IOException("Invalid JP2 file: Contiguous codestream box is missing.");
		} else {
			if (this.xmlMetadata == null) {
				// parse again the file to extract the xml box
				jp2FileStream.seek(positionBeforeHeader);

				// start of any XML block (reversed)
				int[] xmlBoxCodeInReverseOrder = { 0x20, 0x6C, 0x6D, 0x78 };
				ByteSequenceMatcher xmlTagMatcher = new ByteSequenceMatcher(xmlBoxCodeInReverseOrder);

				while (jp2FileStream.getPosition() < fileSizeInBytes) {
					int currentByte = jp2FileStream.read();
					if (xmlTagMatcher.matches(currentByte)) {
						StringBuilder builder = new StringBuilder();
						int current;
						while (jp2FileStream.getPosition() < fileSizeInBytes && !BLOCK_TERMINATORS.contains(current = jp2FileStream.read())) {
							builder.append(Character.toString((char) current));
						}
						if (this.xmlMetadata == null) {
							this.xmlMetadata = new ArrayList<String>();
						}
						this.xmlMetadata.add(builder.toString());
						break;
					}
				}
			}
		}
	}

	private void readJP2SignatureBox(IRandomAccessFile jp2FileStream) throws IOException {
		if (jp2FileStream.readInt() == 0x0000000C) {
			if (jp2FileStream.readInt() == JP2_SIGNATURE_BOX) {
				if (jp2FileStream.readInt() == JP2_SIGNATURE_BOX_CONTENT) {
					return;
				}
			}
		}
		throw new IOException("nvalid JP2 file: file is neither valid JP2 file nor valid JPEG 2000 codestream");
	}

	private void readFileTypeBox(IRandomAccessFile jp2FileStream) throws IOException, EOFException {
		// read box length (LBox)
		int length = jp2FileStream.readInt();
		if (length == 0) {
			throw new IOException("Zero-length of Profile Box");
		} else {
			// check that this is a File Type box (TBox)
			if (jp2FileStream.readInt() == FILE_TYPE_BOX) {
				// check for XLBox
				if (length == 1) { // box has 8 byte length
					throw new IOException("File too long.");
				}

				// read Brand field
				jp2FileStream.readInt();

				// read MinV field
				jp2FileStream.readInt();

				// check that there is at least one FT_BR entry in in compatibility list
				boolean foundComp = false;
				int nComp = (length - 16) / 4; // Number of compatibilities.
				for (int i = nComp; i > 0; i--) {
					if (jp2FileStream.readInt() == FT_BR) {
						foundComp = true;
					}
				}
				if (!foundComp) {
					throw new IOException("Invalid JP2 file: missing entry.");
				}
			} else {
				throw new IOException("Invalid JP2 file: File Type box missing");
			}
		}
	}

	private void readJP2HeaderBox(long boxLength, long longLength, IRandomAccessFile jp2FileStream) throws IOException {
		if (boxLength == 0) { // This can not be last box
			throw new IOException("Zero-length of JP2Header Box");
		}

		// Here the JP2Header data (DBox) would be read if we were to use it
		int firstBlock = jp2FileStream.readInt();
		if (firstBlock == 22) {
		} else {
			throw new IOException("Invalid image header box.");
		}
	}

	private void readContiguousCodeStreamBox(long boxLength, long longLength, IRandomAccessFile jp2FileStream) throws IOException {
		this.contiguousCodestreamBox = new ContiguousCodestreamBox(jp2FileStream);
	}

	private void readIntellectualPropertyBox(long boxLength) {
	}

	private void readXMLBox(long boxLength, IRandomAccessFile jp2FileStream) throws IOException {
		StringBuilder builder = new StringBuilder();
		int index = 0;
		int current;
		while (index < boxLength && !BLOCK_TERMINATORS.contains(current = jp2FileStream.read())) {
			builder.append(Character.toString((char) current));
			index++;
		}
		if (this.xmlMetadata == null) {
			this.xmlMetadata = new ArrayList<String>();
		}
		this.xmlMetadata.add(builder.toString());
	}

	private void readUUIDBox(long boxLength) {
	}

	private void readUUIDInfoBox(long boxLength) {
	}

	private void readAssociationBox(long boxLength) {
	}

	private class ByteSequenceMatcher {
		private int[] queue;
		private int[] sequence;

		ByteSequenceMatcher(int[] sequenceToMatch) {
			sequence = sequenceToMatch;
			queue = new int[sequenceToMatch.length];
		}

		public boolean matches(int unsignedByte) {
			insert(unsignedByte);
			return isMatch();
		}

		private void insert(int unsignedByte) {
			System.arraycopy(queue, 0, queue, 1, sequence.length - 1);
			queue[0] = unsignedByte;
		}

		private boolean isMatch() {
			boolean result = true;
			for (int i = 0; i < sequence.length; i++) {
				result = (queue[i] == sequence[i]);
				if (!result)
					break;
			}
			return result;
		}
	}

	public static void main(String argv[]) throws IOException {
//		String filePath = "d:\\open-jpeg-files\\1_8bit_component_gamma_1_8_space.jp2";
//		String filePath = "d:\\open-jpeg-files\\sample.jp2";
//		String filePath = "d:\\open-jpeg-files\\IMG_test1.jp2";
//		String filePath = "d:\\open-jpeg-files\\IMG_test2.jp2";
//		String filePath = "d:\\open-jpeg-files\\s2-l1c\\T34HFH_20161206T080312_B02.jp2";
//		String filePath = "C:\\Apache24\\htdocs\\snap\\JP2\\IMG_PHR1A_1,5GB.JP2";
//		String filePath = "C:\\Apache24\\htdocs\\snap\\JP2\\IMG_PHR1A_358MB.JP2";
		String filePath = "D:\\shared\\IMG_PHR1A_PMS_201402040054228_ORT_1336649101-001_R1C1.JP2";

		Path jp2File = Paths.get(filePath);

		System.out.println("Reading file: "+ jp2File.toString()+"\n");

		JP2FileReader fileFormatReader = new JP2FileReader();
		fileFormatReader.readFileFormat(jp2File, 1024 * 1024, true);

		ContiguousCodestreamBox hd = fileFormatReader.getHeaderDecoder();
		SIZMarkerSegment siz = hd.getSiz();

		int nCompCod = hd.getNumComps();
		int nTiles = siz.computeNumTiles();

		// Report information

		String info = nCompCod + " component(s) in codestream, " + nTiles + " tile(s)\n";
		info += "Num tiles: on x " + siz.computeNumTilesX();
		info += " on y: " + siz.computeNumTilesY() + "\n";
		info += "Image dimension: ";
		for (int c = 0; c < nCompCod; c++) {
			info += siz.getCompImgWidth(c) + "x" + siz.getCompImgHeight(c) + " ";
		}

		if (nTiles != 1) {
			info += "\nNom. Tile dim. (in canvas): " + siz.getNominalTileWidth() + "x" + siz.getNominalTileHeight();
		}
		System.out.println(info);
		System.out.println("Main header:\n" + hd.toStringMainHeader());
	}
}
