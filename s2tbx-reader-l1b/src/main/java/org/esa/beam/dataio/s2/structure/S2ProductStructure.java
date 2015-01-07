package org.esa.beam.dataio.s2.structure;

import java.util.ArrayList;
import java.util.List;

public class S2ProductStructure
{
    final private List<StructuralItem> thePattern;

    public S2ProductStructure() {
        thePattern = new ArrayList<StructuralItem>();
    }

    public S2ProductStructure(List<StructuralItem> thePattern) {
        this.thePattern = thePattern;
    }

    public void addItem(StructuralItem item)
    {
        thePattern.add(item);
    }

    public List<StructuralItem> getThePattern() {
        return thePattern;
    }
}
