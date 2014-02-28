// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.activiti.spring.rest.model.Execution;

privileged aspect Execution_Roo_Json {
    
    public String Execution.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String Execution.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static Execution Execution.fromJsonToExecution(String json) {
        return new JSONDeserializer<Execution>()
        .use(null, Execution.class).deserialize(json);
    }
    
    public static String Execution.toJsonArray(Collection<Execution> collection) {
        return new JSONSerializer()
        .exclude("*.class").serialize(collection);
    }
    
    public static String Execution.toJsonArray(Collection<Execution> collection, String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(collection);
    }
    
    public static Collection<Execution> Execution.fromJsonArrayToExecutions(String json) {
        return new JSONDeserializer<List<Execution>>()
        .use("values", Execution.class).deserialize(json);
    }
    
}