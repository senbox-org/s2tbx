/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2014-2015 CS-Romania (office@c-s.ro)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 */

package org.esa.s2tbx.dataio.jp2.metadata;

import org.esa.snap.core.datamodel.MetadataElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kraftek on 7/15/2015.
 */
public class OpjDumpFile {

    private Path file;
    private ImageInfo imageInfo;
    private CodeStreamInfo codeStreamInfo;
    private boolean isParsed;

    public OpjDumpFile(Path dumpFile) {
        file = dumpFile;
    }

    public String getPath() {
        return file != null ? file.toString() : null;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public CodeStreamInfo getCodeStreamInfo() { return codeStreamInfo; }

    public MetadataElement toMetadataElement() {
        if (!isParsed) {
            parse();
        }
        MetadataElement element = new MetadataElement("JP2 Metadata");
        if (imageInfo != null) {
            element.addElement(imageInfo.toMetadataElement());
        }
        if (codeStreamInfo != null) {
            element.addElement(codeStreamInfo.toMetadataElement());
        }
        return element;
    }

    public void parse() {
        if (file != null && Files.exists(file) && Files.isReadable(file)) {
            try {
                List<String> lines = Files.readAllLines(file);
                Iterator<String> iterator = lines.iterator();
                String currentLine;
                while(iterator.hasNext()) {
                    currentLine = iterator.next();
                    if (currentLine.startsWith("Image info")) {
                        extractImageInfo(iterator);
                    } else if (currentLine.startsWith("Codestream info")) {
                        extractCodeStreamInfo(iterator);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isParsed = true;
    }

    private void extractImageInfo(Iterator<String> iterator) {
        String currentLine;
        imageInfo = new ImageInfo();
        Map<String, String> values = new HashMap<>();
        while (iterator.hasNext()) {
            currentLine = iterator.next();
            if (currentLine.endsWith("{")) {
                extractImageInfoComponent(iterator);
            } else if (currentLine.endsWith("}")) {
                break;
            } else {
                String[] tokens = currentLine.replace("\t", "").split(", ");
                for (String token : tokens) {
                    if (token.contains("=")) {
                        values.put(token.substring(0, token.indexOf("=")).trim(), token.substring(token.indexOf("=") + 1));
                    }
                }
            }
        }
        for (String key : values.keySet()) {
            switch (key) {
                case "x0":
                    imageInfo.setX0(Integer.parseInt(values.get(key)));
                    break;
                case "y0":
                    imageInfo.setY0(Integer.parseInt(values.get(key)));
                    break;
                case "x1":
                    imageInfo.setWidth(Integer.parseInt(values.get(key)));
                    break;
                case "y1":
                    imageInfo.setHeight(Integer.parseInt(values.get(key)));
                    break;
            }
        }
    }

    private void extractImageInfoComponent(Iterator<String> iterator) {
        String currentLine;
        int dx = 0, dy = 0, prec = 8;
        boolean sgnd = false;
        Map<String, String> values = new HashMap<>();
        while (iterator.hasNext()) {
            currentLine = iterator.next();
            if (currentLine.endsWith("}")) {
                break;
            } else {
                String[] tokens = currentLine.replace("\t", "").split(", ");
                for (String token : tokens) {
                    if (token.contains("=")) {
                        values.put(token.substring(0, token.indexOf("=")).trim(), token.substring(token.indexOf("=") + 1));
                    }
                }
            }
        }
        for (String key : values.keySet()) {
            switch (key) {
                case "dx":
                    dx = Integer.parseInt(values.get(key));
                    break;
                case "dy":
                    dy = Integer.parseInt(values.get(key));
                    break;
                case "prec":
                    prec = Integer.parseInt(values.get(key));
                    break;
                case "sgnd":
                    sgnd = Integer.parseInt(values.get(key)) == 1;
                    break;
            }
        }
        imageInfo.addComponent(dx, dy, prec, sgnd);
    }

    private void extractCodeStreamInfo(Iterator<String> iterator) {
        String currentLine;
        codeStreamInfo = new CodeStreamInfo();
        Map<String, String> values = new HashMap<>();
        while (iterator.hasNext()) {
            currentLine = iterator.next();
            if (currentLine.contains("comp")) {
                extractTileComponent(iterator);
            } else if (currentLine.endsWith("}")) {
                break;
            } else {
                String[] tokens = currentLine.replace("\t", "").split(", ");
                for (String token : tokens) {
                    if (token.contains("=")) {
                        values.put(token.substring(0, token.indexOf("=")).trim(), token.substring(token.indexOf("=") + 1));
                    }
                }
            }
        }
        for (String key : values.keySet()) {
            switch (key) {
                case "tx0":
                    codeStreamInfo.setTx0(Integer.parseInt(values.get(key)));
                    break;
                case "ty0":
                    codeStreamInfo.setTy0(Integer.parseInt(values.get(key)));
                    break;
                case "tdx":
                    codeStreamInfo.setTileWidth(Integer.parseInt(values.get(key)));
                    break;
                case "tdy":
                    codeStreamInfo.setTileHeight(Integer.parseInt(values.get(key)));
                    break;
                case "tw":
                    codeStreamInfo.setNumTilesX(Integer.parseInt(values.get(key)));
                    break;
                case "th":
                    codeStreamInfo.setNumTilesY(Integer.parseInt(values.get(key)));
                    break;
                case "csty":
                    codeStreamInfo.setCsty(values.get(key));
                    break;
                case "prg":
                    codeStreamInfo.setPrg(values.get(key));
                    break;
                case "numlayers":
                    codeStreamInfo.setNumLayers(Integer.parseInt(values.get(key)));
                    break;
                case "mct":
                    codeStreamInfo.setMct(Integer.parseInt(values.get(key)));
                    break;
            }
        }
    }

    private void extractTileComponent(Iterator<String> iterator) {
        String currentLine;
        Map<String, String> values = new LinkedHashMap<>();
        CodeStreamInfo.TileComponentInfo tcInfo = codeStreamInfo.new TileComponentInfo();
        while (iterator.hasNext()) {
            currentLine = iterator.next();
            if (currentLine.endsWith("}")) {
                break;
            } else {
                String[] tokens = currentLine.replace("\t", "").split(", ");
                for (String token : tokens) {
                    if (token.contains("=")) {
                        values.put(token.substring(0, token.indexOf("=")).trim(), token.substring(token.indexOf("=") + 1));
                    }
                }
            }
        }
        for (String key : values.keySet()) {
            switch (key) {
                case "csty":
                    tcInfo.setCsty(values.get(key));
                    break;
                case "numresolutions":
                    tcInfo.setNumResolutions(Integer.parseInt(values.get(key)));
                    break;
                case "cblkw":
                    String[] sValues = values.get(key).split("\\^");
                    tcInfo.setCodeBlockWidth((int) Math.pow(Integer.parseInt(sValues[0]), Integer.parseInt(sValues[1])));
                    break;
                case "cblkh":
                    sValues = values.get(key).split("\\^");
                    tcInfo.setCodeBlockHeight((int) Math.pow(Integer.parseInt(sValues[0]), Integer.parseInt(sValues[1])));
                    break;
                case "cblksty":
                    tcInfo.setCodeBlockSty(Integer.parseInt(values.get(key)));
                    break;
                case "qmfbid":
                    tcInfo.setQmfbid(Integer.parseInt(values.get(key)));
                    break;
                case "preccintsize (w,h)":
                    String[] pairs = values.get(key).replace("(", "").replace(")", "").split(" ");
                    for (String pair : pairs) {
                        tcInfo.addPreccInt(Integer.parseInt(pair.substring(0, pair.indexOf(","))),
                                           Integer.parseInt(pair.substring(pair.indexOf(",") + 1)));
                    }
                    break;
                case "qntsty":
                    tcInfo.setQntsty(values.get(key));
                    break;
                case "numgbits":
                    tcInfo.setNumGBits(Integer.parseInt(values.get(key)));
                    break;
                case "stepsizes (m,e)":
                    pairs = values.get(key).replace("(", "").replace(")", "").split(" ");
                    for (String pair : pairs) {
                        tcInfo.addStepSize(Integer.parseInt(pair.substring(0, pair.indexOf(","))),
                                Integer.parseInt(pair.substring(pair.indexOf(",") + 1)));
                    }
                    break;
                case "roishift":
                    tcInfo.setRoiShift(Integer.parseInt(values.get(key)));
                    break;
            }
        }
        codeStreamInfo.addComponentTileInfo(tcInfo);
    }
}
