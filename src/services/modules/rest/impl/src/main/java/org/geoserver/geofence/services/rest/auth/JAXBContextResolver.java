/* (c) 2014 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.services.rest.auth;


import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;


/**
 * Class JAXBContextResolver.
 *
 * @author ETj (etj at geo-solutions.it)
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext>
{

    private static final JAXBContext context = initContext();

    private static JAXBContext initContext()
    {
        JAXBContext context = null;

//        try {
//            context = null;
//            JAXBContext.newInstance(SearchFilter.class, AndFilter.class,
//                      AttributeFilter.class, FieldFilter.class, NotFilter.class, OrFilter.class);
//        } catch (JAXBException e) {
//            throw new RuntimeException(e);
//        }

        return context;
    }

    /* (non-Javadoc)
     * @see javax.ws.rs.ext.ContextResolver#getContext(java.lang.Class)
     */
    @Override
    public JAXBContext getContext(Class<?> clazz)
    {
//       if(clazz.equals(SearchFilter.class))
//             return context;
        return null;
    }

}
