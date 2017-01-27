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

    public double sza;
    public double vza;
    public double razi;

    public PixelGeometry(double sza, double saa, double vza, double vaa) {
        this.sza = sza;
        this.vza = vza;
        this.razi = getRelativeAzi(saa, vaa);
    }

    public static double getRelativeAzi(double saa, double vaa) {
        final double saaRad = Math.toRadians(saa);
        final double vaaRad = Math.toRadians(vaa);
        return Math.toDegrees(Math.acos(Math.cos(saaRad) * Math.cos(vaaRad) + Math.sin(saaRad) * Math.sin(vaaRad)));
    }

    public double getSza() {
        return sza;
    }

    public void setSza(double sza) {
        this.sza = sza;
    }

    public double getVza() {
        return vza;
    }

    public void setVza(double vza) {
        this.vza = vza;
    }

    public double getRazi() {
        return razi;
    }

    public void setRazi(double razi) {
        this.razi = razi;
    }
}
