package org.esa.beam.dataio.s2;

import com.sun.jna.Library;
import com.sun.jna.Native;

import java.io.File;
import java.util.regex.Pattern;

public class Utils {

    public static String GetShortPathName(String path)
    {
        if(!(new File(path).exists()))
        {
            return "";
        }

        int sizeBuffer = 2048;
        byte[] shortt = new byte[sizeBuffer];

        //Call CKernel32 interface to execute GetShortPathNameA method
        int a = CKernel32.INSTANCE.GetShortPathNameA(path, shortt, sizeBuffer);
        String shortPath = Native.toString(shortt);
        return shortPath;
    }

    public static String GetIterativeShortPathName(String path)
    {
        if(!(new File(path).exists()))
        {
            return "";
        }

        String firstTry = GetShortPathName(path);
        if(firstTry.length() != 0)
        {
            return firstTry;
        }

        int lenght = 0;
        String workingPath = path;
        while (lenght == 0)
        {
            workingPath = new File(workingPath).getParent();
            lenght = GetShortPathName(workingPath).length();
        }

        String[] shortenedFragments = GetShortPathName(workingPath).split(Pattern.quote(File.separator));
        String[] fragments = path.split(Pattern.quote(File.separator));

        for(int index=0; index < shortenedFragments.length; index++)
        {
            fragments[index] = shortenedFragments[index];
        }

        String complete = String.join(File.separator, fragments);
        String shortComplete = GetShortPathName(complete);

        if(shortComplete.length() != 0)
        {
            return shortComplete;
        }

        return GetIterativeShortPathName(complete);
    }

    public interface CKernel32 extends Library {

        CKernel32 INSTANCE = (CKernel32) Native.loadLibrary("kernel32", CKernel32.class);

        int GetShortPathNameA(String LongName, byte[] ShortName, int BufferCount);
    }

}