package org.esa.beam.dataio.atmcorr;

import java.io.*;
import java.util.*;

/**
 *
 * @author Tonio Fincke
 */
public class AtmCorrCaller {

    public static void main(String[] args) throws IOException {
        List<String> command = new ArrayList<String>();
        command.add("python");
        command.add("L2A_Process.py");
        command.add("/home/tonio/S2L2APP/testdata/Level-1C_User_Product");
        command.add("--resolution");
        command.add("60");
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(new File("/home/tonio/S2L2APP/src"));
        Map<String,String> environment = processBuilder.environment();
        Set<Map.Entry<String,String>> entries = environment.entrySet();
        Iterator<Map.Entry<String,String>> iterator = entries.iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
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
