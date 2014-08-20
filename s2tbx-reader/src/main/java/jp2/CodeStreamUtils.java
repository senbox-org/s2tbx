package jp2;

import jp2.segments.CodingStyleDefaultSegment;
import jp2.segments.ImageAndTileSizeSegment;

import org.esa.beam.dataio.s2.L1cTileLayout;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by opicas-p on 03/07/2014.
 */
public class CodeStreamUtils {

    public static ImageAndTileSizeSegment getSizeInfo(String uri, BoxReader.Listener listener) throws URISyntaxException, IOException {
        final File file = new File(CodeStreamUtils.class.getResource(uri).toURI());
        final FileImageInputStream stream = new FileImageInputStream(file);
        BoxReader boxReader = new BoxReader(stream, file.length(), listener);

        Box box;
        do {
            box =  boxReader.readBox();
            if (box == null) {
                //todo change error messages
                throw new IllegalArgumentException("Wrong jpeg2000 format ?");
            }
        } while (!box.getSymbol().equals("jp2c"));

        boxReader.getStream().seek(box.getPosition() + box.getDataOffset());
        final CodestreamReader reader = new CodestreamReader(boxReader.getStream(),
                box.getPosition() + box.getDataOffset(),
                box.getLength() - box.getDataOffset());
        final MarkerSegment seg1 = reader.readSegment();

        final MarkerSegment seg2 = reader.readSegment();

        final ImageAndTileSizeSegment imageAndTileSizeSegment = (ImageAndTileSizeSegment) seg2;
        return imageAndTileSizeSegment;
    }

    public static CodingStyleDefaultSegment getCodingInfo(String uri, BoxReader.Listener listener) throws URISyntaxException, IOException {
        final File file = new File(CodeStreamUtils.class.getResource(uri).toURI());
        final FileImageInputStream stream = new FileImageInputStream(file);
        BoxReader boxReader = new BoxReader(stream, file.length(), listener);

        Box box;
        do {
            box =  boxReader.readBox();
            if (box == null) {
                //todo change error messages
                throw new IllegalArgumentException("Wrong jpeg2000 format ?");
            }
        } while (!box.getSymbol().equals("jp2c"));

        boxReader.getStream().seek(box.getPosition() + box.getDataOffset());
        final CodestreamReader reader = new CodestreamReader(boxReader.getStream(),
                box.getPosition() + box.getDataOffset(),
                box.getLength() - box.getDataOffset());
        final MarkerSegment seg1 = reader.readSegment();
        final MarkerSegment seg2 = reader.readSegment();
        final MarkerSegment seg3 = reader.readSegment();

        CodingStyleDefaultSegment roar = (CodingStyleDefaultSegment) seg3;
        return roar;
    }

    public static L1cTileLayout getL1cTileLayout(String uri, BoxReader.Listener listener) throws URISyntaxException, IOException {
        final File file = new File(CodeStreamUtils.class.getResource(uri).toURI());
        final FileImageInputStream stream = new FileImageInputStream(file);
        BoxReader boxReader = new BoxReader(stream, file.length(), listener);

        Box box;
        do {
            box =  boxReader.readBox();
            if (box == null) {
                //todo change error messages
                throw new IllegalArgumentException("Wrong jpeg2000 format ?");
            }
        } while (!box.getSymbol().equals("jp2c"));

        boxReader.getStream().seek(box.getPosition() + box.getDataOffset());
        final CodestreamReader reader = new CodestreamReader(boxReader.getStream(),
                box.getPosition() + box.getDataOffset(),
                box.getLength() - box.getDataOffset());
        final MarkerSegment seg1 = reader.readSegment();
        final MarkerSegment seg2 = reader.readSegment();

        final ImageAndTileSizeSegment is = (ImageAndTileSizeSegment) seg2;
        final MarkerSegment seg3 = reader.readSegment();

        CodingStyleDefaultSegment roar = (CodingStyleDefaultSegment) seg3;

        return new L1cTileLayout((int) is.getXsiz(), (int) is.getYsiz(), (int) is.getXtsiz(), (int) is.getYtsiz(), getXNumTiles(is), getYNumTiles(is), roar.getLevels());
    }

    public static int getNumTiles(ImageAndTileSizeSegment imageAndTileSizeSegment)
    {
        double xTiles = Math.ceil((imageAndTileSizeSegment.getXsiz() - imageAndTileSizeSegment.getXosiz()) / (float) imageAndTileSizeSegment.getXtsiz());
        double yTiles = Math.ceil((imageAndTileSizeSegment.getYsiz() - imageAndTileSizeSegment.getYosiz()) / (float) imageAndTileSizeSegment.getYtsiz());

        int numTiles = (int) (xTiles * yTiles);

        return numTiles;
    }

    public static int getYNumTiles(ImageAndTileSizeSegment imageAndTileSizeSegment)
    {
        double yTiles = Math.ceil((imageAndTileSizeSegment.getYsiz() - imageAndTileSizeSegment.getYosiz()) / (float) imageAndTileSizeSegment.getYtsiz());

        return (int) yTiles;
    }

    public static int getXNumTiles(ImageAndTileSizeSegment imageAndTileSizeSegment)
    {
        double xTiles = Math.ceil((imageAndTileSizeSegment.getXsiz() - imageAndTileSizeSegment.getXosiz()) / (float) imageAndTileSizeSegment.getXtsiz());

        return (int) xTiles;
    }
}
