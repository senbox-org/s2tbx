package org.esa.beam.dataio.s2;
import com.sun.jna.Native;
import com.sun.jna.Library;

public class Utils {

    public static String GetShortPathName(String path)
    {
        int sizeBuffer = 2048;
        byte[] shortt = new byte[sizeBuffer];

        //Call CKernel32 interface to execute GetShortPathNameA method
        int a = CKernel32.INSTANCE.GetShortPathNameA(path, shortt, sizeBuffer);
        String shortPath = Native.toString(shortt);
        return shortPath;

    }

    public interface CKernel32 extends Library {

        CKernel32 INSTANCE = (CKernel32) Native.loadLibrary("kernel32", CKernel32.class);

        int GetShortPathNameA(String LongName, byte[] ShortName, int BufferCount);
    }

}