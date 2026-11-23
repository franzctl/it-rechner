package de.franzctl.itrechner;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SubnettingTest {

    @Test
    public void testPraefixFuerHosts() {
        assertEquals(26, Subnetting.praefixFuerHosts(50));
        assertEquals(24, Subnetting.praefixFuerHosts(200));
        assertEquals(30, Subnetting.praefixFuerHosts(1));
    }
}