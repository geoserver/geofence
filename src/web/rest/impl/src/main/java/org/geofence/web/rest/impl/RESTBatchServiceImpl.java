/* (c) 2014 - 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */

package org.geofence.web.rest.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geofence.web.rest.api.exception.BadRequestRestEx;
import org.geofence.web.rest.api.exception.ConflictRestEx;
import org.geofence.web.rest.api.exception.GeoFenceRestEx;
import org.geofence.web.rest.api.exception.InternalErrorRestEx;
import org.geofence.web.rest.api.exception.NotFoundRestEx;
import org.geofence.web.rest.api.interfaces.RESTBatchService;
import org.geofence.web.rest.api.interfaces.RESTGSInstanceService;
import org.geofence.web.rest.api.interfaces.RESTRuleService;
import org.geofence.web.rest.api.interfaces.RESTUserGroupService;
import org.geofence.web.rest.api.interfaces.RESTUserService;
import org.geofence.web.rest.api.model.RESTBatch;
import org.geofence.web.rest.api.model.RESTBatchOperation;
import org.geofence.web.rest.api.model.RESTInputGroup;
import org.geofence.web.rest.api.model.RESTInputInstance;
import org.geofence.web.rest.api.model.RESTInputRule;
import org.geofence.web.rest.api.model.RESTInputUser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

