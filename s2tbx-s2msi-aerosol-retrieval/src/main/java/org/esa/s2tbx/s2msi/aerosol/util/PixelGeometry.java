/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esa.s2tbx.s2msi.aerosol.util;

/**
 *
 * @author akheckel
 */
public class PixelGeometry {

    public float sza;
    public float vza;
    public float razi;

    public PixelGeometry(double sza, double saa, double vza, double vaa) {
        this.sza = (float) sza;
        this.vza = (float) vza;
        this.razi = getRelativeAzi((float)saa, (float)vaa);
    }

    public static float getRelativeAzi(float saa, float vaa) {
        final double saaRad = Math.toRadians(saa);
        final double vaaRad = Math.toRadians(vaa);
        return (float) (Math.toDegrees(Math.acos(Math.cos(saaRad) * Math.cos(vaaRad) + Math.sin(saaRad) * Math.sin(vaaRad))));
    }

    public float getSza() {
        return sza;
    }

    public void setSza(float sza) {
        this.sza = sza;
    }

    public float getVza() {
        return vza;
    }

    public void setVza(float vza) {
        this.vza = vza;
    }

    public float getRazi() {
        return razi;
    }

    public void setRazi(float razi) {
        this.razi = razi;
    }
}
