// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.activiti.spring.rest.model.ProcessInstance;

privileged aspect ProcessInstance_Roo_Json {
    
    public String ProcessInstance.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String ProcessInstance.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static ProcessInstance ProcessInstance.fromJsonToProcessInstance(String json) {
        return new JSONDeserializer<ProcessInstance>()
        .use(null, ProcessInstance.class).deserialize(json);
    }
    
    public static Collection<ProcessInstance> ProcessInstance.fromJsonArrayToProcessInstances(String json) {
        return new JSONDeserializer<List<ProcessInstance>>()
        .use("values", ProcessInstance.class).deserialize(json);
    }
    
}