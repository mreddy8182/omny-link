// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.web;

import org.activiti.spring.rest.model.Deployment;
import org.activiti.spring.rest.model.Execution;
import org.activiti.spring.rest.model.Form;
import org.activiti.spring.rest.model.FormProperty;
import org.activiti.spring.rest.model.ProcessDefinition;
import org.activiti.spring.rest.model.ProcessInstance;
import org.activiti.spring.rest.model.Task;
import org.activiti.spring.rest.model.UserGroup;
import org.activiti.spring.rest.model.UserInfo;
import org.activiti.spring.rest.web.ApplicationConversionServiceFactoryBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

privileged aspect ApplicationConversionServiceFactoryBean_Roo_ConversionService {
    
    declare @type: ApplicationConversionServiceFactoryBean: @Configurable;
    
    public Converter<Deployment, String> ApplicationConversionServiceFactoryBean.getDeploymentToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.Deployment, java.lang.String>() {
            public String convert(Deployment deployment) {
                return new StringBuilder().append(deployment.getName()).append(' ').append(deployment.getDeploymentTime()).append(' ').append(deployment.getCategory()).append(' ').append(deployment.getUrl()).toString();
            }
        };
    }
    
    public Converter<String, Deployment> ApplicationConversionServiceFactoryBean.getIdToDeploymentConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.Deployment>() {
            public org.activiti.spring.rest.model.Deployment convert(java.lang.String id) {
                return Deployment.findDeployment(id);
            }
        };
    }
    
    public Converter<Execution, String> ApplicationConversionServiceFactoryBean.getExecutionToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.Execution, java.lang.String>() {
            public String convert(Execution execution) {
                return new StringBuilder().append(execution.getActivityId()).append(' ').append(execution.getParentId()).append(' ').append(execution.getProcessInstanceId()).toString();
            }
        };
    }
    
    public Converter<String, Execution> ApplicationConversionServiceFactoryBean.getIdToExecutionConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.Execution>() {
            public org.activiti.spring.rest.model.Execution convert(java.lang.String id) {
                return Execution.findExecution(id);
            }
        };
    }
    
    public Converter<Form, String> ApplicationConversionServiceFactoryBean.getFormToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.Form, java.lang.String>() {
            public String convert(Form form) {
                return new StringBuilder().append(form.getFormKey()).append(' ').append(form.getDeploymentId()).append(' ').append(form.getProcessDefinitionId()).append(' ').append(form.getProcessDefinitionUrl()).toString();
            }
        };
    }
    
    public Converter<Long, Form> ApplicationConversionServiceFactoryBean.getIdToFormConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, org.activiti.spring.rest.model.Form>() {
            public org.activiti.spring.rest.model.Form convert(java.lang.Long id) {
                return Form.findForm(id);
            }
        };
    }
    
    public Converter<String, Form> ApplicationConversionServiceFactoryBean.getStringToFormConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.Form>() {
            public org.activiti.spring.rest.model.Form convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Form.class);
            }
        };
    }
    
    public Converter<FormProperty, String> ApplicationConversionServiceFactoryBean.getFormPropertyToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.FormProperty, java.lang.String>() {
            public String convert(FormProperty formProperty) {
                return new StringBuilder().append(formProperty.getId()).append(' ').append(formProperty.getName()).append(' ').append(formProperty.getType()).append(' ').append(formProperty.getDatePattern()).toString();
            }
        };
    }
    
    public Converter<Long, FormProperty> ApplicationConversionServiceFactoryBean.getIdToFormPropertyConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, org.activiti.spring.rest.model.FormProperty>() {
            public org.activiti.spring.rest.model.FormProperty convert(java.lang.Long id) {
                return FormProperty.findFormProperty(id);
            }
        };
    }
    
    public Converter<String, FormProperty> ApplicationConversionServiceFactoryBean.getStringToFormPropertyConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.FormProperty>() {
            public org.activiti.spring.rest.model.FormProperty convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), FormProperty.class);
            }
        };
    }
    
    public Converter<ProcessDefinition, String> ApplicationConversionServiceFactoryBean.getProcessDefinitionToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.ProcessDefinition, java.lang.String>() {
            public String convert(ProcessDefinition processDefinition) {
                return new StringBuilder().append(processDefinition.getName()).append(' ').append(processDefinition.getCategory()).append(' ').append(processDefinition.getDescription()).append(' ').append(processDefinition.getVersion()).toString();
            }
        };
    }
    
    public Converter<String, ProcessDefinition> ApplicationConversionServiceFactoryBean.getIdToProcessDefinitionConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.ProcessDefinition>() {
            public org.activiti.spring.rest.model.ProcessDefinition convert(java.lang.String id) {
                return ProcessDefinition.findProcessDefinition(id);
            }
        };
    }
    
    public Converter<ProcessInstance, String> ApplicationConversionServiceFactoryBean.getProcessInstanceToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.ProcessInstance, java.lang.String>() {
            public String convert(ProcessInstance processInstance) {
                return new StringBuilder().append(processInstance.getActivityId()).append(' ').append(processInstance.getParentId()).append(' ').append(processInstance.getProcessInstanceId()).append(' ').append(processInstance.getBusinessKey()).toString();
            }
        };
    }
    
    public Converter<String, ProcessInstance> ApplicationConversionServiceFactoryBean.getIdToProcessInstanceConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.ProcessInstance>() {
            public org.activiti.spring.rest.model.ProcessInstance convert(java.lang.String id) {
                return ProcessInstance.findProcessInstance(id);
            }
        };
    }
    
    public Converter<Task, String> ApplicationConversionServiceFactoryBean.getTaskToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.Task, java.lang.String>() {
            public String convert(Task task) {
                return new StringBuilder().append(task.getAssignee()).append(' ').append(task.getCreateTime()).append(' ').append(task.getDueDate()).append(' ').append(task.getDelegateState()).toString();
            }
        };
    }
    
    public Converter<String, Task> ApplicationConversionServiceFactoryBean.getIdToTaskConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.Task>() {
            public org.activiti.spring.rest.model.Task convert(java.lang.String id) {
                return Task.findTask(id);
            }
        };
    }
    
    public Converter<UserGroup, String> ApplicationConversionServiceFactoryBean.getUserGroupToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.UserGroup, java.lang.String>() {
            public String convert(UserGroup userGroup) {
                return new StringBuilder().append(userGroup.getId()).append(' ').append(userGroup.getName()).append(' ').append(userGroup.getType()).toString();
            }
        };
    }
    
    public Converter<Long, UserGroup> ApplicationConversionServiceFactoryBean.getIdToUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, org.activiti.spring.rest.model.UserGroup>() {
            public org.activiti.spring.rest.model.UserGroup convert(java.lang.Long id) {
                return UserGroup.findUserGroup(id);
            }
        };
    }
    
    public Converter<String, UserGroup> ApplicationConversionServiceFactoryBean.getStringToUserGroupConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.UserGroup>() {
            public org.activiti.spring.rest.model.UserGroup convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), UserGroup.class);
            }
        };
    }
    
    public Converter<UserInfo, String> ApplicationConversionServiceFactoryBean.getUserInfoToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<org.activiti.spring.rest.model.UserInfo, java.lang.String>() {
            public String convert(UserInfo userInfo) {
                return new StringBuilder().append(userInfo.getKey()).append(' ').append(userInfo.getValue()).toString();
            }
        };
    }
    
    public Converter<String, UserInfo> ApplicationConversionServiceFactoryBean.getIdToUserInfoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, org.activiti.spring.rest.model.UserInfo>() {
            public org.activiti.spring.rest.model.UserInfo convert(java.lang.String id) {
                return UserInfo.findUserInfo(id);
            }
        };
    }
    
    public void ApplicationConversionServiceFactoryBean.installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getDeploymentToStringConverter());
        registry.addConverter(getIdToDeploymentConverter());
        registry.addConverter(getExecutionToStringConverter());
        registry.addConverter(getIdToExecutionConverter());
        registry.addConverter(getFormToStringConverter());
        registry.addConverter(getIdToFormConverter());
        registry.addConverter(getStringToFormConverter());
        registry.addConverter(getFormPropertyToStringConverter());
        registry.addConverter(getIdToFormPropertyConverter());
        registry.addConverter(getStringToFormPropertyConverter());
        registry.addConverter(getProcessDefinitionToStringConverter());
        registry.addConverter(getIdToProcessDefinitionConverter());
        registry.addConverter(getProcessInstanceToStringConverter());
        registry.addConverter(getIdToProcessInstanceConverter());
        registry.addConverter(getTaskToStringConverter());
        registry.addConverter(getIdToTaskConverter());
        registry.addConverter(getUserGroupToStringConverter());
        registry.addConverter(getIdToUserGroupConverter());
        registry.addConverter(getStringToUserGroupConverter());
        registry.addConverter(getUserInfoToStringConverter());
        registry.addConverter(getIdToUserInfoConverter());
    }
    
    public void ApplicationConversionServiceFactoryBean.afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
}