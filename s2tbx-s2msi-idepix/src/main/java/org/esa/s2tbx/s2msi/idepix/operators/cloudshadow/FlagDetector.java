package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow;

/**
 * todo: add comment
 * todo: we should try to get rid of this
 *
 */
public interface FlagDetector {

    boolean isLand(int x, int y);
    boolean isCloud(int x, int y);
    boolean isCloudBuffer(int x, int y);
    boolean isInvalid(int x, int y);

}
