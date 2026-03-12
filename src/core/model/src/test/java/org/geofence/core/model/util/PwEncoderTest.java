/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.core.model.util;

import static org.junit.jupiter.api.Assertions.*;

import java.security.GeneralSecurityException;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** @author ETj <etj at geo-solutions.it> */
public class PwEncoderTest {

    public PwEncoderTest() {}

    @BeforeAll
    public static void setUpClass() throws Exception {}

    @AfterAll
    public static void tearDownClass() throws Exception {}

    @Test
    public void testEncode() {
        testString("test");
        testString("topolino");
        testString("");
    }

    @Test
    public void cannotDecodePwWithCustomKey() throws Exception {
        Properties originalProperties = System.getProperties();
        Properties properties = new Properties();
        properties.putAll(originalProperties);
        System.setProperties(properties);

        properties.setProperty("GEOFENCE_PWENCODER_KEY", "mycustomkeyforencrypting");
        String encoded = PwEncoder.encode("test");
        System.out.println(encoded);
        String defaultKey = "installation dependant key needed";
        byte[] bytes = defaultKey.substring(0, 16).getBytes();
        SecretKeySpec keySpec = new SecretKeySpec(bytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        try {
            cipher.doFinal(Base64.decodeBase64(encoded));
            fail();
        } catch (GeneralSecurityException e) {
        }

        System.setProperties(originalProperties);
    }

    public void testString(String test) {

        String enc = PwEncoder.encode(test);
        System.out.println("ENC --> " + enc);
        String dec = PwEncoder.decode(enc);
        System.out.println("DEC --> " + dec);
        assertEquals(test, dec);
    }
}
