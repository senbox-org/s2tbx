package org.esa.s2tbx.dataio.s2;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class ECMWFTReader {

    List<TiePointGrid> tiePointGrid;

    public ECMWFTReader(Path path, Path cachedir) throws IOException {
        if (tiePointGrid != null)
            tiePointGrid.clear();
        tiePointGrid = new ArrayList<>();
        NetcdfFile ncfile = null;
        Path cacheFolderPath = cachedir;
        cacheFolderPath = cacheFolderPath.resolve("aux_ecmfwt");
        try {
            Files.createDirectory(cacheFolderPath);
        } catch (FileAlreadyExistsException exc) {
        }
        Path copyPath = cacheFolderPath.resolve(path.getFileName());
        try {
            Files.copy(path, copyPath, StandardCopyOption.REPLACE_EXISTING);
            ncfile = NetcdfFile.openInMemory(copyPath.toString());
            tiePointGrid.add(getGrid(ncfile, "Total_column_water_vapour_surface"));
            tiePointGrid.add(getGrid(ncfile, "Total_column_ozone_surface"));
            tiePointGrid.add(getGrid(ncfile, "Mean_sea_level_pressure_surface"));
        } catch (IOException ioe) {
            // Handle less-cool exceptions here
            ioe.printStackTrace();
        } catch (Exception e) {
            // Handle less-cool exceptions here
            e.printStackTrace();
        } finally {
            ncfile.close();
            FileUtils.deleteDirectory(cacheFolderPath.toFile());
        }
    }

    public List<TiePointGrid> getECMWFGrids() {
        return tiePointGrid;
    }

    public TiePointGrid getGrid(NetcdfFile ncfile, String name) throws IOException, FactoryException, TransformException {
       
        Variable variable = ncfile.findVariable(null, name);
        String description = variable.getDescription();
        if( description.contains("@"))
            description = description.split(" @")[0]+" at surface level provided by ECMWF";
        String units = variable.getUnitsString();
        int[] shape = variable.getShape();
        float[] tiePoints = (float[])variable.read().get1DJavaArray(float.class);
        TiePointGrid tiePointGrid = new TiePointGrid(name.toLowerCase().replace("_surface",""), shape[1],shape[2], 0.5, 0.5, 1220, 1220,tiePoints);
        tiePointGrid.setUnit(units);
        tiePointGrid.setNoDataValue(Double.NaN);
        tiePointGrid.setNoDataValueUsed(true);
        tiePointGrid.setDescription(description);
        return tiePointGrid;
    }
}
