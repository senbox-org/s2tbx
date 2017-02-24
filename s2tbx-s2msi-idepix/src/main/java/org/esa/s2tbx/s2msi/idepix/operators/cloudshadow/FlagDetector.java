package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

/**
 * todo: add comment
 *
 */
public interface FlagDetector {

    boolean isLand(int x, int y);
    boolean isCloud(int x, int y);
//    boolean isOcean(int x, int y);
    boolean isInvalid(int x, int y);

}
