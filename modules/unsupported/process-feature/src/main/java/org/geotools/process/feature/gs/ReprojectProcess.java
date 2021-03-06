/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2001-2007 TOPP - www.openplans.org.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.process.feature.gs;

import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;
import org.geotools.data.crs.ForceCoordinateSystemFeatureResults;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.store.ReprojectingFeatureCollection;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Will reproject the features to another CRS. Can also be used to force a known CRS onto a dataset
 * that does not have ones
 * 
 * @author Andrea Aime
 *
 * @source $URL$
 */
@DescribeProcess(title = "reprojectFeatures", description = "Reprojects the specified features to another CRS, can also be used to force a known CRS onto a set of feaures that miss one (or that have a wrong one)")
public class ReprojectProcess implements GSProcess {

    @DescribeResult(name = "result", description = "The reprojected features")
    public SimpleFeatureCollection execute(
            @DescribeParameter(name = "features", description = "The feature collection that will be reprojected") SimpleFeatureCollection features,
            @DescribeParameter(name = "forcedCRS", min = 0, description = "Forces a certain CRS on features before reprojection") CoordinateReferenceSystem forcedCRS,
            @DescribeParameter(name = "targetCRS", min = 0, description = "Features will be reprojected from their native/forced CRS to the target CRS") CoordinateReferenceSystem targetCRS)
            throws Exception {

        if (forcedCRS != null) {
            features = new ForceCoordinateSystemFeatureResults(features, forcedCRS, false);
        }
        if (targetCRS != null) {
            // note, using ReprojectFeatureResults would work. However that would
            // just work by accident... see GEOS-4072
            features = new ReprojectingFeatureCollection(features, targetCRS);
        }
        return features;
    }

}
