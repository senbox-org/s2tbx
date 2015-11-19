package org.esa.s2tbx.dataio.s2;

/**
 * @author Julien Malik (CS SI)
 */
public class S2BandInformation {
    protected String physicalBand;
    protected S2SpatialResolution resolution;
    protected String imageFileTemplate;

    public S2BandInformation(String physicalBand,
                      S2SpatialResolution resolution,
                      String imageFileTemplate) {
        this.physicalBand = physicalBand;
        this.resolution = resolution;
        this.imageFileTemplate = imageFileTemplate;
    }

    public String getPhysicalBand() {
        return physicalBand;
    }

    public void setPhysicalBand(String physicalBand) {
        this.physicalBand = physicalBand;
    }

    public S2SpatialResolution getResolution() {
        return resolution;
    }

    public void setResolution(S2SpatialResolution resolution) {
        this.resolution = resolution;
    }

    public void setImageFileTemplate(String template) {
        this.imageFileTemplate = template;
    }

    public String getImageFileTemplate() {
        return this.imageFileTemplate;
    }
}
