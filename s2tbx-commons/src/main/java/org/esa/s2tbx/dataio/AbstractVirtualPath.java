package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;

import java.io.IOException;

/**
 * Created by jcoravu on 9/4/2019.
 */
public abstract class AbstractVirtualPath extends VirtualDir {

    protected AbstractVirtualPath() {

    }

    public <ResultType> ResultType loadData(String path, ICallbackCommand<ResultType> command) throws IOException {
        return null;
    }
}
