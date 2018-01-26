package org.esa.s2tbx.mapper.pixels.mean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rdumitrascu on 1/11/2018.
 */
public class SpectrumContainer {

    private static SpectrumContainer instance;
    private List<Spectrum> specs = new ArrayList<>();

    public  SpectrumContainer(){}

    public synchronized void addElements(Spectrum spectrum){
        specs.add(spectrum);
    }

    public synchronized List<Spectrum> getElements(){
        return Collections.unmodifiableList(this.specs);
    }
}
