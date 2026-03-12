/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.model;

import jakarta.xml.bind.DatatypeConverter;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Moving base64 de-coding from commons codec to DatatypeConverter. Making sure the results are the same, or we may lose
 * some passwords in the db...
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
        Assertions.assertEquals(output_codec, output_dconv);

        byte[] back_codec = Base64.decodeBase64(output_dconv);
        byte[] back_dconv = DatatypeConverter.parseBase64Binary(output_dconv);

        Assertions.assertTrue(Arrays.equals(msg1.getBytes(), back_codec));
        Assertions.assertTrue(Arrays.equals(msg1.getBytes(), back_dconv));
    }
}
