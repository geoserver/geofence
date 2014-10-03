/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.model;

import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Moving base64 de-coding from commons codec to DatatypeConverter.
 * Making sure the results are the same, or we may lose some passwords in the db...
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class Base64EncodersTest {

    @Test
    public void testEq() {

        String msg1 = "this is the message to encode";

        String output_codec = new String(Base64.encodeBase64(msg1.getBytes()));
        String output_dconv = DatatypeConverter.printBase64Binary(msg1.getBytes());

        System.out.println("apache commons:    " + output_codec);
        System.out.println("DatatypeConverter: " + output_dconv);
        assertEquals(output_codec, output_dconv);

        byte[] back_codec = Base64.decodeBase64(output_dconv);
        byte[] back_dconv = DatatypeConverter.parseBase64Binary(output_dconv);



        Assert.assertTrue( Arrays.equals(msg1.getBytes(), back_codec));
        Assert.assertTrue( Arrays.equals(msg1.getBytes(), back_dconv));

    }

}
