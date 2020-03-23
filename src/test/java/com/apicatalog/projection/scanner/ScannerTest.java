package com.apicatalog.projection.scanner;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.apicatalog.projection.TestObjectA;
import com.apicatalog.projection.TestProjectionAF;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;

@RunWith(JUnit4.class)
public class ScannerTest {

	ProjectionScanner scanner;
	
	@Before
	public void setup() {
		scanner = new ProjectionScanner();
	}
	
    @Test
    public void testScanAF() {
    	
    	final ProjectionMapping projection = scanner.scan(TestProjectionAF.class);
    	
    	Assert.assertNotNull(projection);
    	Assert.assertEquals(TestProjectionAF.class, projection.getProjectionClass());

    	final Collection<PropertyMapping> properties = projection.getProperties();
    	
    	Assert.assertNotNull(properties);
    	Assert.assertEquals(3, properties.size());
    	
    	final Iterator<PropertyMapping> it = properties.iterator();
    	
    	final PropertyMapping pm1 = it.next();
    	Assert.assertNotNull(pm1);
    	Assert.assertEquals("originString", pm1.getName());
    	Assert.assertNull(pm1.getFunctions());
    	Assert.assertNotNull(pm1.getSources());
    	Assert.assertNotNull(pm1.getTarget());
    	Assert.assertEquals(1, pm1.getSources().length);
    	Assert.assertEquals("stringValue", pm1.getSources()[0].getPropertyName());
    	Assert.assertEquals(TestObjectA.class, pm1.getSources()[0].getObjectClass());
    	Assert.assertNull(pm1.getSources()[0].getFunctions());

    	final PropertyMapping pm2 = it.next();
    	Assert.assertNotNull(pm2);
    	Assert.assertEquals("modifiedString", pm2.getName());
    	Assert.assertNull(pm2.getFunctions());
    	Assert.assertNotNull(pm2.getSources());
    	Assert.assertNotNull(pm2.getTarget());
    	Assert.assertEquals(1, pm2.getSources().length);
    	Assert.assertEquals("stringValue", pm2.getSources()[0].getPropertyName());
    	Assert.assertEquals(TestObjectA.class, pm2.getSources()[0].getObjectClass());
    	Assert.assertNotNull(pm2.getSources()[0].getFunctions());
    	Assert.assertEquals(1, pm2.getSources()[0].getFunctions().length);
    	
    	final PropertyMapping pm3 = it.next();
    	Assert.assertNotNull(pm3);
    	Assert.assertEquals("modified2xString", pm3.getName());
    	Assert.assertNull(pm3.getFunctions());
    	Assert.assertNotNull(pm3.getSources());
    	Assert.assertNotNull(pm3.getTarget());
    	Assert.assertEquals(1, pm3.getSources().length);
    	Assert.assertEquals("stringValue", pm3.getSources()[0].getPropertyName());
    	Assert.assertEquals(TestObjectA.class, pm3.getSources()[0].getObjectClass());
    	Assert.assertNotNull(pm3.getSources()[0].getFunctions());
    	Assert.assertEquals(2, pm3.getSources()[0].getFunctions().length);
    }
}
