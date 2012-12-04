package org.esa.beam.dataio.s2.jp2;

import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Norman Fomferra
 * @author Peter Maloney
 */
@Ignore
public class JasPerImageAccessTest {
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

    @Before
    public void before() throws JDOMException, IOException {
        // findPathHack - finds the src/main/c directory whree the .so files are expected to be
        // @todo: replace with something else when the tests are developed
        String path = JasPerImageAccessTest.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File somedir = new File(path);
        while( !somedir.getName().equals("suhet") ) {
            somedir = somedir.getParentFile();
        }
        File projSrcMainC = new File(somedir, "src/main/c");

        final String jnaPath = projSrcMainC.getAbsolutePath();
        System.out.println("JNA path is " + jnaPath);
        System.setProperty("jna.library.path", jnaPath);
    }
}
