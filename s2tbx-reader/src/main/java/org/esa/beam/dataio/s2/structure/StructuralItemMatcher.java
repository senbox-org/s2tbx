package org.esa.beam.dataio.s2.structure;

import java.io.File;
import java.util.List;
public class StructuralItemMatcher {

    public static boolean matches(List<StructuralItem> aList, File our_dir)
    {
        assert our_dir.exists();
        for (StructuralItem aStructuralItem : aList)
        {
            String[] result = our_dir.list(aStructuralItem);
            if (!aStructuralItem.isOptional())
            {
                if(result.length == 0)
                {
                    System.err.println("This failed: " + aStructuralItem.getPattern());
                    return false;
                }
            }
        }
        return true;
    }
}
