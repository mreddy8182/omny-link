// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.activiti.spring.rest.model.Execution;
import org.activiti.spring.rest.model.ExecutionDataOnDemand;
import org.springframework.stereotype.Component;

privileged aspect ExecutionDataOnDemand_Roo_DataOnDemand {
    
    declare @type: ExecutionDataOnDemand: @Component;
    
    private Random ExecutionDataOnDemand.rnd = new SecureRandom();
    
    private List<Execution> ExecutionDataOnDemand.data;
    
    public Execution ExecutionDataOnDemand.getNewTransientExecution(int index) {
        Execution obj = new Execution();
        setActivityId(obj, index);
        setEnded(obj, index);
        setParentId(obj, index);
        setProcessInstanceId(obj, index);
        return obj;
    }
    
    public void ExecutionDataOnDemand.setActivityId(Execution obj, int index) {
        String activityId = "activityId_" + index;
        obj.setActivityId(activityId);
    }
    
    public void ExecutionDataOnDemand.setEnded(Execution obj, int index) {
        Boolean ended = Boolean.TRUE;
        obj.setEnded(ended);
    }
    
    public void ExecutionDataOnDemand.setParentId(Execution obj, int index) {
        String parentId = "parentId_" + index;
        obj.setParentId(parentId);
    }
    
    public void ExecutionDataOnDemand.setProcessInstanceId(Execution obj, int index) {
        String processInstanceId = "processInstanceId_" + index;
        obj.setProcessInstanceId(processInstanceId);
    }
    
    public Execution ExecutionDataOnDemand.getSpecificExecution(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Execution obj = data.get(index);
        String id = obj.getId();
        return Execution.findExecution(id);
    }
    
    public Execution ExecutionDataOnDemand.getRandomExecution() {
        init();
        Execution obj = data.get(rnd.nextInt(data.size()));
        String id = obj.getId();
        return Execution.findExecution(id);
    }
    
    public boolean ExecutionDataOnDemand.modifyExecution(Execution obj) {
        return false;
    }
    
    public void ExecutionDataOnDemand.init() {
        int from = 0;
        int to = 10;
        data = Execution.findExecutionEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Execution' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Execution>();
        for (int i = 0; i < 10; i++) {
            Execution obj = getNewTransientExecution(i);
            try {
                obj.persist();
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
    
}