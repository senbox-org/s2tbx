package org.esa.s2tbx.dataio.s2;

import org.apache.commons.io.FileUtils;
import org.esa.snap.core.datamodel.TiePointGrid;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ECMWFTReader {

    final List<TiePointGrid> tiePointGrids;

    public ECMWFTReader(Path path, Path cachedir) throws IOException {
        this.tiePointGrids = new ArrayList<>();
        NetcdfFile ncfile = null;
        final Path cacheFolderPath = cachedir.resolve("aux_ecmwft");
        try {
            final Path copyPath = cacheFolderPath.resolve(path.getFileName().toString());
            Files.createDirectories(copyPath);
            Files.copy(path, copyPath, StandardCopyOption.REPLACE_EXISTING);
            ncfile = NetcdfFile.openInMemory(copyPath.toString());
            TiePointGrid tpGrid = getGrid(ncfile, "Total_column_water_vapour_surface");
            if (tpGrid != null) {
                this.tiePointGrids.add(tpGrid);
            }
            tpGrid = getGrid(ncfile, "Total_column_ozone_surface");
            if (tpGrid != null) {
                this.tiePointGrids.add(tpGrid);
            }
            tpGrid = getGrid(ncfile, "Mean_sea_level_pressure_surface");
            if (tpGrid != null) {
                this.tiePointGrids.add(tpGrid);
            }
        } catch (Exception ioe) {
            // Handle less-cool exceptions here
            ioe.printStackTrace();
        } finally {
            if (ncfile != null) {
                ncfile.close();
            }
            FileUtils.deleteDirectory(cacheFolderPath.toFile());
        }
    }

    public List<TiePointGrid> getECMWFGrids() {
        return tiePointGrids;
    }

    public TiePointGrid getGrid(NetcdfFile ncfile, String name) throws IOException {
        final Variable variable = ncfile.findVariable(null, name);
        if (variable == null) {
            return null;
        }
        String description = variable.getDescription();
        if( description.contains("@"))
            description = description.split(" @")[0] + " at surface level provided by ECMWF";
        String units = variable.getUnitsString();
        final int[] shape = variable.getShape();
        final float[] tiePoints = (float[])variable.read().get1DJavaArray(float.class);
        final TiePointGrid tiePointGrid = new TiePointGrid(name.toLowerCase().replace("_surface",""), shape[1],shape[2], 0.5, 0.5, 1220, 1220,tiePoints);
        tiePointGrid.setUnit(units);
        tiePointGrid.setNoDataValue(Double.NaN);
        tiePointGrid.setNoDataValueUsed(true);
        tiePointGrid.setDescription(description);
        return tiePointGrid;
    }
}
