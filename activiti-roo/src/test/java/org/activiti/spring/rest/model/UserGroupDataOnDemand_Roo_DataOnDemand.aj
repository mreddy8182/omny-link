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
import org.activiti.spring.rest.model.UserGroup;
import org.activiti.spring.rest.model.UserGroupDataOnDemand;
import org.springframework.stereotype.Component;

privileged aspect UserGroupDataOnDemand_Roo_DataOnDemand {
    
    declare @type: UserGroupDataOnDemand: @Component;
    
    private Random UserGroupDataOnDemand.rnd = new SecureRandom();
    
    private List<UserGroup> UserGroupDataOnDemand.data;
    
    public UserGroup UserGroupDataOnDemand.getNewTransientUserGroup(int index) {
        UserGroup obj = new UserGroup();
        setId(obj, index);
        setName(obj, index);
        setType(obj, index);
        return obj;
    }
    
    public void UserGroupDataOnDemand.setId(UserGroup obj, int index) {
        String id = "id_" + index;
        obj.setId(id);
    }
    
    public void UserGroupDataOnDemand.setName(UserGroup obj, int index) {
        String name = "name_" + index;
        obj.setName(name);
    }
    
    public void UserGroupDataOnDemand.setType(UserGroup obj, int index) {
        String type = "type_" + index;
        obj.setType(type);
    }
    
    public UserGroup UserGroupDataOnDemand.getSpecificUserGroup(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        UserGroup obj = data.get(index);
        Long id = obj.getId_();
        return UserGroup.findUserGroup(id);
    }
    
    public UserGroup UserGroupDataOnDemand.getRandomUserGroup() {
        init();
        UserGroup obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId_();
        return UserGroup.findUserGroup(id);
    }
    
    public boolean UserGroupDataOnDemand.modifyUserGroup(UserGroup obj) {
        return false;
    }
    
    public void UserGroupDataOnDemand.init() {
        int from = 0;
        int to = 10;
        data = UserGroup.findUserGroupEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'UserGroup' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<UserGroup>();
        for (int i = 0; i < 10; i++) {
            UserGroup obj = getNewTransientUserGroup(i);
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