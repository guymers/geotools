package org.geotools.filter.v2_0.bindings;

import org.geotools.filter.v2_0.FESTestSupport;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.temporal.After;
import org.opengis.temporal.Instant;

public class AfterBindingTest extends FESTestSupport {

    public void testParse() throws Exception {
        String xml = 
        "<fes:Filter " + 
        "   xmlns:fes='http://www.opengis.net/fes/2.0' " + 
        "   xmlns:gml='http://www.opengis.net/gml/3.2' " + 
        "   xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
        "   xsi:schemaLocation='http://www.opengis.net/fes/2.0 http://schemas.opengis.net/filter/2.0/filterAll.xsd" + 
        " http://www.opengis.net/gml/3.2 http://schemas.opengis.net/gml/3.2.1/gml.xsd'>" + 
        "   <fes:After> " + 
        "      <fes:ValueReference>timeInstanceAttribute</fes:ValueReference> " + 
        "      <gml:TimeInstant gml:id='TI1'> " + 
        "         <gml:timePosition>2005-05-19T09:28:40Z</gml:timePosition> " + 
        "      </gml:TimeInstant> " + 
        "   </fes:After> " + 
        "</fes:Filter>";
        buildDocument(xml);
        
        After after = (After) parse();
        assertNotNull(after);
        
        assertTrue(after.getExpression1() instanceof PropertyName);
        assertEquals("timeInstanceAttribute", ((PropertyName)after.getExpression1()).getPropertyName());

        assertTrue(after.getExpression2() instanceof Literal);
        assertTrue(after.getExpression2().evaluate(null) instanceof Instant);
    }
}
