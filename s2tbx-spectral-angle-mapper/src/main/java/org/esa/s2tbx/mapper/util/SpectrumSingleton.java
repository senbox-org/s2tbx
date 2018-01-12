package org.esa.s2tbx.mapper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rdumitrascu on 1/11/2018.
 */
public class SpectrumSingleton {

    private static SpectrumSingleton instance;
    private List<Spectrum> specs = new ArrayList<>();

    private SpectrumSingleton(){}

    public static synchronized SpectrumSingleton getInstance(){
        if(instance == null){
            instance = new SpectrumSingleton();
        }
        return instance;
    }

    public synchronized void addElements(Spectrum spectrum){
        specs.add(spectrum);
    }

    public List<Spectrum> getElements(){
        return Collections.unmodifiableList(this.specs);
    }
}
