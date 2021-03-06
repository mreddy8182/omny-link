/*******************************************************************************
 *Copyright 2011-2018 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.knowprocess.bpm.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.TransformerConfigurationException;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.DeploymentEntity;
import org.activiti.engine.impl.persistence.entity.ResourceEntity;
import org.activiti.engine.repository.DeploymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.knowprocess.bpm.api.ReportableException;
import com.knowprocess.bpm.api.UnsupportedBpmnException;
import com.knowprocess.bpm.model.Deployment;
import com.knowprocess.bpm.model.ProcessModel;
import com.knowprocess.bpm.repositories.ProcessModelRepository;
import com.knowprocess.xslt.TransformTask;

@Controller
@RequestMapping("/{tenantId}/deployments")
public class DeploymentController {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(DeploymentController.class);

    private static final String PREPROCESSOR_RESOURCES = "/xslt/polyfill.xslt,/xslt/ExecutableTweaker.xsl";

    private static final String BPMN_RESOURCE_RESOURCES = "/static/xslt/bpmn2resources.xslt";

    private static final String VALIDATOR_RESOURCES = "/xslt/KpSupportRules.xsl";

    /**
     * Set true to make verbose debug level logging.
     */
    protected static boolean verbose;

    @Autowired(required = true)
    ProcessEngine processEngine;

    @Autowired
    private ProcessModelRepository processModelRepo;

    private TransformTask preProcessor;

    private TransformTask resourceExtractor;

    private TransformTask validator;

    @RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody List<Deployment> showAllJson(
            @PathVariable("tenantId") String tenantId) {
        LOGGER.info(String.format("showAllJson(%1$s)", tenantId));

        List<Deployment> list = Deployment.findAllDeployments(tenantId);
        LOGGER.info("Deployments: " + list.size());
        return list;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody Deployment showJson(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("id") String id) {
        LOGGER.info(String.format("showJson %1$s for tenant %2$s", id, tenantId));

        Deployment deployment = Deployment.findDeployment(id);
        return deployment;
    }

    @RequestMapping(value = "/{resource}/", method = RequestMethod.POST, headers = "Accept=application/json")
    public final @ResponseBody org.activiti.engine.repository.Deployment deployFromClasspath(
            @PathVariable("tenantId") String tenantId,
            @PathVariable("resource") String resource,
            @RequestParam(required = false) String deploymentName)
            throws UnsupportedEncodingException, IOException,
            UnsupportedBpmnException {

        org.activiti.engine.repository.Deployment deployment = null;

        LOGGER.debug(String.format("Deploy %1$s with name %2$s", resource,
                deploymentName));
        resource = resource.replace('.', '/') + ".bpmn";

        DeploymentBuilder builder = processEngine.getRepositoryService()
                .createDeployment();
        builder.tenantId(tenantId);
        if (deploymentName != null) {
            builder.name(deploymentName);
        }
        builder.addClasspathResource(resource);
        builder.deploy();

        return deployment;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded", headers = "Accept=application/json")
    public final @ResponseBody org.activiti.engine.repository.Deployment deploy(
            @RequestParam String tenantId, @RequestParam String name,
            @RequestParam String bpmn) throws UnsupportedEncodingException,
            IOException, UnsupportedBpmnException {
        org.activiti.engine.repository.Deployment deployment = null;

        LOGGER.debug(String.format("deploymentName: %1$s", name));

        DeploymentBuilder builder = processEngine.getRepositoryService()
                .createDeployment().name(name).tenantId(tenantId);

        final Map<String, String> processes = new HashMap<String, String>();
        processes.put(name, bpmn);

        try {
            for (Entry<String, String> entry : runExecutableTweaker(processes)
                    .entrySet()) {
                builder.addString(entry.getKey() + ".bpmn", entry.getValue());
            }
            deployment = builder.deploy();

            LOGGER.info(String.format(
                    "Completed deployment: %1$s(%2$s) at %3$s", deployment
                            .getName(), deployment.getId(), deployment
                            .getDeploymentTime().toString()));

            runResourceCreator(processes);

            return deployment;
        } catch (ActivitiException e) {
            LOGGER.warn(String
                    .format("Processes rejected for execution, continue as non-executable. Reason: %1$s",
                            e.getMessage()));
            handleIncompleteModel(tenantId, processes);
            return null;
        }

    }

    @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data", headers = "Accept=application/json")
    public final @ResponseBody org.activiti.engine.repository.Deployment uploadMultipleFiles(
            UriComponentsBuilder uriBuilder, @RequestParam String tenantId,
            @RequestParam(required = false) String deploymentName,
            @RequestParam MultipartFile... resourceFile)
            throws UnsupportedEncodingException, IOException,
            UnsupportedBpmnException {
        org.activiti.engine.repository.Deployment deployment = null;

        LOGGER.debug(String.format("deploymentName: %1$s", deploymentName));
        LOGGER.debug(String.format("# of resources: %1$s", resourceFile.length));

        DeploymentBuilder builder = processEngine.getRepositoryService()
                .createDeployment();
        if (deploymentName != null) {
            builder.name(deploymentName);
        }
        if (tenantId != null) {
            builder.tenantId(tenantId);
        }
        final Map<String, String> processes = new HashMap<String, String>();
        for (MultipartFile resource : resourceFile) {
            LOGGER.debug(String.format("Deploying file: %1$s",
                    resource.getOriginalFilename()));
            if (resource.getOriginalFilename().toLowerCase().endsWith(".bpmn")
                    || resource.getOriginalFilename().toLowerCase()
                            .endsWith(".bpmn20.xml")) {
                LOGGER.info("... BPMN resource: "
                        + resource.getOriginalFilename());
                processes.put(resource.getOriginalFilename(), new String(
                        resource.getBytes()));
            } else {
                LOGGER.debug("... non-BPMN resource");
                builder.addInputStream(resource.getOriginalFilename(),
                        resource.getInputStream());
            }
        }

        if (isValid(processes)) {
            try {
                for (Entry<String, String> entry : runExecutableTweaker(
                        processes).entrySet()) {
                    builder.addString(entry.getKey(), entry.getValue());
                }
                deployment = builder.deploy();

                LOGGER.info(String.format(
                        "Completed deployment: %1$s(%2$s) at %3$s", deployment
                                .getName(), deployment.getId(), deployment
                                .getDeploymentTime().toString()));
                for (Entry<String, ResourceEntity> entry : ((DeploymentEntity) deployment)
                        .getResources().entrySet()) {
                    LOGGER.debug("  ...including: " + entry.getKey());
                }

                runResourceCreator(processes);

                return deployment;
            } catch (ActivitiException e) {
                LOGGER.warn("Processes rejected for execution, continue as non-executable. Reason: {}",
                                e.getMessage());
                if (e.getCause() != null) {
                    LOGGER.warn("  cause reported: {}", e.getCause().getMessage());
                }
                handleIncompleteModel(tenantId, processes);
                return null;
            } catch (Exception e) {
                LOGGER.error("Unable to read file, attempt to continue as non-executable. Reason: {}",
                                e.getMessage(), e);
                handleIncompleteModel(tenantId, processes);
                return null;
            }
        } else {
            handleIncompleteModel(tenantId, processes);
            return null;
        }
    }

    private Map<String, String> runExecutableTweaker(
            Map<String, String> processes) throws UnsupportedEncodingException {
        HashMap<String, String> tweakedProcesses = new HashMap<String, String>();
        for (Entry<String, String> entry : processes.entrySet()) {
            String bpmn = fixContentInProlog(entry.getValue());
            bpmn = getPreProcessor().transform(bpmn);
            if (LOGGER.isDebugEnabled() && verbose) {
                LOGGER.debug("BPMN: " + bpmn);
            }
            tweakedProcesses.put(entry.getKey(), bpmn);
        }
        return tweakedProcesses;
    }

    private String fixContentInProlog(String xml) {
        // workaround: if start <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        // we'll get SystemId Unknown; Line #1; Column #1; Content is not allowed in prolog
        // Apparently this is an invisible character known as the Unicode byte order mark (<U+FEFF>)
        String bpmn = xml.trim();
        if (bpmn.startsWith("<?xml") || bpmn.substring(0, 100).contains("<?xml")) {
            bpmn = bpmn.substring(bpmn.indexOf("?>")+2).trim();
        }
        return bpmn;
    }

    private void runResourceCreator(
            Map<String, String> processes) throws UnsupportedEncodingException {
        for (Entry<String, String> entry : processes.entrySet()) {
            String bpmn = fixContentInProlog(entry.getValue());
            String[] resources = getResourceExtractor().transform(bpmn).split(",");
            for (String resource : resources) {
                if (resource == null || resource.trim().length() < 1) {
                    LOGGER.info("Ignoring empty resource '{}'", resource);
                } else {
                    Group group = processEngine.getIdentityService().newGroup(resource);
                    group.setName(resource);
                    try {
                        processEngine.getIdentityService().saveGroup(group);
                    } catch (RuntimeException e) {
                        LOGGER.warn("Ignoring error creating group '{}', presumably already exists", resource);
                    }
                }
            }
        }
    }

    private void handleIncompleteModel(String tenantId,
            final Map<String, String> processes) {
        for (Entry<String, String> entry : processes.entrySet()) {
            ProcessModel model = new ProcessModel();
            model.setName(entry.getKey());
            model.setBpmnString(fixContentInProlog(entry.getValue()));
            // First id="xyz" is our id
            int start = entry.getValue().indexOf("id=") + 4;
            int end = entry.getValue().indexOf("\"", start);
            if (entry.getValue().indexOf("'", start) > -1
                    && entry.getValue().indexOf("'", start) < end) {
                end = entry.getValue().indexOf("'", start);
            }
            String id = entry.getValue().substring(start, end);
            // model.setDeploymentId(deploymentId);
            model.setTenantId(tenantId);

            String issues = getValidator().transform(entry.getValue());
            if (LOGGER.isDebugEnabled() && verbose) {
                LOGGER.debug("ISSUES: " + issues);
            }
            model.setIssuesAsString(issues);

            ProcessModel latestExisting = processModelRepo.findLatestForTenant(id, tenantId);
            if (latestExisting == null) {
                model.setVersion(1);
            } else {
                model.setVersion(latestExisting.getVersion()+1);
            }
            model.setId(id+":"+model.getVersion());

            createModel(model);
        }
    }

    private TransformTask getPreProcessor() {
        if (preProcessor == null) {
            preProcessor = new TransformTask();
            try {
                preProcessor.setXsltResources(PREPROCESSOR_RESOURCES);
            } catch (TransformerConfigurationException e) {
                LOGGER.error(String.format(
                        "Unable to locate deployment pre-processors: %1$s",
                        PREPROCESSOR_RESOURCES), e);
            }
        }
        return preProcessor;
    }

    private TransformTask getResourceExtractor() {
      if (resourceExtractor == null) {
          resourceExtractor = new TransformTask();
          try {
              resourceExtractor.setXsltResources(BPMN_RESOURCE_RESOURCES);
          } catch (TransformerConfigurationException e) {
              LOGGER.error(String.format(
                      "Unable to locate resource extractor: %1$s",
                      BPMN_RESOURCE_RESOURCES), e);
          }
      }
      return resourceExtractor;
  }

    private TransformTask getValidator() {
        if (validator == null) {
            validator = new TransformTask();
            try {
                validator.setXsltResources(VALIDATOR_RESOURCES);
            } catch (TransformerConfigurationException e) {
                LOGGER.error(String.format(
                        "Unable to locate deployment validator: %1$s",
                        VALIDATOR_RESOURCES), e);
            }
        }
        return validator;
    }
    private boolean isValid(Map<String, String> processes) {
        // TODO rethink this in more Activiti way
        // for (Entry<String, String> entry : processes.entrySet()) {
        // if (transformService.transform(entry.getValue()).contains(
        // TransformTask.ERROR_KEY)) {
        // return false;
        // }
        // }
        return true;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteFromJson(@PathVariable("id") String id,
            @RequestParam(value = "cascade", defaultValue = "false") boolean cascade) {
        LOGGER.info(String.format("deleting deployment: %1$s", id));
        try {
            if (cascade) {
                processEngine.getRepositoryService().deleteDeployment(id, true);
            } else {
                processEngine.getRepositoryService().deleteDeployment(id);
            }
        } catch (ActivitiObjectNotFoundException e) {
            // assume this is an incomplete model....
            try {
                processModelRepo.delete(id);
            } catch (org.springframework.dao.EmptyResultDataAccessException e2) {
                throw new ActivitiObjectNotFoundException(e2.getMessage(), ProcessModel.class);
            }
        } catch (ActivitiException e) {
            throw e;
        } catch (org.apache.ibatis.exceptions.PersistenceException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ReportableException(String.format(
                    "Unable to delete deployment with id '%1$s', does it still have instances?", id), e);
        } catch (Exception e) {
            String msg = String.format("Unable to delete deployment with id '%1$s': %2$s: %3$s",
                    id, e.getClass().getName(), e.getMessage(), e);
            LOGGER.error(msg, e);
            throw new ReportableException(msg, e);
        }
    }

    protected ProcessModel createModel(ProcessModel model) {
        return processModelRepo.save(model);
    }

}
