package org.esa.s2tbx.dataio.readers;

import org.esa.snap.core.metadata.XmlMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcoravu on 23/1/2020.
 */
public class MetadataList<MetadataType extends XmlMetadata> {

    private final List<Pair<MetadataType, String>> metadataList;

    public MetadataList() {
        this.metadataList = new ArrayList<>();
    }

    public void addMetadata(MetadataType metadata, String imageRelativePath) {
        this.metadataList.add(new Pair(metadata, imageRelativePath));
    }

    public int getCount() {
        return this.metadataList.size();
    }

    public MetadataType getMetadataAt(int index) {
        return this.metadataList.get(index).getFirst();
    }

    public String getMetadataImageRelativePath(int index) {
        return this.metadataList.get(index).getSecond();
    }

    private static class Pair<First, Second> {

        private final First first;
        private final Second second;

        Pair(First first, Second second) {
            this.first = first;
            this.second = second;
        }

        public First getFirst() {
            return this.first;
        }

        public Second getSecond() {
            return this.second;
        }
    }
}
