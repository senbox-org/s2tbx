/*
 *
 * Copyright (C) 2013-2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 * Copyright (C) 2014-2015 CS SI
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *
 */

package org.esa.s2tbx.dataio.s2.l2a;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by opicas-p on 09/12/2014.
 */
public class ImageInfoPredicates {

    public static Predicate<ImageInfo> isBand(String band) {

        return p -> p.getFileName().contains(band);
    }

    public static Predicate<ImageInfo> isGranule(String granuleId) {
        // todo get rid of hardcoded strings...
        return p -> p.get("GranuleIdentifier").equals(granuleId);
    }

    public static Predicate<ImageInfo> isJPEG2000() {
        return p -> p.get("ImageFormat").equals("JPEG2000");
    }

    public static List<ImageInfo> filterImageInfo(Collection<ImageInfo> images, Predicate<ImageInfo> predicate) {
        return images.stream().filter(predicate).collect(Collectors.<ImageInfo>toList());
    }

    public static List<ImageInfo> filterImageInfo(Collection<ImageInfo> images, Predicate<ImageInfo> predicate, Predicate<ImageInfo> otherPredicate) {
        List<Predicate<ImageInfo>> predicates = new ArrayList<>();
        predicates.add(predicate);
        predicates.add(otherPredicate);
        return filterImageInfo(images, predicates);
    }

    public static List<ImageInfo> filterImageInfo(Collection<ImageInfo> images, Predicate<ImageInfo> predicate, Predicate<ImageInfo> otherPredicate, Predicate<ImageInfo> thirdPredicate) {
        List<Predicate<ImageInfo>> predicates = new ArrayList<>();
        predicates.add(predicate);
        predicates.add(otherPredicate);
        predicates.add(thirdPredicate);
        return filterImageInfo(images, predicates);
    }

    public static List<ImageInfo> filterImageInfo(Collection<ImageInfo> images, List<Predicate<ImageInfo>> predicates) {
        Collection<ImageInfo> intermediate = images;
        for (Predicate<ImageInfo> aPredicate : predicates) {
            intermediate = intermediate.stream().filter(aPredicate).collect(Collectors.<ImageInfo>toList());
        }
        return intermediate.stream().collect(Collectors.<ImageInfo>toList());
    }
}
