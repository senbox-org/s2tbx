package org.esa.s2tbx.dataio;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jcoravu on 9/4/2019.
 */
public interface ICallbackCommand<ResultType> {

    public ResultType execute(Path path) throws IOException;
}
