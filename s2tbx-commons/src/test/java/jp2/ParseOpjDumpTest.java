package jp2;

import org.junit.Test;
import org.openjpeg.JpegUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.*;

public class ParseOpjDumpTest {

    @Test
    public void testRun4() throws URISyntaxException, IOException
    {
        String jp2Path = "/org/esa/s2tbx/dataio/s2/l2a/out.txt";
        final File file = new File(ParseOpjDumpTest.class.getResource(jp2Path).toURI());

        List<String> content = Files.readAllLines(file.toPath());

        TileLayout result = JpegUtils.parseOpjDump(content);
        
        assertEquals(5490, result.width);
        assertEquals(5490, result.height);
        assertEquals(5490, result.tileWidth);
        assertEquals(5490, result.tileHeight);
        assertEquals(1, result.numXTiles);
        assertEquals(1, result.numYTiles);
        assertEquals(6, result.numResolutions);
    }

    @Test
    public void testRun5() throws URISyntaxException, IOException
    {
        String jp2Path = "/org/esa/s2tbx/dataio/s2/l1c/out.txt";
        final File file = new File(ParseOpjDumpTest.class.getResource(jp2Path).toURI());

        List<String> content = Files.readAllLines(file.toPath());

        TileLayout result = JpegUtils.parseOpjDump(content);

        assertEquals(10980, result.width);
        assertEquals(10980, result.height);
        assertEquals(2048, result.tileWidth);
        assertEquals(2048, result.tileHeight);
        assertEquals(6, result.numXTiles);
        assertEquals(6, result.numYTiles);
        assertEquals(6, result.numResolutions);
    }

}
