// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.activiti.spring.rest.model.Task;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Task_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Task.entityManager;
    
    public static final List<String> Task.fieldNames4OrderClauseFilter = java.util.Arrays.asList("JSON_FIELDS", "processEngine", "assignee", "createTime", "dueDate", "delegateState", "description", "id", "name", "owner", "parentTaskId", "priority", "processDefinitionId", "taskDefinitionKey", "suspended", "formKey", "deploymentId", "formProperties");
    
    public static final EntityManager Task.entityManager() {
        EntityManager em = new Task().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    @Transactional
    public void Task.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Task.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Task attached = Task.findTask(this.id_);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Task.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Task.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Task Task.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Task merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}