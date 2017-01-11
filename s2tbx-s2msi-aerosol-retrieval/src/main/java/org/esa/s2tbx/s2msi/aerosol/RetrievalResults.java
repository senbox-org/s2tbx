/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esa.s2tbx.s2msi.aerosol;

/**
 *
 * @author akheckel
 */
class RetrievalResults {
    private final boolean retrievalFailed;
    private final float optAOT;
    private final float optErr;
    private final float retrievalErr;
    private final float curvature;

    public RetrievalResults(boolean retrievalFailed, float optAOT, float optErr, float retrievalErr, float curv) {
        this.retrievalFailed = retrievalFailed;
        this.optAOT = optAOT;
        this.optErr = optErr;
        this.retrievalErr = retrievalErr;
        this.curvature = curv;
    }

    public float getOptAOT() {
        return optAOT;
    }

    public float getRetrievalErr() {
        return retrievalErr;
    }

    public boolean isRetrievalFailed() {
        return retrievalFailed;
    }

}
