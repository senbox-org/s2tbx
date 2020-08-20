package org.esa.s2tbx.dataio.kompsat2.metadata;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Class for basic band metadata operations
 *
 * @author  Razvan Dumitrascu
 */
public class BandMetadataUtil {

    BandMetadata[] bandsMetadata;

    public BandMetadataUtil(BandMetadata... bMetadata) {
        this.bandsMetadata = bMetadata;
    }

    public int getMaxNumColumns(){
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumColumns));
        return this.bandsMetadata[bandsMetadata.length-1].getNumColumns();
    }

    public int getMaxNumLines(){
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumLines));
        return this.bandsMetadata[bandsMetadata.length-1].getNumLines();
    }

    public double getMaxStepSizeX(){
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getStepSizeX));
        return this.bandsMetadata[0].getStepSizeX();
    }

    public double getMaxStepSizeY(){
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getStepSizeY));
        return this.bandsMetadata[0].getStepSizeY();
    }

    public boolean isMultiSize(){
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumColumns));
        if(bandsMetadata[0].getNumColumns() != bandsMetadata[bandsMetadata.length - 1].getNumColumns()){
            return true;
        }
        Arrays.sort(this.bandsMetadata, Comparator.comparing(BandMetadata::getNumLines));
        return (bandsMetadata[0].getNumLines() != bandsMetadata[bandsMetadata.length - 1].getNumLines());
    }
}
