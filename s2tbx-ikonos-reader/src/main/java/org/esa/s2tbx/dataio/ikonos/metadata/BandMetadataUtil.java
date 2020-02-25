package org.esa.s2tbx.dataio.ikonos.metadata;


import java.util.Arrays;
import java.util.Comparator;

/**
 * Class for basic band metadata operations
 *
 * @author Denisa Stefanescu
 */
public class BandMetadataUtil {

    BandMetadata[] bandsMetadata;

    public BandMetadataUtil(BandMetadata... bMetadata) {
        this.bandsMetadata = bMetadata;
    }

    public int getMaxNumColumns() {
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumColumns));
        return this.bandsMetadata[bandsMetadata.length - 1].getNumColumns();
    }

    public int getMaxNumLines() {
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumLines));
        return this.bandsMetadata[bandsMetadata.length - 1].getNumLines();
    }

    public int getProductStepX(){
        if(Arrays.asList(this.bandsMetadata).stream().anyMatch(bandMetadata -> bandMetadata.getImageFileName().contains("pan") || bandMetadata.getImageFileName().contains("PAN"))){
            return (int) Arrays.asList(this.bandsMetadata).stream().filter(bandMetadata -> bandMetadata.getImageFileName().contains("pan")).findAny().get().getPixelSizeX();
        }
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getPixelSizeX));
        return (int) this.bandsMetadata[bandsMetadata.length - 1].getPixelSizeX();
    }

    public int getProductStepY(){
        if(Arrays.asList(this.bandsMetadata).stream().anyMatch(bandMetadata -> bandMetadata.getImageFileName().contains("pan") || bandMetadata.getImageFileName().contains("PAN"))){
            return (int) Arrays.asList(this.bandsMetadata).stream().filter(bandMetadata -> bandMetadata.getImageFileName().contains("pan")).findAny().get().getPixelSizeY();
        }
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getPixelSizeY));
        return (int) this.bandsMetadata[bandsMetadata.length - 1].getPixelSizeY();
    }

    public boolean isMultiSize(){
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumColumns));
        if(bandsMetadata[0].getNumColumns() != bandsMetadata[bandsMetadata.length - 1].getNumColumns()){
            return true;
        }
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumLines));
        return(bandsMetadata[0].getNumLines() != bandsMetadata[bandsMetadata.length - 1].getNumLines());
    }
}
