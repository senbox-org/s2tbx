package org.esa.s2tbx.s2msi.aerosol;

/**
 * todo: add comment
 * To change this template use File | Settings | File Templates.
 * Date: 12.01.2017
 * Time: 15:44
 *
 * @author olafd
 */
public class S2AerosolConstants {
    public static final double RHO_REFERENCE = 0.15;
    public static final double[] K_RAY = {1.335542192182526E-13, 8.850846506862589E-14, 5.133075658166496E-14,
            2.546082681762043E-14, 2.0061953450282244E-14, 1.6463395039117695E-14, 1.3074853352226656E-14,
            9.72101207049594E-15, 8.708815646765025E-15, 6.070480061252796E-15, 1.3143463008748833E-15,
            6.904547814781845E-16, 1.967763167294875E-16};
    public final static double OZONE_STANDARD = 0.33176;
    public final static double PRESSURE_STANDARD = 1013.25;
    public final static double[] OZONE_ABSORPTION_COEFFICENTS_PER_S2_BAND =
            new double[]{0.0024, 0.021, 0.103, 0.0525, 0.0205, 0.01, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
}
