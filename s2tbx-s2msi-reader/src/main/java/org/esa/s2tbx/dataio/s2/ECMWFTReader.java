package org.esa.s2tbx.dataio.s2;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Doubles;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.esa.snap.core.dataio.geocoding.ComponentFactory;
import org.esa.snap.core.dataio.geocoding.ComponentGeoCoding;
import org.esa.snap.core.dataio.geocoding.ForwardCoding;
import org.esa.snap.core.dataio.geocoding.GeoChecks;
import org.esa.snap.core.dataio.geocoding.GeoRaster;
import org.esa.snap.core.dataio.geocoding.InverseCoding;
import org.esa.snap.core.dataio.geocoding.forward.PixelForward;
import org.esa.snap.core.dataio.geocoding.inverse.PixelGeoIndexInverse;
import org.esa.snap.core.dataio.geocoding.inverse.PixelQuadTreeInverse;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.CrsGeoCoding;
import org.esa.snap.core.datamodel.GeoCoding;
import org.esa.snap.core.datamodel.PixelPos;
import org.esa.snap.core.datamodel.ProductData;
import org.esa.snap.core.datamodel.TiePointGrid;
import org.esa.snap.core.transform.MathTransform2D;
import org.esa.snap.core.util.ImageUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

public class ECMWFTReader {

    List<Band> bands;

    public ECMWFTReader(Path path, Path cachedir) throws IOException {
        if (bands != null)
            bands.clear();
        bands = new ArrayList<>();
        NetcdfFile ncfile = null;
        System.out.println("getECMWFBands cachedir: " + cachedir.toAbsolutePath());
        Path cacheFolderPath = cachedir;
        cacheFolderPath = cacheFolderPath.resolve("aux_ecmfwt");
        try {
            Files.createDirectory(cacheFolderPath);
        } catch (FileAlreadyExistsException exc) {
        }
        Path copyPath = cacheFolderPath.resolve(path.getFileName());
        try {
            System.out.println("cacheFolderPath: " + cacheFolderPath.toAbsolutePath());
            Files.copy(path, copyPath, StandardCopyOption.REPLACE_EXISTING);
            ncfile = NetcdfFile.openInMemory(copyPath.toString());
            bands.add(getBand(ncfile, "Total_column_water_vapour_surface"));
            bands.add(getBand(ncfile, "Total_column_ozone_surface"));
            bands.add(getBand(ncfile, "Mean_sea_level_pressure_surface"));
        } catch (IOException ioe) {
            // Handle less-cool exceptions here
            ioe.printStackTrace();
        } catch (Exception e) {
            // Handle less-cool exceptions here
            e.printStackTrace();
        } finally {
            ncfile.close();
            System.out.println("FileUtils.deleteDirectory ");
            FileUtils.deleteDirectory(cacheFolderPath.toFile());
        }
    }

    public List<Band> getECMWFBands() throws IOException {
        return bands;
    }

    public Band getBand(NetcdfFile ncfile, String name) throws IOException, FactoryException, TransformException {
       
        Variable variable = ncfile.findVariable(null, name);
        Variable reftimeVar = ncfile.findVariable(null, "reftime");
        Variable lon = ncfile.findVariable(null, "lon");
        Variable lat = ncfile.findVariable(null, "lat");
        String description = variable.getDescription();
        String units = variable.getUnitsString();
        
        int[] shape = variable.getShape();
        double reftime = reftimeVar.readScalarDouble();
        String unitsTime = reftimeVar.getUnitsString();
        // ProductData.UTC startTime = ProductData.UTC.parse("01-01-2010", "dd-MM-yyyy");
        Band band = new Band(name,ProductData.TYPE_FLOAT64,shape[1],shape[2]);
        band.setUnit(units);
        band.setDescription(description);
        band.setNoDataValue(Double.NaN);
        band.setNoDataValueUsed(true);
        System.out.println("getGlobalAttributes : "+ncfile.getGlobalAttributes());
        // System.out.println("getVariables : "+ncfile.getVariables());
        if(unitsTime.contains("since "))
            unitsTime=unitsTime.split("since ")[1];
        System.out.println("latitude : "+lon.read());
        System.out.println("longitude : "+lat.read());
        
        double[] longitude = (double[])lon.read().get1DJavaArray(double.class);
        double[] longitudeLine = (double[])lon.read().get1DJavaArray(double.class);
        for(int i=1;i<shape[2];i++)
        {
            longitude = Doubles.concat(longitude, longitudeLine);
        }
        
        double[] latitude = new double[shape[1]];
        Arrays.fill(latitude, lat.read().getDouble(0));
        for(int i=0;i<shape[2];i++)
        {
            double[] latitudeLine = new double[shape[1]];
            Arrays.fill(latitudeLine, lat.read().getDouble(i));
            latitude = Doubles.concat(latitude, latitudeLine);
        }
        
        final ForwardCoding forward = ComponentFactory.getForward(PixelForward.KEY);
        final InverseCoding inverse = ComponentFactory.getInverse(PixelGeoIndexInverse.KEY);
        final GeoRaster geoRaster = new GeoRaster(longitude,latitude, "lon", "lat", shape[1],shape[2], 1.25);
        CoordinateReferenceSystem mapCRS = CRS.decode("EPSG:4326");
        final ComponentGeoCoding geoCoding = new ComponentGeoCoding(geoRaster, forward, inverse, GeoChecks.NONE,mapCRS);
        geoCoding.initialize();


        // double[] longitude = (double[])lon.read().get1DJavaArray(double.class);
        // double[] latitude = (double[])lat.read().get1DJavaArray(double.class);
        // CoordinateReferenceSystem mapCRS = CRS.decode("EPSG:4326");
        // GeoCoding geoCoding = ImageUtils.buildCrsGeoCoding( longitude[0],latitude[0],  longitude[1]-longitude[0],latitude[0]-latitude[1], new Dimension(shape[1],shape[2]),mapCRS , new Rectangle(0, 0, shape[1], shape[2]));
        // 

        // System.out.println("longitude: "+ArrayUtils.toString(longitude));
        // System.out.println("latitude: "+ArrayUtils.toString(latitude));
        System.out.println("geoCoding 0 0 : "+geoCoding.getGeoPos(new PixelPos(0,0), null));
        System.out.println("geoCoding 8 8 : "+geoCoding.getGeoPos(new PixelPos(8,8), null));

        // band.setTimeCoding(timeCoding);
        // band.setModelToSceneTransform(MathTransform2D.NULL);
        band.setGeoCoding(geoCoding);

        ProductData tileData = ProductData.createInstance(ProductData.TYPE_FLOAT64,shape[1]*shape[2]);
        Array raster = variable.read();
        
        for(int i=0;i<raster.getSize();i++)
        {
            tileData.setElemFloatAt(i, raster.getFloat(i));
        }
        band.setRasterData(tileData);     
        return band;
    }
}
