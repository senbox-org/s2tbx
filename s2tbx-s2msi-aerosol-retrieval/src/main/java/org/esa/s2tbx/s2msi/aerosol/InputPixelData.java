package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.aerosol.util.PixelGeometry;

/**
 *
 * @author akheckel, Tonio Fincke
 */
public class InputPixelData {
    public final double elevation;
    public PixelGeometry geom;
    public PixelGeometry geomFward;
    public final double ozone;
    public double surfPressure;
    public double wvCol;
    public int nSpecWvl;
    public float[] specWvl;
    public double[] toaReflec;
    public double[] toaReflecFward;
    public double[][] surfReflec;
    public double[][] diffuseFrac;
    public double[][][] pixelLutSubset; //first dim: aod, second dim: wavelength, third dim: atmospheric parameters

    public InputPixelData(PixelGeometry geom, PixelGeometry geomFward, double elevation, double ozone, double surfPressure,
                          double wvCol, float[] specWvl, double[] toaReflec, double[] toaReflecFward) {
        this.geom = geom;
        this.geomFward = geomFward;
        this.elevation = elevation;
        this.ozone = ozone;
        this.surfPressure = surfPressure;
        this.wvCol = wvCol;
        this.specWvl = specWvl;
        this.nSpecWvl = specWvl.length;
        this.toaReflec = toaReflec;
        this.toaReflecFward = toaReflecFward;
        this.surfReflec = new double[2][nSpecWvl];
        this.diffuseFrac = new double[2][nSpecWvl];
    }

    public double[][] getDiffuseFrac() {
        return diffuseFrac;
    }

    public double[][] getSurfReflec() {
        return surfReflec;
    }

    public double[] getToaReflec() {
        return toaReflec;
    }


    public PixelGeometry getGeom() {
        return geom;
    }

    public void setWvCol(double wvCol) {
        this.wvCol = wvCol;
    }
}
