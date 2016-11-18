package org.esa.s2tbx.radiometry.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for a Band type class member that constraints the wavelength bandwidth of the band.
 *
 * @author  Cosmin Cara
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BandParameter {

    /**
     * The minimum wavelength of the band
     */
    float minWavelength() default 0.0f;

    /**
     * The maximum wavelength of the band
     */
    float maxWavelength() default 0.0f;

}
