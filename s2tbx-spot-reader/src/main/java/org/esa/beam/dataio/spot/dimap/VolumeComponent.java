package org.esa.beam.dataio.spot.dimap;

/**
 * This maps to the corresponding DIMAP Component element.
 * @author Cosmin Cara
 */
public class VolumeComponent
{
    String title;
    String type;
    String path;
    String thumbnailPath;
    int[] index;

    public VolumeComponent() {}

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public int[] getIndex() { return index; }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}
