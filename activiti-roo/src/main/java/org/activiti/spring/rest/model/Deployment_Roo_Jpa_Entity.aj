// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import org.activiti.spring.rest.model.Deployment;

privileged aspect Deployment_Roo_Jpa_Entity {
    
    declare @type: Deployment: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_")
    private Long Deployment.id_;
    
    @Version
    @Column(name = "version")
    private Integer Deployment.version;
    
    public Long Deployment.getId_() {
        return this.id_;
    }
    
    public void Deployment.setId_(Long id) {
        this.id_ = id;
    }
    
    public Integer Deployment.getVersion() {
        return this.version;
    }
    
    public void Deployment.setVersion(Integer version) {
        this.version = version;
    }
    
}