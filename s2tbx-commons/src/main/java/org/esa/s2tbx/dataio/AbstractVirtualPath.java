package org.esa.s2tbx.dataio;

import com.bc.ceres.core.VirtualDir;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by jcoravu on 9/4/2019.
 */
public abstract class AbstractVirtualPath extends VirtualDir {

    protected AbstractVirtualPath() {
    }

    public abstract <ResultType> ResultType loadData(String path, ICallbackCommand<ResultType> command) throws IOException;

    public FileSystem newFileSystem() throws IOException {
        throw new UnsupportedOperationException();
    }

}
