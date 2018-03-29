package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

/**
 * todo: add comment
 *
 */
public class CloudVerticalExtent {

    static double[] getCloudVerticalExtentSentinel2() {

        double[] cloudExtent = new double[2];
        double cloudBase;
        double cloudTop;

        // todo cloud height properties
        cloudBase = S2IdepixPreCloudShadowOp.mincloudBase;// [m]
        cloudTop  = S2IdepixPreCloudShadowOp.maxcloudTop; // [m]

        //cloud top and cloud base height in [m]
        cloudExtent[0]= cloudBase;
        cloudExtent[1]= cloudTop;

        return cloudExtent;
    }

}
