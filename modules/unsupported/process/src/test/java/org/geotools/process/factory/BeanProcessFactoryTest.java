package org.geotools.process.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.geotools.data.Parameter;
import org.geotools.data.Query;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.FactoryIteratorProvider;
import org.geotools.factory.GeoTools;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.ProcessException;
import org.geotools.process.ProcessFactory;
import org.geotools.process.Processors;
import org.geotools.process.RenderingProcess;
import org.geotools.util.SimpleInternationalString;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequenceFactory;

/**
 * Tests some processes that do not require integration with the application context
 * 
 * @author Andrea Aime - OpenGeo
 * @author Martin Davis - OpenGeo
 * 
 *
 * @source $URL$
 */
public class BeanProcessFactoryTest extends TestCase {

    public class BeanProcessFactory extends AnnotatedBeanProcessFactory {

        public BeanProcessFactory() {
            super(new SimpleInternationalString("Some bean based processes custom processes"),
                    "bean", 
                    IdentityProcess.class,
                    VectorIdentityRTProcess.class);
        }

    }

    BeanProcessFactory factory;

    @Override
    protected void setUp() throws Exception {
        factory = new BeanProcessFactory();

        // check SPI will see the factory if we register it using an iterator
        // provider
        GeoTools.addFactoryIteratorProvider(new FactoryIteratorProvider() {

            public <T> Iterator<T> iterator(Class<T> category) {
                if (ProcessFactory.class.isAssignableFrom(category)) {
                    return (Iterator<T>) Collections.singletonList(factory).iterator();
                } else {
                    return null;
                }
            }
        });
    }

    public void testNames() {
        Set<Name> names = factory.getNames();
        assertTrue(names.size() > 0);
        // System.out.println(names);
        // Identity
        assertTrue(names.contains(new NameImpl("bean", "Identity")));
    }

    public void testDescribeIdentity() {
        NameImpl name = new NameImpl("bean", "Identity");
        InternationalString desc = factory.getDescription(name);
        assertNotNull(desc);

        Map<String, Parameter<?>> params = factory.getParameterInfo(name);
        assertEquals(1, params.size());

        Parameter<?> input = params.get("input");
        assertEquals(Object.class, input.type);
        assertTrue(input.required);

        Map<String, Parameter<?>> result = factory.getResultInfo(name, null);
        assertEquals(1, result.size());
        Parameter<?> identity = result.get("value");
        assertEquals(Object.class, identity.type);
    }

    public void testExecuteIdentity() throws ProcessException {
        // prepare a mock feature collection
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setName("test");
        final ReferencedEnvelope re = new ReferencedEnvelope(-10, 10, -10, 10, null);

        org.geotools.process.Process p = factory.create(new NameImpl("bean", "Identity"));
        Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put("input", re);
        Map<String, Object> result = p.execute(inputs, null);

        assertEquals(1, result.size());
        ReferencedEnvelope computed = (ReferencedEnvelope) result.get("value");
        assertEquals(re, computed);
        assertSame(re, computed);
    }

    public void testSPI() throws Exception {
        NameImpl boundsName = new NameImpl("bean", "Identity");
        ProcessFactory factory = Processors.createProcessFactory(boundsName);
        assertNotNull(factory);
        assertTrue(factory instanceof BeanProcessFactory);

        org.geotools.process.Process buffer = Processors.createProcess(boundsName);
        assertNotNull(buffer);
    }

    public void testInvertQuery() throws ProcessException {
        // prepare a mock feature collection
        SimpleFeatureCollection data = buildTestFeatures();
        
        org.geotools.process.Process transformation = factory.create(new NameImpl("bean", "VectorIdentityRT"));
        Map<String, Object> inputs = new HashMap<String, Object>();
        inputs.put("data", data);
        inputs.put("value", 10);
        
        RenderingProcess tx = (RenderingProcess) transformation;
        Query dummyQuery = tx.invertQuery(inputs, null, null);
        
        Map<String, Object> result = transformation.execute(inputs, null);

        assertEquals(1, result.size());
        
        SimpleFeatureCollection computed = (SimpleFeatureCollection) result.get("result");
        
        assertEquals(data, computed);
        assertEquals(data, computed);
        assertSame(data, computed);
    }


    private SimpleFeatureCollection buildTestFeatures()
    {
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setName("test");
        // this should be populated correctly
        CoordinateReferenceSystem crs = null;
		tb.add("geom", Geometry.class, crs );
        tb.add("count", Integer.class);
        SimpleFeatureType schema = tb.buildFeatureType();

        SimpleFeatureCollection fc = new ListFeatureCollection(schema);
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(schema);

        GeometryFactory factory = new GeometryFactory(new PackedCoordinateSequenceFactory());

        Geometry point = factory.createPoint(new Coordinate(10, 10));
        fb.add(point);
        fb.add(5);
        
        fc.add(fb.buildFeature(null));

        return fc;
    }
}
