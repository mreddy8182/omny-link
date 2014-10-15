package org.activiti.spring.rest.model;
import org.springframework.roo.addon.equals.RooEquals;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.serializable.RooSerializable;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooEquals
@RooSerializable
@RooJson
public class Execution {

    /**
     */
    private String activityId;

    /**
     */
    private String id;

    /**
     */
    private String parentId;

    /**
     */
    private String processInstanceId;

    /**
     */
    private Boolean ended;
    
    public Execution() { 
    	;
    }
    
	public Execution(org.activiti.engine.runtime.Execution exe) {
		this();
    	setActivityId(exe.getActivityId());
		setId(exe.getId());
		setParentId(exe.getParentId());
		setProcessInstanceId(exe.getProcessInstanceId());
		setEnded(exe.isEnded());
    }
}