/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.login.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class TokenEncoder
{

    // // 123456789 123456789 123456789 12
    // private static final byte[] KEY =
    // "installation dependant key needed".substring(0,16).getBytes();

    public static String encode(String msg, String key)
    {
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] input = msg.getBytes();
            byte[] encrypted = cipher.doFinal(input);
            byte[] output = Base64.encodeBase64(encrypted);

            return new String(output);
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (NoSuchPaddingException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (IllegalBlockSizeException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (BadPaddingException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (InvalidKeyException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
    }

    public static String decode(String msg, String key)
    {
        try
        {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] de64 = Base64.decodeBase64(msg);
            byte[] decrypted = cipher.doFinal(de64);

            return new String(decrypted);
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (NoSuchPaddingException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (IllegalBlockSizeException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (BadPaddingException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
        catch (InvalidKeyException ex)
        {
            throw new RuntimeException("Error while encoding", ex);
        }
    }
}
