// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.activiti.spring.rest.web;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.activiti.spring.rest.model.FormProperty;
import org.activiti.spring.rest.model.ProcessDefinition;
import org.activiti.spring.rest.web.ProcessDefinitionController;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect ProcessDefinitionController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String ProcessDefinitionController.create(@Valid ProcessDefinition processDefinition, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, processDefinition);
            return "process-definitions/create";
        }
        uiModel.asMap().clear();
        processDefinition.persist();
        return "redirect:/process-definitions/" + encodeUrlPathSegment(processDefinition.getId_().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String ProcessDefinitionController.createForm(Model uiModel) {
        populateEditForm(uiModel, new ProcessDefinition());
        return "process-definitions/create";
    }
    
    @RequestMapping(value = "/{id_}", produces = "text/html")
    public String ProcessDefinitionController.show(@PathVariable("id_") Long id_, Model uiModel) {
        uiModel.addAttribute("processdefinition", ProcessDefinition.findProcessDefinition(id_));
        uiModel.addAttribute("itemId", id_);
        return "process-definitions/show";
    }
    
    @RequestMapping(produces = "text/html")
    public String ProcessDefinitionController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("processdefinitions", ProcessDefinition.findProcessDefinitionEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) ProcessDefinition.countProcessDefinitions() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("processdefinitions", ProcessDefinition.findAllProcessDefinitions(sortFieldName, sortOrder));
        }
        return "process-definitions/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String ProcessDefinitionController.update(@Valid ProcessDefinition processDefinition, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, processDefinition);
            return "process-definitions/update";
        }
        uiModel.asMap().clear();
        processDefinition.merge();
        return "redirect:/process-definitions/" + encodeUrlPathSegment(processDefinition.getId_().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id_}", params = "form", produces = "text/html")
    public String ProcessDefinitionController.updateForm(@PathVariable("id_") Long id_, Model uiModel) {
        populateEditForm(uiModel, ProcessDefinition.findProcessDefinition(id_));
        return "process-definitions/update";
    }
    
    @RequestMapping(value = "/{id_}", method = RequestMethod.DELETE, produces = "text/html")
    public String ProcessDefinitionController.delete(@PathVariable("id_") Long id_, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        ProcessDefinition processDefinition = ProcessDefinition.findProcessDefinition(id_);
        processDefinition.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/process-definitions";
    }
    
    void ProcessDefinitionController.populateEditForm(Model uiModel, ProcessDefinition processDefinition) {
        uiModel.addAttribute("processDefinition", processDefinition);
        uiModel.addAttribute("formpropertys", FormProperty.findAllFormPropertys());
    }
    
    String ProcessDefinitionController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}