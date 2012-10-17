package org.esa.beam.dataio.s2.jp2;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * @author Norman Fomferra
 * @author Peter Maloney
 */
public class OpenJpegImageAccessTest {
    final String libPath = "/usr/local/lib/libopenjp2.so";
    final String openJpegPath = "/usr/local/lib/openjpeg-1.99";
    final String jasPerPath = "/usr/local/lib/jasper";
    final String pathSeparator = System.getProperty("path.separator");

    @Test
    public void testOpenImage() {
        // @todo: hardcoded test image
        System.out.println( OpenJpegImageAccess.INSTANCE.openImage("/opt/imageaccess/Cevennes1.j2k") );
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
    @Test
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
        System.out.println("JNA path is " + jnaPath);
        System.setProperty("jna.library.path", libPath + pathSeparator + openJpegPath + pathSeparator + jnaPath + pathSeparator + jasPerPath);
    }
}
