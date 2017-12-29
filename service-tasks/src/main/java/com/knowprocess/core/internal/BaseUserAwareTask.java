package com.knowprocess.core.internal;

import javax.annotation.Nonnull;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class capable of looking up user settings such as for external accounts.
 */
public abstract class BaseUserAwareTask implements JavaDelegate {

    public static final Logger LOGGER = LoggerFactory
            .getLogger(BaseUserAwareTask.class);

    private UserInfoHelper userInfoHelper;

    protected Expression resourceUsername;
    protected Expression resourcePassword;
    protected Expression jwtLoginResource;

    protected UserInfoHelper getUserInfoHelper() {
        if (userInfoHelper == null) {
            userInfoHelper = new UserInfoHelper();
        }
        return userInfoHelper;
    }

    protected String lookup(DelegateExecution execution, @Nonnull String usr, @Nonnull Expression expr) {
        return getUserInfoHelper().lookup(execution, usr, expr);
    }

    protected String getPassword(DelegateExecution execution, String usr) {
        return resourcePassword == null ? null : lookup(execution, usr,
                resourcePassword);
    }

    protected String getUsername(DelegateExecution execution) {
        LOGGER.info("getUsername...");
        if (resourceUsername == null) {
            LOGGER.info("... not specified");
            return null;
        } else if (resourceUsername.getExpressionText().equals(
                "userInfo('tenant-bot')")) {
            LOGGER.info("... use tenant bot");
            return lookupBotName(execution);
        } else if (resourceUsername.getExpressionText()
                .startsWith("userInfo('")) {
            LOGGER.info("... use property of tenant bot");
            return lookup(execution, lookupBotName(execution), resourceUsername);
        } else {
            LOGGER.info("... use expression directly: {}", resourceUsername.getExpressionText());
            return (String) resourceUsername.getValue(execution);
        }
    }

    protected String lookupBotName(DelegateExecution execution) {
        return getUserInfoHelper().lookupBotName(execution);
    }

    /**
     * No-op implementation for sub-classes to override.
     */
    @Override
    public void execute(DelegateExecution execution) throws Exception {
    }

}
