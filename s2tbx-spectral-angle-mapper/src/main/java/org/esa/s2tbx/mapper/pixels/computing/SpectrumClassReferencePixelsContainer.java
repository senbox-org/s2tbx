package org.esa.s2tbx.mapper.pixels.computing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container for all the SpectrumClassReferencePixels objects
 *
 * @author Razvan Dumitrascu
 */
public class SpectrumClassReferencePixelsContainer {

    private static SpectrumClassReferencePixelsContainer instance;
    private List<SpectrumClassReferencePixels> specs = new ArrayList<>();

    public SpectrumClassReferencePixelsContainer( ){}

    public synchronized void addElements(SpectrumClassReferencePixels spectrumClassReferencePixels){
      specs.add(spectrumClassReferencePixels);
    }

    public synchronized List<SpectrumClassReferencePixels> getElements(){
        return Collections.unmodifiableList(this.specs);
    }
}
