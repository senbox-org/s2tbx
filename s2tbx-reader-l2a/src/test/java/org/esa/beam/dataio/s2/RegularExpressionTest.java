package org.esa.beam.dataio.s2;

import org.esa.beam.dataio.s2.structure.S2ProductStructure;
import org.esa.beam.dataio.s2.structure.S2ProductStructureFactory;
import org.esa.beam.dataio.s2.structure.StructuralItemMatcher;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RegularExpressionTest {

    @Before
    public void setup() {
    }

    @Test
    public void testFileMatch() throws Exception
    {
        Pattern pat = Pattern.compile("(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3})?");

        assertTrue(pat.matcher("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLH.xml").matches());

        assertTrue(pat.matcher("S2A_OPER_MTD_L1C_DS_CGS1_20130621T120000_S20091211T165928.xml").matches());

        assertTrue(pat.matcher("S2A_TEST_BWI_MSIL1A_PDMC_20130424T120700_R054_V20091210235100_20091210235134.PNG").matches());
    }

    @Test
    public void testFileMatch2() throws Exception
    {
        Pattern pat = Pattern.compile("(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]{4})_([0-9]{8}T[0-9]{6})([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3,4})?");

        Pattern pat2 = Pattern.compile("_S[0-9]{8}T[0-9]{6}|_O[0-9]{6}T[0-9]{6}|_V[0-9]{8}[T]?[0-9]{6}_[0-9]{8}[T]?[0-9]{6}|_D[0-9]{2}|_A[0-9]{6}|_R[0-9]{3}|_T[A-Z|0-9]{5}|_N[0-9]{2}\\.[0-9]{2}|_B[A-B|0-9]{2}|_W[F|P]|_L[N|D]");

        assertTrue(pat.matcher("S2A_OPER_MTD_L1C_TL_CGS1_20130621T120000_A000065_T14SLH.xml").matches());

        assertTrue(pat.matcher("S2A_OPER_MTD_L1C_DS_CGS1_20130621T120000_S20091211T165928.xml").matches());

        assertTrue(pat.matcher("S2A_TEST_BWI_MSIL1A_PDMC_20130424T120700_R054_V20091210235100_20091210235134.PNG").matches());

        System.err.println(pat.matcher("S2A_TEST_BWI_MSIL1A_PDMC_20130424T120700_R054_V20091210235100_20091210235134.PNG").groupCount());

        Matcher matcher = pat.matcher("S2A_TEST_BWI_MSIL1A_PDMC_20130424T120700_R054_V20091210235100_20091210235134.PNG");
        matcher.find();
        System.err.println(matcher.group(5));
        System.err.println(matcher.group(6));
        System.err.println(matcher.group(7));

        String subdate = matcher.group(7);
        Matcher m2 = pat2.matcher(subdate);
        while(m2.find())
        {
            System.err.println(m2.group());
        }
    }


    @Test
    public void testProcut() throws Exception
    {
        S2ProductStructure prod = S2ProductStructureFactory.create(S2ProductStructureFactory.ProductType.S2L1APRODUCT);
        S2ProductStructure prodl0 = S2ProductStructureFactory.create(S2ProductStructureFactory.ProductType.S2L0PRODUCT);

        assertNotNull(prodl0);
        assertNotNull(prod);

        URL aUrl = getClass().getResource("l1c/data/S2A_OPER_PRD_MSIL1C_PDMC_20130621T120000_R065_V20091211T165928_20091211T170025.SAFE");

        if(aUrl != null)
        {
            File f = new File(aUrl.toURI());
            if(f.exists())
            {
                boolean isGood = StructuralItemMatcher.matches(prod.getThePattern(), f);
                assertTrue(isGood);

                isGood = StructuralItemMatcher.matches(prodl0.getThePattern(), f);
                assertFalse(isGood);
            }
        }
    }

}
