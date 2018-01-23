package org.esa.s2tbx.mapper.pixels.computing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Razvan Dumitrascu
 */
public class SpectrumClassReferencePixelsSingleton {

    private static SpectrumClassReferencePixelsSingleton instance;
    private List<SpectrumClassReferencePixels> specs = new ArrayList<>();

    private SpectrumClassReferencePixelsSingleton(){}

    public static synchronized SpectrumClassReferencePixelsSingleton getInstance(){
        if(instance == null){
            instance = new SpectrumClassReferencePixelsSingleton();
        }
        return instance;
    }

    public synchronized void addElements(SpectrumClassReferencePixels spectrumClassReferencePixels){
      specs.add(spectrumClassReferencePixels);
    }

    public synchronized List<SpectrumClassReferencePixels> getElements(){
        return Collections.unmodifiableList(this.specs);
    }
}
