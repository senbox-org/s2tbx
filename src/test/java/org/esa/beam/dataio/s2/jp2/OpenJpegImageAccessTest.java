package org.esa.beam.dataio.s2.jp2;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openJpeg.OpenJPEGJavaDecoder;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Norman Fomferra
 * @author Peter Maloney
 */
public class OpenJpegImageAccessTest {
    final String openJpegPath = "/usr/local/lib/openjpeg-1.99";
    final String jasPerPath = "/usr/local/lib/jasper";
    final String pathSeparator = System.getProperty("path.separator");

    @Test
    public void testOpenJpegJava() throws IOException {
        final String openJPEGLibPath = "//home/peter/projects/suhet/src/main/c/libopenjpegjni.so";

        final File libFile = new File(openJPEGLibPath);
        assertTrue("library should exist: " + libFile, libFile.exists());

        System.load(openJPEGLibPath);
        System.load("/usr/lib64/libopenjpeg.so");
        System.load("/home/peter/projects/openjpeg-read-only/bin/libopenjp2.so");

        OpenJPEGJavaDecoder d = new OpenJPEGJavaDecoder(openJPEGLibPath);

        File imageFile = new File("/opt/imageaccess/Cevennes1.j2k");
        InputStream in = new FileInputStream(imageFile);
        byte[] buf = new byte[(int) imageFile.length()];
        final int status = in.read(buf);

        assertTrue(status == imageFile.length() );

        d.setCompressedStream(buf);
        final int decodeStatus = d.decodeJ2KtoImage();

        assertTrue(decodeStatus == 0 );
    }

    @Test
    public void testOpenImage() {
        // @todo: hardcoded test image
        final long pointer = OpenJpegImageAccess.INSTANCE.openImage("/opt/imageaccess/Cevennes1.j2k");
        System.out.println( "pointer is " + pointer );

        assertTrue( "pointer must not be 0", pointer != 0 );
    }

    @Test
    public void testGetNumComponents() {
        // @todo: hardcoded test image
        final long pointer = OpenJpegImageAccess.INSTANCE.openImage("/opt/imageaccess/Cevennes1.j2k");
        long numComponents = OpenJpegImageAccess.INSTANCE.getNumComponents(pointer);
        // @todo assert
        System.out.println( "numComponents = " + numComponents );
    }

    @Test
    public void testGetImageWidth() {
        // @todo: hardcoded test image
        final long pointer = OpenJpegImageAccess.INSTANCE.openImage("/opt/imageaccess/Cevennes1.j2k");
        long imageWidth0 = OpenJpegImageAccess.INSTANCE.getImageWidth(pointer, 0);
        // @todo assert
        System.out.println( "imageWidth0 = " + imageWidth0 );
        long imageWidth1 = OpenJpegImageAccess.INSTANCE.getImageWidth(pointer, 1);
        // @todo assert
        System.out.println( "imageWidth1 = " + imageWidth1 );
    }

    @Test
    public void testTestFunction() {
        OpenJpegImageAccess.INSTANCE.testFunction("/test/path/todo /f // /? /| x");
        OpenJpegImageAccess.INSTANCE.testFunction("test1/test2");
    }

    @Test
    public void testTestFunction2() {
        System.out.println( OpenJpegImageAccess.INSTANCE.testFunction2() );
    }

    //@Test
    public void testTestFunction3() {
        try{
            //this crashes the JVM... there is nothing to catch in Java or in C
            OpenJpegImageAccess.INSTANCE.testFunction3();
        }catch(Throwable t) {
            System.out.println("exception caught");
        }
    }

    /** Just scans the class to prove that the JNA is basically connected (without testing any methods) */
    //@Test
    public void testReflect() {
        final Class<? extends JasPerImageAccess> clazz = JasPerImageAccess.INSTANCE.getClass();
        System.out.println(clazz.getName() + " extends " + clazz.getSuperclass().getName() );
        for (Class<?> aClass : clazz.getInterfaces()) {
            System.out.println("    implements " + aClass.getName() );
        }
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println( "    " + method.toString() );
        }
    }

    public static void printProperties() {
        // temp debug
        System.out.println("Properties:");
        for (Object nameObj : System.getProperties().keySet()) {
            String name = (String)nameObj;
            String value = System.getProperties().getProperty(name);

            System.out.println("    " + name + " = " + value);
        }
    }

    @Before
    public void before() throws JDOMException, IOException {
        // findPathHack - finds the src/main/c directory whree the .so files are expected to be
        // @todo: replace with something else when the tests are developed
        String path = OpenJpegImageAccessTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File somedir = new File(path);
        while( !somedir.getName().equals("suhet") ) {
            somedir = somedir.getParentFile();
        }
        File projSrcMainC = new File(somedir, "src/main/c");
        // end findPathHack

        final String jnaPath = projSrcMainC.getAbsolutePath();
//        System.out.println("JNA path is " + jnaPath);
        System.setProperty("jna.library.path", jnaPath + pathSeparator + openJpegPath + pathSeparator + jasPerPath);
    }
}
