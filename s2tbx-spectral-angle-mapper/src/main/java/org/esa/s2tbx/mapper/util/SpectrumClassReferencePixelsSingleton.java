package org.esa.s2tbx.mapper.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rdumitrascu on 1/11/2018.
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

    public List<SpectrumClassReferencePixels> getElements(){
        return Collections.unmodifiableList(this.specs);
    }
}
