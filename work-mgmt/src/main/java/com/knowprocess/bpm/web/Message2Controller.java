package com.knowprocess.bpm.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Handle REST requests sending BPMN message events to start or modify process
 * instances.
 * 
 * @author Tim Stephenson
 * 
 */
@Controller
@RequestMapping("/{tenant}/messages")
public class Message2Controller {

	protected static final Logger LOGGER = LoggerFactory
			.getLogger(Message2Controller.class);

	@Autowired
	protected ProcessEngine processEngine;

	@Autowired
	protected MessageController messageController;

	@RequestMapping(method = RequestMethod.POST, value = "/{msgId}", headers = {
			"Accept=application/json", "Content-Type=application/json" })
	@ResponseBody
	public ResponseEntity<String> handleMessageStart(
			UriComponentsBuilder uriBuilder,
			@PathVariable("tenant") String tenantId,
			@PathVariable("msgId") String msgId,
			@RequestParam(required = false, value = "businessDescription") String bizDesc,
			@RequestBody String json) {
		return messageController.doInOutMep(uriBuilder, tenantId, msgId,
				bizDesc, json);
	}

  /**
   * Handle a message destined for an intermediate message catch event of an 
   * existing process.
   */ 
	@RequestMapping(method = RequestMethod.POST, value = "/{msgId}/{instanceId}")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void handleMessage(@PathVariable("tenant") String tenantId,
			@PathVariable("msgId") String msgId,
			@PathVariable("instanceId") String instanceId,
			@RequestBody JsonNode json) {
		long start = System.currentTimeMillis();
		LOGGER.info("handleMessage: " + msgId + ", json:" + json);

		if (LOGGER.isDebugEnabled()) {
			List<String> activeActivityIds = processEngine.getRuntimeService()
					.getActiveActivityIds(instanceId);
			LOGGER.debug("Active ids: " + activeActivityIds);
		}
		Map<String, Object> vars = new HashMap<String, Object>();
		String s = json.toString();
		vars.put(messageController.getMessageVarName(msgId), s);
		processEngine.getRuntimeService().messageEventReceived(msgId,
				instanceId, vars);

		LOGGER.debug(String.format("handleMessage took: %1$s ms",
				(System.currentTimeMillis() - start)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{msgId}", headers = "Accept=application/json")
	public @ResponseBody List<com.knowprocess.bpm.model.Execution> getEventListeners(
			@PathVariable("tenant") String tenantId,
			@PathVariable("msgId") String msgId) {
		LOGGER.info(String.format("getEventListeners(%1$s, %2$s)", tenantId,
				msgId));

		List<Execution> list = processEngine.getRuntimeService()
				.createExecutionQuery().messageEventSubscriptionName(msgId)
				.list();
		if (list.size() == 0) {
			throw new ActivitiObjectNotFoundException(String.format(
					"No message subscriptions to %1$s for %2$s", msgId,
					tenantId));
		} else {
			LOGGER.debug(String.format("... found %1$d", list.size()));
		}
		// TODO add createProcessInstanceQuery().executionIds to mirror processInstanceIds()
		List<com.knowprocess.bpm.model.Execution> piList = new ArrayList<com.knowprocess.bpm.model.Execution>();
		 for (Execution execution : list) {
			com.knowprocess.bpm.model.Execution exe = new com.knowprocess.bpm.model.Execution(execution);
			ProcessInstance pi = processEngine.getRuntimeService()
					.createProcessInstanceQuery()
					.processInstanceId(execution.getProcessInstanceId())
					.singleResult();
			exe.setBusinessKey(pi.getBusinessKey());
			piList.add(exe);
		 }
		LOGGER.info(String.format(" found %1$s, instances", piList.size()));
		return piList;
//		return findNativelyProcessInstancesWhereExecutionIn(list);
	}

	// TODO Unreliable?!
	private List<com.knowprocess.bpm.model.ProcessInstance> findNativelyProcessInstancesWhereExecutionIn(
			List<Execution> list) {
		StringBuffer sb = new StringBuffer();
		for (Execution execution : list) {
			sb.append(execution.getId()).append(",");
		}
		sb = sb.deleteCharAt(sb.length() - 1);
		return com.knowprocess.bpm.model.ProcessInstance.wrap(processEngine
				.getRuntimeService().createNativeExecutionQuery()
				.sql("SELECT * FROM ACT_RU_EXECUTION WHERE id_ IN (#{ids})")
				.parameter("ids", sb.toString()).list());
	}

}