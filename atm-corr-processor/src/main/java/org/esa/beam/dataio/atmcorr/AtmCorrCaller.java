package org.esa.beam.dataio.atmcorr;

import java.io.*;
import java.util.*;

/**
 *
 * @author Tonio Fincke
 */
public class AtmCorrCaller {

    public void call(String l1cProductPath, int resolution) throws IOException {
        List<String> command = new ArrayList<String>();
        command.add("python");
        command.add("L2A_Process.py");
        command.add(l1cProductPath);
        if(resolution > -1) {
            command.add("--resolution");
            command.add("" + resolution);
        }
        String apphome = System.getenv("S2L2APPHOME");
        String applicationPath = apphome + "/src";
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File(applicationPath));
        Process start = processBuilder.start();
        InputStream inputStream = start.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        while((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
        InputStream errorStream = start.getErrorStream();
        InputStreamReader errorReader = new InputStreamReader(errorStream);
        BufferedReader errorBufferedReader = new BufferedReader(errorReader);
        String errorLine;
        while((errorLine = errorBufferedReader.readLine()) != null) {
            System.out.println(errorLine);
        }
        System.out.println("Program terminated");
    }

}
