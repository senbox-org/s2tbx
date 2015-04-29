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

package jp2;

import jp2.segments.IgnoredSegment;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

/**
 * @author Norman Fomferra
 */
public class CodestreamReader {
    ImageInputStream stream;
    private final long position;
    long length;

    boolean init;

    public CodestreamReader(ImageInputStream stream, long position, long length) {
        this.stream = stream;
        this.position = position;
        this.length = length;

    }

    public MarkerSegment readSegment() throws IOException {
        if (!init) {
            init = true;
            stream.seek(position);
        }

        final int code = stream.readShort() & 0x0000ffff;

        //stream.seek(position + length - 2);

        final MarkerType markerType = MarkerType.get(code);
        if (markerType != null) {
            final MarkerSegment segment = markerType.createSegment();
            segment.readFrom(stream);
            return segment;
        } else {
            final MarkerSegment segment = new IgnoredSegment(code);
            segment.readFrom(stream);
            return segment;
        }
    }
}
