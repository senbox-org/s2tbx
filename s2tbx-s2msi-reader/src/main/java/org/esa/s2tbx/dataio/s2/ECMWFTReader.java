package org.esa.s2tbx.dataio.s2;

import org.apache.commons.io.FileUtils;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.util.SystemUtils;

import ucar.ma2.InvalidRangeException;
import ucar.ma2.Range;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ECMWFTReader {

    static Logger logger = SystemUtils.LOG;

    List<TiePointGrid> tiePointGrids;

    public ECMWFTReader(Path path, Path cachedir, String tileId) throws IOException {
        tiePointGrids = new ArrayList<>();
        NetcdfFile ncfile = null;
        if(!tileId.isEmpty())
            tileId = "_" + tileId;
        final Path cacheFolderPath = cachedir.resolve("aux_ecmwft"+tileId);
        try {
            Files.createDirectory(cacheFolderPath);
        } catch (FileAlreadyExistsException exc) {
        }
        try {
            final Path copyPath = cacheFolderPath.resolve(path.getFileName().toString());
            try {
                Files.createDirectory(copyPath);
            } catch (FileAlreadyExistsException exc) {
            }
            Files.copy(path, copyPath, StandardCopyOption.REPLACE_EXISTING);
            ncfile = NetcdfFile.openInMemory(copyPath.toString());
            List<GridPair> gridList = new ArrayList<GridPair>();
            gridList.add(new GridPair("Total_column_water_vapour_surface","tcwv"+tileId));
            gridList.add(new GridPair("Total_column_ozone_surface","tco3"+tileId));
            gridList.add(new GridPair("Mean_sea_level_pressure_surface","msl"+tileId));
            gridList.add(new GridPair("Relative_humidity_isobaric","r"+tileId));
            gridList.add(new GridPair("10_metre_U_wind_component_surface","10u"+tileId));
            gridList.add(new GridPair("10_metre_V_wind_component_surface","10v"+tileId));
            // we add the coordinates as they do not match the corners of the MSI image
            gridList.add(new GridPair("lat", "aux_latitude"+tileId));
            gridList.add(new GridPair("lon", "aux_longitude"+tileId));
            for(GridPair gridPair:gridList) {
                TiePointGrid tpGrid = getGrid(ncfile, gridPair);
                if (tpGrid != null) {
                    this.tiePointGrids.add(tpGrid);
                }
            }
        } catch (Exception ioe) {
            // Handle less-cool exceptions here
            ioe.printStackTrace();
        } finally {
            if(ncfile!=null)
                ncfile.close();
            try{
                FileUtils.deleteDirectory(cacheFolderPath.toFile());
            }catch(IOException ioe)
            {
                logger.warning("The aux data folder cache has encounterd an issue: "+ioe.getMessage());
            }
            
        }
    }

    public List<TiePointGrid> getECMWFGrids() {
        return tiePointGrids;
    }

    public TiePointGrid getGrid(NetcdfFile ncfile, GridPair gridPair) throws IOException, InvalidRangeException {
        final Variable variable = ncfile.findVariable(null, gridPair.getKey());
        if (variable == null) {
            return null;
        }
        String description = variable.getDescription();
        if(description != null && description.contains("@"))
            description = description.split(" @")[0] + " at surface level provided by ECMWF";
        String units = variable.getUnitsString();
        final int[] shape = variable.getShape();
        float[] tiePoints = null;
        int width;
        int height;
        if(shape.length == 4)
        {
            width = shape[2];
            height = shape[3];
            int[] shape2 = new int[]{1,1,width,height};
            int[] origin = new int[]{0,0,0,0};
            tiePoints = (float[])variable.read(origin, shape2).getStorage();
        } else {
            width = shape.length > 1 ? shape[1] : shape[0];
            height = shape.length > 2 ? shape[2] : shape[0];
            tiePoints = (float[])variable.read().getStorage();
        }
        final TiePointGrid tiePointGrid;
        if (shape.length >= 3) {
            // atmospheric data bands
            tiePointGrid = new TiePointGrid(gridPair.name, width, height, 0.5, 0.5, 1220, 1220, tiePoints);
        } else if ("lat".equals(gridPair.getKey())) {
            // lat band
            final float[] tiePoints2D = new float[shape[0] * shape[0]];
            for (int j=0; j<shape[0]; ++j) {
                for (int i=0; i<shape[0]; ++i) {
                    tiePoints2D[j*shape[0]+i] = tiePoints[j];
                }
            }
            tiePointGrid = new TiePointGrid(gridPair.name, width, height, 0.5, 0.5, 1220, 1220, tiePoints2D);
        } else {
            // lon band
            final float[] tiePoints2D = new float[shape[0] * shape[0]];
            for (int i=0; i<shape[0]; ++i) {
                System.arraycopy(tiePoints, 0, tiePoints2D, i*shape[0], shape[0]);
            }
            tiePointGrid = new TiePointGrid(gridPair.name, width, height, 0.5, 0.5, 1220, 1220, tiePoints2D);
        }
        tiePointGrid.setUnit(units);
        tiePointGrid.setNoDataValue(Double.NaN);
        tiePointGrid.setNoDataValueUsed(true);
        tiePointGrid.setDescription(description);
        return tiePointGrid;
    }

    public class GridPair {
        private String key;
        private String name;
    
        public GridPair(String key, String name) {
            this.key = key;
            this.name = name;
        }

        protected final String getKey() {
            return key;
        }

        protected final String getName() {
            return name;
        }

    }
}
