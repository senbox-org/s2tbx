/*
 * Copyright (C) 2014-2015 CS-SI (foss-contact@thor.si.c-s.fr)
 * Copyright (C) 2013-2015 Brockmann Consult GmbH (info@brockmann-consult.de)
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

package org.esa.s2tbx.dataio.s2.structure;

public class S2ProductStructureFactory {
    public static enum ProductType {S2TILE, S2DATASTRIP, S2L0DATASTRIP, S2L0TILE, S2L0PRODUCT, S2L1APRODUCT, S2L1BPRODUCT, S2L1CPRODUCT}

    ;

    public static S2ProductStructure create(ProductType pt) throws Exception {
        if (pt == ProductType.S2TILE) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "IMG_DATA"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "QI_DATA"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "AUX_DATA"));
            return s2;
        }

        if (pt == ProductType.S2DATASTRIP) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "QI_DATA"));
            return s2;
        }

        if (pt == ProductType.S2L0DATASTRIP) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "QI_DATA"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "ANC_DATA"));
            return s2;
        }

        if (pt == ProductType.S2L0TILE) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "IMG_DATA"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "QI_DATA"));
            return s2;
        }

        if (pt == ProductType.S2L0PRODUCT) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, false, "manifest.safe"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "GRANULE"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "DATASTRIP"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "AUX_DATA"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "ANC_DATA"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.DIRECTORY, false, "rep_info"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, false, "INSPIRE.xml"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "HTML"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)\\.(PNG|png)"));

            return s2;
        }

        if (pt == ProductType.S2L1APRODUCT) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, false, "manifest.safe"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "GRANULE"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "DATASTRIP"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "AUX_DATA"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.DIRECTORY, false, "rep_info"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, false, "INSPIRE.xml"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "HTML"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)\\.(PNG|png)"));

            return s2;
        }

        if (pt == ProductType.S2L1BPRODUCT) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, false, "manifest.safe"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "GRANULE"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "DATASTRIP"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "AUX_DATA"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.DIRECTORY, false, "rep_info"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, false, "INSPIRE.xml"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "HTML"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)\\.(PNG|png)"));

            return s2;
        }

        if (pt == ProductType.S2L1CPRODUCT) {
            S2ProductStructure s2 = new S2ProductStructure();
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)(\\.[A-Z|a-z|0-9]{3})?"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, false, "manifest.safe"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "GRANULE"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "DATASTRIP"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "AUX_DATA"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.DIRECTORY, false, "rep_info"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.FILE, false, "INSPIRE.xml"));
            s2.addItem(new StructuralItem(false, StructuralItem.Type.DIRECTORY, false, "HTML"));
            s2.addItem(new StructuralItem(true, StructuralItem.Type.FILE, true, "(S2A|S2B|S2_)_([A-Z|0-9]{4})_([A-Z|0-9|_]{4})([A-Z|0-9|_]{6})_([A-Z|0-9|_]+)\\.(PNG|png)"));

            return s2;
        }

        throw new Exception("Unexpected type");
    }
}
