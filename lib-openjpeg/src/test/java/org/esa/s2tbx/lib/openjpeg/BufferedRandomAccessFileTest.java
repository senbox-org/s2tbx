package org.esa.s2tbx.lib.openjpeg;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by jcoravu on 7/6/2019.
 */
public class BufferedRandomAccessFileTest {

    @Test
    public void testReadFileContent() throws Exception {
        File testJP2File = JP2FileReaderTest.getTestDataDir("sample.jp2");
        assertNotNull(testJP2File);

        Path filePath = testJP2File.toPath();
        assertNotNull(filePath);

        assertTrue("The input test file '"+filePath.toString()+"' does not exist.", Files.exists(filePath));

        BufferedRandomAccessFile bufferedRandomAccessFile = new BufferedRandomAccessFile(filePath, 1024, true);

        assertEquals(16298, bufferedRandomAccessFile.getLength());

        assertEquals(0, bufferedRandomAccessFile.getPosition());

        assertEquals(0, bufferedRandomAccessFile.readShort());

        assertEquals(12, bufferedRandomAccessFile.readShort());

        assertEquals(106, bufferedRandomAccessFile.readByte());

        assertEquals(5, bufferedRandomAccessFile.getPosition());

        assertEquals(1344282637, bufferedRandomAccessFile.readInt());

        assertEquals(2695, bufferedRandomAccessFile.readUnsignedShort());

        assertEquals(11, bufferedRandomAccessFile.getPosition());

        assertEquals(1.6259746672639677E-260d, bufferedRandomAccessFile.readDouble(), 0.0d);

        assertEquals(2.9022051E29f, bufferedRandomAccessFile.readFloat(), 0.0f);

        assertEquals(23, bufferedRandomAccessFile.getPosition());

        assertEquals(2305843009220669490L, bufferedRandomAccessFile.readLong());

        assertEquals(536870913L, bufferedRandomAccessFile.readUnsignedInt());

        byte[] buffer = new byte[100];
        bufferedRandomAccessFile.read(buffer, 0, buffer.length);

        assertEquals(135, bufferedRandomAccessFile.getPosition());

        assertEquals(89, buffer[0]);
        assertEquals(106, buffer[1]);
        assertEquals(112, buffer[2]);
        assertEquals(50, buffer[3]);

        bufferedRandomAccessFile.seek(1000);

        assertEquals(1000, bufferedRandomAccessFile.getPosition());

        assertEquals(4800905805561501015L, bufferedRandomAccessFile.readLong());

        assertEquals(-1352857162, bufferedRandomAccessFile.readInt());

        assertEquals(158, bufferedRandomAccessFile.readByte());

        assertEquals(720965248, bufferedRandomAccessFile.readUnsignedInt());

        assertEquals(-32640, bufferedRandomAccessFile.readShort());

        assertEquals(1019, bufferedRandomAccessFile.getPosition());
    }
}
