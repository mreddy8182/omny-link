// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import org.activiti.spring.rest.model.Execution;

privileged aspect Execution_Roo_JavaBean {
    
    public String Execution.getActivityId() {
        return this.activityId;
    }
    
    public void Execution.setActivityId(String activityId) {
        this.activityId = activityId;
    }
    
    public String Execution.getId() {
        return this.id;
    }
    
    public void Execution.setId(String id) {
        this.id = id;
    }
    
    public String Execution.getParentId() {
        return this.parentId;
    }
    
    public void Execution.setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public String Execution.getProcessInstanceId() {
        return this.processInstanceId;
    }
    
    public void Execution.setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public Boolean Execution.getEnded() {
        return this.ended;
    }
    
    public void Execution.setEnded(Boolean ended) {
        this.ended = ended;
    }
    
}