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

package org.esa.s2tbx.dataio.spot.dimap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * SAX parser for DIMAP volume metadata file.
 *
 * @author Cosmin Cara
 */
class VolumeMetadataParser {

    private static class VolumeMetadataHandler extends DefaultHandler {
        private VolumeMetadata result;
        private VolumeComponent currentComponent;
        private String buffer;

        VolumeMetadata getResult() {
            return result;
        }

        @Override
        public void startDocument() throws SAXException {
            result = new VolumeMetadata();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            buffer = "";
            if (qName.equals(SpotConstants.TAG_VOL_METADATA_FORMAT)) {
                result.formatVersion = attributes.getValue(SpotConstants.ATTR_VERSION);
            }
            if (qName.equals(SpotConstants.TAG_VOL_PRODUCER_URL)) {
                result.producerURL = attributes.getValue(SpotConstants.ATTR_HREF);
            }
            if (qName.equals(SpotConstants.TAG_VOL_COMPONENT)) {
                currentComponent = new VolumeComponent();
            }
            if (qName.equals(SpotConstants.TAG_VOL_COMPONENT_PATH)) {
                currentComponent.path = attributes.getValue(SpotConstants.ATTR_HREF);
            }
            if (qName.equals(SpotConstants.TAG_VOL_COMPONENT_TN_PATH)) {
                currentComponent.thumbnailPath = attributes.getValue(SpotConstants.ATTR_HREF);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals(SpotConstants.TAG_VOL_DATASET_NAME)) {
                result.datasetName = buffer;
            } else if (qName.equals(SpotConstants.TAG_VOL_DATASET_PRODUCER_NAME)) {
                result.producerName = buffer;
            } else if (qName.equals(SpotConstants.TAG_VOL_DATASET_PRODUCTION_DATE)) {
                try {
                    result.productionDate = new SimpleDateFormat(SpotConstants.UTC_DATE_FORMAT).parse(buffer);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (qName.equals(SpotConstants.TAG_VOL_COMPONENT)) {
                if (SpotConstants.DIMAP.equals(currentComponent.getType())) {
                    result.components.add(currentComponent);
                }
            } else if (qName.equals(SpotConstants.TAG_VOL_COMPONENT_TITLE)) {
                currentComponent.title = buffer;
                int idx = currentComponent.title.lastIndexOf(",");
                if (idx > 0) {
                    String tmp = currentComponent.title.substring(idx + 1).trim().replace("T_", "");
                    String[] tokens = tmp.split("_");
                    currentComponent.index = new int[]{Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])};
                }
            } else if (qName.equals(SpotConstants.TAG_VOL_COMPONENT_TYPE)) {
                currentComponent.type = buffer;
            } else if (qName.equals(SpotConstants.TAG_VOL_METADATA_PROFILE)) {
                result.profileName = buffer;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            buffer = new String(ch, start, length);
        }
    }

    static VolumeMetadata parse(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        VolumeMetadataHandler handler = new VolumeMetadataHandler();
        parser.parse(inputStream, handler);

        return handler.getResult();
    }
}
