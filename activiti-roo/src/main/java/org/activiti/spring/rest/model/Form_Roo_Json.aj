// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.model;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.activiti.spring.rest.model.Form;

privileged aspect Form_Roo_Json {
    
    public String Form.toJson() {
        return new JSONSerializer()
        .exclude("*.class").serialize(this);
    }
    
    public String Form.toJson(String[] fields) {
        return new JSONSerializer()
        .include(fields).exclude("*.class").serialize(this);
    }
    
    public static Form Form.fromJsonToForm(String json) {
        return new JSONDeserializer<Form>()
        .use(null, Form.class).deserialize(json);
    }
    
    public static Collection<Form> Form.fromJsonArrayToForms(String json) {
        return new JSONDeserializer<List<Form>>()
        .use("values", Form.class).deserialize(json);
    }
    
}