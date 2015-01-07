package org.esa.beam.dataio.s2;

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

    public static Predicate<ImageInfo> isGranule(String granuleId)
    {
        // todo get rid of hardcoded strings...
        return p -> p.get("GranuleIdentifier").equals(granuleId);
    }

    public static Predicate<ImageInfo> isJPEG2000()
    {
        return p -> p.get("ImageFormat").equals("JPEG2000");
    }

    public static List<ImageInfo> filterImageInfo (Collection<ImageInfo> images, Predicate<ImageInfo> predicate)
    {
        return images.stream().filter( predicate ).collect(Collectors.<ImageInfo>toList());
    }

    public static List<ImageInfo> filterImageInfo (Collection<ImageInfo> images, Predicate<ImageInfo> predicate, Predicate<ImageInfo> otherPredicate)
    {
        List<Predicate<ImageInfo>> predicates = new ArrayList<>();
        predicates.add(predicate);
        predicates.add(otherPredicate);
        return filterImageInfo(images, predicates);
    }

    public static List<ImageInfo> filterImageInfo (Collection<ImageInfo> images, Predicate<ImageInfo> predicate, Predicate<ImageInfo> otherPredicate, Predicate<ImageInfo> thirdPredicate)
    {
        List<Predicate<ImageInfo>> predicates = new ArrayList<>();
        predicates.add(predicate);
        predicates.add(otherPredicate);
        predicates.add(thirdPredicate);
        return filterImageInfo(images, predicates);
    }

    public static List<ImageInfo> filterImageInfo (Collection<ImageInfo> images, List<Predicate<ImageInfo>> predicates)
    {
        Collection<ImageInfo> intermediate = images;
        for (Predicate<ImageInfo> aPredicate: predicates)
        {
            intermediate = intermediate.stream().filter( aPredicate ).collect(Collectors.<ImageInfo>toList());
        }
        return intermediate.stream().collect(Collectors.<ImageInfo>toList());
    }
}
