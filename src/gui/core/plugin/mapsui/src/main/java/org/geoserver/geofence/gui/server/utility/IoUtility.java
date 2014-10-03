/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.gui.server.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;


// TODO: Auto-generated Javadoc
/**
 * The Class IoUtility.
 */
public class IoUtility
{

    /**
     * Decompress.
     *
     * @param prefix
     *            the prefix
     * @param inputFile
     *            the input file
     * @param tempFile
     *            the temp file
     * @return the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static File decompress(final String prefix, final File inputFile, final File tempFile) throws IOException
    {
        final File tmpDestDir = createTodayPrefixedDirectory(prefix, new File(tempFile.getParent()));

        ZipFile zipFile = new ZipFile(inputFile);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements())
        {

            ZipEntry entry = (ZipEntry) entries.nextElement();
            InputStream stream = zipFile.getInputStream(entry);

            if (entry.isDirectory())
            {
                // Assume directories are stored parents first then
                // children.
                (new File(tmpDestDir, entry.getName())).mkdir();

                continue;
            }

            File newFile = new File(tmpDestDir, entry.getName());

            FileOutputStream fos = new FileOutputStream(newFile);
            try
            {
                byte[] buf = new byte[1024];
                int len;

                while ((len = stream.read(buf)) >= 0)
                {
                    saveCompressedStream(buf, fos, len);
                }

            }
            catch (IOException e)
            {
                zipFile.close();

                IOException ioe = new IOException("Not valid ZIP archive file type.");
                ioe.initCause(e);
                throw ioe;
            }
            finally
            {
                fos.flush();
                fos.close();

                stream.close();
            }
        }
        zipFile.close();

        if ((tmpDestDir.listFiles().length == 1) && (tmpDestDir.listFiles()[0].isDirectory()))
        {
            return getShpFile(tmpDestDir.listFiles()[0]);
        }

        // File[] files = tmpDestDir.listFiles(new FilenameFilter() {
        //
        // public boolean accept(File dir, String name) {
        // return FilenameUtils.getExtension(name).equalsIgnoreCase("shp");
        // }
        // });
        //
        // return files.length > 0 ? files[0] : null;

        return getShpFile(tmpDestDir);
    }

    /**
     * Gets the shp file.
     *
     * @param tmpDestDir
     *            the tmp dest dir
     * @return the shp file
     */
    private static File getShpFile(File tmpDestDir)
    {
        File[] files = tmpDestDir.listFiles(new FilenameFilter()
                {

                    public boolean accept(File dir, String name)
                    {
                        return FilenameUtils.getExtension(name).equalsIgnoreCase("shp");
                    }
                });

        return (files.length > 0) ? files[0] : null;
    }

    /**
     * Save compressed stream.
     *
     * @param buffer
     *            the buffer
     * @param out
     *            the out
     * @param len
     *            the len
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void saveCompressedStream(final byte[] buffer, final OutputStream out,
        final int len) throws IOException
    {
        try
        {
            out.write(buffer, 0, len);

        }
        catch (Exception e)
        {
            out.flush();
            out.close();

            IOException ioe = new IOException("Not valid archive file type.");
            ioe.initCause(e);
            throw ioe;
        }
    }

    /**
     * Creates the today directory.
     *
     * @param destDir
     *            the dest dir
     * @param inputFileName
     *            the input file name
     * @return the file
     */
    public static final File createTodayDirectory(File destDir, String inputFileName)
    {
        return createTodayDirectory(destDir, inputFileName, false);
    }

    /**
     * Creates the today directory.
     *
     * @param destDir
     *            the dest dir
     * @param inputFileName
     *            the input file name
     * @param withTime
     *            the with time
     * @return the file
     */
    public static final File createTodayDirectory(File destDir, String inputFileName,
        final boolean withTime)
    {
        final SimpleDateFormat SDF = withTime ? new SimpleDateFormat("yyyy_MM_dd_hhmmsss") : new SimpleDateFormat("yyyy_MM_dd");
        final String newPath =
            (new StringBuffer(destDir.getAbsolutePath().trim()).append(
                    File.separatorChar).append(SDF.format(new Date())).append("_").append(inputFileName)).toString();
        File dir = new File(newPath);
        if (!dir.exists())
        {
            dir.mkdir();
        }

        return dir;
    }

    /**
     * Creates the today prefixed directory.
     *
     * @param prefix
     *            the prefix
     * @param parent
     *            the parent
     * @return the file
     */
    public static File createTodayPrefixedDirectory(final String prefix, final File parent)
    {
        final SimpleDateFormat SDF_HMS = new SimpleDateFormat("yyyy_MM_dd_hhmmsss");
        final String newPath =
            (new StringBuffer(parent.getAbsolutePath().trim()).append(
                    File.separatorChar).append(prefix).append(File.separatorChar).append(SDF_HMS.format(new Date()))).toString();
        File dir = new File(newPath);
        if (!dir.exists())
        {
            dir.mkdirs();
        }

        return dir;
    }
}
