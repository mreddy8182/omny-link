package com.knowprocess.logging;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.knowprocess.beans.ModelBasedConversionService;

public class LoggingService implements JavaDelegate {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(ModelBasedConversionService.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        if (LOGGER.isInfoEnabled()) {
            for (String name : execution.getVariableNames()) {
                LOGGER.info(name + " = " + execution.getVariable(name));
            }
        }
    }

}