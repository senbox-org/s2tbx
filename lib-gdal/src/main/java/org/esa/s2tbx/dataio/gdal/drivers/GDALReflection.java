package org.esa.s2tbx.dataio.gdal.drivers;

import org.esa.s2tbx.dataio.gdal.GDALLoader;

import java.lang.reflect.Method;

class GDALReflection {

    private GDALReflection() {
        //nothing to init
    }

    static <T> T fetchGDALLibraryConstant(String className, String constantName, Class<T> type) {
        try {
            Class<?> gdalconstConstantsClass = Class.forName(className, false, GDALLoader.getInstance().getGDALVersionLoader());
            return type.cast(gdalconstConstantsClass.getField(constantName).get(null));
        } catch (Exception ignored) {
            //nothing to do
        }
        return null;
    }

    static <T> T callGDALLibraryMethod(String className, String methodName, Class<T> returnType, Object instance, Class[] argumentsTypes, Object[] arguments) {
        try {
            Class<?> gdalClass = Class.forName(className, false, GDALLoader.getInstance().getGDALVersionLoader());
            Method gdalClassMethod = gdalClass.getMethod(methodName, argumentsTypes);
            Object returnResult = gdalClassMethod.invoke(instance, arguments);
            if (returnResult != null && returnType != null) {
                return returnType.cast(returnResult);
            }
        } catch (Exception ignored) {
            //nothing to do
        }
        return null;
    }
}
