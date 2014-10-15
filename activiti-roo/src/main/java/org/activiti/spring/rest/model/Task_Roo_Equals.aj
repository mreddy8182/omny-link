// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import org.activiti.spring.rest.model.Task;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

privileged aspect Task_Roo_Equals {
    
    public boolean Task.equals(Object obj) {
        if (!(obj instanceof Task)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Task rhs = (Task) obj;
        return new EqualsBuilder().append(assignee, rhs.assignee).append(createTime, rhs.createTime).append(delegateState, rhs.delegateState).append(deploymentId, rhs.deploymentId).append(description, rhs.description).append(dueDate, rhs.dueDate).append(formKey, rhs.formKey).append(id, rhs.id).append(id_, rhs.id_).append(name, rhs.name).append(owner, rhs.owner).append(parentTaskId, rhs.parentTaskId).append(priority, rhs.priority).append(processDefinitionId, rhs.processDefinitionId).append(suspended, rhs.suspended).append(taskDefinitionKey, rhs.taskDefinitionKey).isEquals();
    }
    
    public int Task.hashCode() {
        return new HashCodeBuilder().append(assignee).append(createTime).append(delegateState).append(deploymentId).append(description).append(dueDate).append(formKey).append(id).append(id_).append(name).append(owner).append(parentTaskId).append(priority).append(processDefinitionId).append(suspended).append(taskDefinitionKey).toHashCode();
    }
    
}