/** @author ETj (etj at geo-solutions.it) */
@Service
@RestController
public class RESTBatchServiceImpl extends BaseRESTServiceImpl implements InitializingBean, RESTBatchService
//        implements RESTUserGroupService
{
    private static final Logger LOGGER = LogManager.getLogger(RESTBatchServiceImpl.class);

    //    private final static String OP_INSERT = "insert";
    //    private final static String OP_UPDATE = "update";
    //    private final static String OP_DELETE = "delete";
    //
    //    private final static String OP_ADDGROUP = "addGroup";
    //    private final static String OP_DELGROUP = "delGroup";

    @Autowired
    private RESTUserService restUserService;

    @Autowired
    private RESTUserGroupService restUserGroupService;

    @Autowired
    private RESTGSInstanceService restInstanceService;

    @Autowired
    private RESTRuleService restRuleService;

    @Transactional(value = "geofenceTransactionManager")
    @Override
    public ResponseEntity<String> exec(RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        runBatch(batch);
        return ResponseEntity.ok("OK\n");
    }

    /** Legacy {@code multipart/form-data} entry point (a "batch" part), for callers not yet sending JSON/XML bodies. */
    @PostMapping(path = "/exec", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> execMultipart(@RequestPart("batch") RESTBatch batch)
            throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        return exec(batch);
    }

    public void runBatch(RESTBatch batch) throws BadRequestRestEx, NotFoundRestEx, InternalErrorRestEx {
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Running batch with " + batch.getList().size() + " operations");

        for (RESTBatchOperation op : batch.getList()) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Running " + op);

            if (op.getType() == null) throw new BadRequestRestEx("Operation type is missing in operation " + op);

            try {
                switch (op.getService()) {
                    case users:
                        dispatchUserOp(op);
                        break;

                    case groups:
                        dispatchGroupOp(op);
                        break;

                    case instances:
                        dispatchInstanceOp(op);
                        break;

                    case rules:
                        dispatchRuleOp(op);
                        break;

                    default:
                        throw new BadRequestRestEx("Unhandled service for operation " + op);
                }
            } catch (GeoFenceRestEx ex) {
                throw ex;
            } catch (Exception ex) {
                LOGGER.error("Unexpected error: " + ex.getMessage(), ex);
                throw new InternalErrorRestEx("Unexpected exception: " + ex.getMessage());
            }
        }
    }

    protected void dispatchRuleOp(RESTBatchOperation op) throws NotFoundRestEx, BadRequestRestEx {
        switch (op.getType()) {
            case insert:
                ensurePayload(op);
                restRuleService.insert((RESTInputRule) op.getPayload());
                break;

            case update:
                ensurePayload(op);
                if (op.getId() != null) restRuleService.update(op.getId(), (RESTInputRule) op.getPayload());
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            case delete:
                if (op.getId() != null) restRuleService.delete(op.getId());
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            default:
                throw new BadRequestRestEx("Operation not bound " + op);
        }
    }

    protected void dispatchInstanceOp(RESTBatchOperation op)
            throws NotFoundRestEx, InternalErrorRestEx, ConflictRestEx, BadRequestRestEx {
        switch (op.getType()) {
            case insert:
                ensurePayload(op);
                restInstanceService.insert((RESTInputInstance) op.getPayload());
                break;

            case update:
                ensurePayload(op);
                if (op.getId() != null) restInstanceService.update(op.getId(), (RESTInputInstance) op.getPayload());
                else if (op.getName() != null)
                    restInstanceService.update(op.getName(), (RESTInputInstance) op.getPayload());
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            case delete:
                boolean cascade =
                        op.getCascade() == null ? false : op.getCascade().booleanValue();
                if (op.getId() != null) restInstanceService.delete(op.getId(), cascade);
                else if (op.getName() != null) restInstanceService.delete(op.getName(), cascade);
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            default:
                throw new BadRequestRestEx("Operation not bound " + op);
        }
    }

    protected void dispatchGroupOp(RESTBatchOperation op)
            throws BadRequestRestEx, NotFoundRestEx, ConflictRestEx, InternalErrorRestEx {
        switch (op.getType()) {
            case insert:
                ensurePayload(op);
                restUserGroupService.insert((RESTInputGroup) op.getPayload());
                break;

            case update:
                ensurePayload(op);
                if (op.getName() != null) restUserGroupService.update(op.getName(), (RESTInputGroup) op.getPayload());
                else if (op.getId() != null) throw new BadRequestRestEx("Bad identifier for op " + op);
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            case delete:
                boolean cascade =
                        op.getCascade() == null ? false : op.getCascade().booleanValue();
                if (op.getName() != null) restUserGroupService.delete(op.getName(), cascade);
                else if (op.getId() != null) throw new BadRequestRestEx("Bad identifier for op " + op);
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            default:
                throw new BadRequestRestEx("Operation not bound " + op);
        }
    }

    protected void dispatchUserOp(RESTBatchOperation op)
            throws NotFoundRestEx, BadRequestRestEx, InternalErrorRestEx, ConflictRestEx,
                    UnsupportedOperationException {
        switch (op.getType()) {
            case insert:
                ensurePayload(op);
                restUserService.insert((RESTInputUser) op.getPayload());
                break;

            case update:
                ensurePayload(op);
                if (op.getId() != null) restUserService.update(op.getId(), (RESTInputUser) op.getPayload());
                else if (op.getName() != null) restUserService.update(op.getName(), (RESTInputUser) op.getPayload());
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            case delete:
                boolean cascade =
                        op.getCascade() == null ? false : op.getCascade().booleanValue();
                if (op.getName() != null) restUserService.delete(op.getName(), cascade);
                else if (op.getId() != null) throw new BadRequestRestEx("Bad identifier for op " + op);
                else throw new BadRequestRestEx("Missing identifier for op " + op);
                break;

            case addGroup:
                {
                    restUserService.addIntoGroup(op.getUserName(), op.getGroupName());
                }
                break;

            case delgroup:
                {
                    restUserService.removeFromGroup(op.getUserName(), op.getGroupName());
                }
                break;

            default:
                throw new BadRequestRestEx("Operation not bound " + op);
        }
    }

    // ==========================================================================

    private void ensurePayload(RESTBatchOperation op) throws BadRequestRestEx {
        if (op.getPayload() == null) throw new BadRequestRestEx("Empty payload in operation " + op);
    }

    // ==========================================================================
    // ==========================================================================

    //    public void setUserGroupAdminService(UserGroupAdminService service) {
    //        this.userGroupAdminService = service;
    //    }

    @Override
    public void afterPropertiesSet() throws Exception {}

    // ==========================================================================

    public void setRestInstanceService(RESTGSInstanceService restInstanceService) {
        this.restInstanceService = restInstanceService;
    }

    public void setRestRuleService(RESTRuleService restRuleService) {
        this.restRuleService = restRuleService;
    }

    public void setRestUserGroupService(RESTUserGroupService restUserGroupService) {
        this.restUserGroupService = restUserGroupService;
    }

    public void setRestUserService(RESTUserService restUserService) {
        this.restUserService = restUserService;
    }
}
