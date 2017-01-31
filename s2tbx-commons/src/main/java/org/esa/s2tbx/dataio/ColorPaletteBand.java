package org.esa.s2tbx.dataio;

import com.bc.ceres.core.ProgressMonitor;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ColorPaletteDef;
import org.esa.snap.core.datamodel.ImageInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jean Coravu
 */
public class ColorPaletteBand extends Band {
    private static final Logger logger = Logger.getLogger(ColorPaletteBand.class.getName());

    private final Path colorPaletteFilePath;

    public ColorPaletteBand(String name, int dataType, int width, int height, Path colorPaletteFilePath) {
        super(name, dataType, width, height);

        this.colorPaletteFilePath = colorPaletteFilePath;
    }

    @Override
    public synchronized ImageInfo createDefaultImageInfo(double[] histoSkipAreas, ProgressMonitor pm) {
        if (this.colorPaletteFilePath != null) {
            try {
                ColorPaletteDef colorPaletteDef = ColorPaletteDef.loadColorPaletteDef(this.colorPaletteFilePath.toFile());
                return new ImageInfo(colorPaletteDef);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, String.format("Unable to load the custom color palette from file '%s'.", this.colorPaletteFilePath.toString()), ex);
            }
        }
        return super.createDefaultImageInfo(histoSkipAreas, pm);
    }
}
