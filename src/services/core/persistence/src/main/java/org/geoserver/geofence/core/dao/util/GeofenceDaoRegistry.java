/* (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geoserver.geofence.core.dao.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geoserver.geofence.core.dao.GSUserDAO;
import org.geoserver.geofence.core.dao.RegistrableDAO;
import org.geoserver.geofence.core.dao.UserGroupDAO;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Allow DAO pluggabily and externalization of the DAO type selection.
 *
 * @author Emanuele Tajariol (etj at geo-solutions.it)
 */
public class GeofenceDaoRegistry implements ApplicationContextAware
{
    private static final Logger LOGGER = LogManager.getLogger(GeofenceDaoRegistry.class);

    private static final Set<Class> REGISTERABLE_CLASSES = new HashSet<>(Arrays.asList(GSUserDAO.class, UserGroupDAO.class));

    public static class DaoRegistrar
    {
        private String name;
        private RegistrableDAO dao;

        public DaoRegistrar(String name, RegistrableDAO dao)
        {
            this.name = name;
            this.dao = dao;
        }
    }

    private Map<Class, Map<String, RegistrableDAO>> registry = new HashMap<>();
    private String selectedType;


    private GeofenceDaoRegistry()
    {
        for (Class registerableClass : REGISTERABLE_CLASSES) {
            registry.put(registerableClass, new HashMap<>());
        }
    }

    public GeofenceDaoRegistry(String defaultType)
    {
        this();
        this.selectedType = defaultType;
    }

    public void setSelectedType(String selectedType)
    {
        this.selectedType = selectedType;
    }

    public <T extends RegistrableDAO> T getDao(Class<T> t) {

        Map<String, RegistrableDAO> registryMap = registry.get(t);
        if(registryMap == null)
            throw new IllegalArgumentException("Unregistered class " + t);

        T dao = (T)registryMap.get(selectedType);
        if(dao == null)
            throw new IllegalArgumentException("DAO not found for class " +t.getSimpleName()+ " and type " + selectedType);

        LOGGER.info("Returning DAO type " + selectedType + " for class " + t.getSimpleName());
        return dao;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        Map<String, DaoRegistrar> registrars = applicationContext.getBeansOfType(DaoRegistrar.class);
        for (GeofenceDaoRegistry.DaoRegistrar registrar : registrars.values()) {
            addRegistrar(registrar);
        }
    }

    protected void addRegistrar(DaoRegistrar registrar) {
        RegistrableDAO dao = registrar.dao;
        
        for (Class registerableClass : REGISTERABLE_CLASSES) {
            if(registerableClass.isAssignableFrom(dao.getClass())) {
                LOGGER.info("Registering DAO type " + registrar.name + " for class " + dao.getClass().getSimpleName());

                Map<String, RegistrableDAO> registryMap = registry.get(registerableClass);
                registryMap.put(registrar.name, dao);
                return;
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skipping DAO type " + registrar.name + ": " + dao.getClass() + " is not a " + registerableClass);
            }
        }
        LOGGER.error("Cannot register DAO class " + registrar.dao.getClass() + " with type " + registrar.name);
        throw new IllegalArgumentException("Cannot register DAO class " + dao.getClass() + " with type " + registrar.name);
    }


}
