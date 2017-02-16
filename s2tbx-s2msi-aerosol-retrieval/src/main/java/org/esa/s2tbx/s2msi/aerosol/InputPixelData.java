package org.esa.s2tbx.s2msi.aerosol;

import org.esa.s2tbx.s2msi.aerosol.util.PixelGeometry;

/**
 *
 * @author akheckel, Tonio Fincke
 */
public class InputPixelData {
    public final double elevation;
    public final double[] toaIrradianceToPathToToaTosa;
    public final double[] lToa;
    public final double[] tauOzoneStratAeroView;
    public final double[] tauRayOzoneStratAeroView;
    public final double[] tauRaySun;
    public final double[] tauOzoneSun;
    public PixelGeometry geom;
    public double wvCol;
    public int nSpecWvl;
    public float[] specWvl;
    private double[] toaReflec;
    public double[][] surfReflec;
    public double[][] diffuseFrac;
    public double[][][] pixelLutSubset; //first dim: aod, second dim: wavelength, third dim: atmospheric parameters

    public InputPixelData(PixelGeometry geom, double elevation, double wvCol, float[] specWvl, double[] toaReflec,
                          double[] toaIrradianceToPathToToaTosa, double[] lToa, double[] tauOzoneStratAeroView,
                          double[] tauRayOzoneStratAeroView, double[] tauRaySun, double[] tauOzoneSun) {
        this.geom = geom;
        this.elevation = elevation;
        this.wvCol = wvCol;
        this.specWvl = specWvl;
        this.nSpecWvl = specWvl.length;
        this.toaReflec = toaReflec;
        this.surfReflec = new double[2][nSpecWvl];
        this.diffuseFrac = new double[2][nSpecWvl];
        this.toaIrradianceToPathToToaTosa = toaIrradianceToPathToToaTosa;
        this.lToa = lToa;
        this.tauOzoneStratAeroView = tauOzoneStratAeroView;
        this.tauRayOzoneStratAeroView = tauRayOzoneStratAeroView;
        this.tauRaySun = tauRaySun;
        this.tauOzoneSun = tauOzoneSun;
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
