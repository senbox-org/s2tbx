package org.esa.s2tbx.mapper.pixels.mean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A container for all the Spectrum objects
 *
 *  @author Razvan Dumitrascu
 */
public class SpectrumContainer {

    private List<Spectrum> specs = new ArrayList<>();

    public  SpectrumContainer(){}

    public synchronized void addElements(Spectrum spectrum){
        specs.add(spectrum);
    }

    public synchronized List<Spectrum> getElements(){
        return Collections.unmodifiableList(this.specs);
    }
}
