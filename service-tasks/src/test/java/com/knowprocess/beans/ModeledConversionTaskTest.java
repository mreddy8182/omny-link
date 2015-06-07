package com.knowprocess.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.knowprocess.beans.model.LeadActivity;
import com.knowprocess.sugarcrm.api.SugarLead;
import com.knowprocess.test.activiti.ExtendedRule;

public class ModeledConversionTaskTest {

    public static final String MSG_NAME = "kp.account";

    @Rule
    public ExtendedRule activitiRule = new ExtendedRule(
            "test-activiti.cfg.xml");

    private static final String INITIATOR = "tim@knowprocess.com";

    @Before
    public void setUp() {
    }

    @Test
    @Ignore
    // @Deployment(resources = {
    // "process/ModelBasedBeanConversionTaskWithDataObjects.bpmn" })
    public void testConversionTaskUsingDataObjects() {

    }

    @Test
    @Deployment(resources = { "process/ModelBasedBeanConversionTask.bpmn" })
    public void testConversionTaskInProcess() {

        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("initiator", INITIATOR);
        LeadActivity lead = new LeadActivity("User read article XYZ");
        variableMap.put("source", lead);
        String msg = "{}";
        variableMap.put(MSG_NAME, msg);

        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByMessage(MSG_NAME, variableMap);
        assertNotNull(processInstance.getId());
        System.out.println("id " + processInstance.getId() + " "
                + processInstance.getProcessDefinitionId());
        SugarLead sugarLead = (SugarLead) activitiRule
                .getHistoryService().createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.getId())
                .variableName("target").singleResult().getValue();
        System.out.println(" returned: " + sugarLead);
        assertNotNull(sugarLead);
        assertEquals(lead.getDateOfActivity(), sugarLead.getDateEntered());
        assertEquals(lead.getDescription(), sugarLead.getDescription());

        activitiRule.assertComplete(processInstance);
        activitiRule.dumpVariables(processInstance.getId());
    }

}