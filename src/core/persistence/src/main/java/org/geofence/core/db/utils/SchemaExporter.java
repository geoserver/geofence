/*
 */
package org.geofence.core.db.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.core.model.AdminRule;
import org.geofence.core.model.GSInstance;
import org.geofence.core.model.GSUser;
import org.geofence.core.model.IPAddressRange;
import org.geofence.core.model.IPRangeProvider;
import org.geofence.core.model.Identifiable;
import org.geofence.core.model.LayerAttribute;
import org.geofence.core.model.LayerDetails;
import org.geofence.core.model.Rule;
import org.geofence.core.model.RuleLimits;
import org.geofence.core.model.UserGroup;
import org.geofence.core.model.adapter.MultiPolygonAdapter;
import org.hibernate.boot.MetadataSources;
// import org.hibernate.tool.schema.internal.SchemaExport;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.TargetType;

/** @author etj */
public class SchemaExporter {

    private static final Logger LOGGER = LogManager.getLogger(SchemaExporter.class);

    static final String DIALECT_POSTGIS = "org.hibernate.spatial.dialect.postgis.PostgisDialect";
    static final String DIALECT_H2 = "org.hibernate.dialect.H2Dialect";

    public static void main(String[] args) {
        SchemaExporter exporter = new SchemaExporter();

        exporter.createSqlFile(DIALECT_POSTGIS, "./schema.postgres.sql", true);
        exporter.createSqlFile(DIALECT_H2, "./schema.h2.sql", false);
    }

    public void createSqlFile(String dialect, String outputFile, boolean stdout) {

        Map<String, Object> settings = new HashMap<>();

        settings.put("hibernate.dialect", dialect);
        //        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");

        ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(settings).build();

        MetadataSources sources = new MetadataSources(serviceRegistry);

        Class<?> modelClasses[] = {
            AdminRule.class,
            GSInstance.class,
            GSUser.class,
            IPAddressRange.class,
            IPRangeProvider.class,
            Identifiable.class,
            LayerAttribute.class,
            LayerDetails.class,
            Rule.class,
            RuleLimits.class,
            UserGroup.class,
            MultiPolygonAdapter.class
        };

        for (Class<?> c : modelClasses) {
            sources.addAnnotatedClass(c);
        }

        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.SCRIPT);
        if (stdout) {
            enumSet.add(TargetType.STDOUT);
        }

        //        Hbm2DDLExporterTask exporter = new Hbm2DDLExporterTask();

        SchemaExport schemaExport = new SchemaExport();
        schemaExport
                .setOutputFile(outputFile)
                .setOverrideOutputFileContent()
                .setDelimiter(";")
                .setFormat(true)
                .execute(enumSet, Action.CREATE, sources.buildMetadata());
    }
}
