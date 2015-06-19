/*
 *
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

package org.esa.s2tbx.dataio.s2.l1c;

import org.esa.snap.framework.datamodel.SceneRasterTransform;
import org.opengis.referencing.operation.MathTransform2D;

/**
 * Created by opicas-p on 06/05/2015.
 */
public class S2SceneRasterTransform implements SceneRasterTransform {
    private final MathTransform2D forward;
    private final MathTransform2D inverse;

    public S2SceneRasterTransform(MathTransform2D forward, MathTransform2D inverse) {
        this.forward = forward;
        this.inverse = inverse;
    }

    @Override
    public MathTransform2D getForward() {
        return forward;
    }

    @Override
    public MathTransform2D getInverse() {
        return inverse;
    }
}
