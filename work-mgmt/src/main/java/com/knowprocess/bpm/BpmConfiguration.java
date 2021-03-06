/*******************************************************************************
 *Copyright 2011-2018 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.knowprocess.bpm;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.bpmn.parser.factory.ActivityBehaviorFactory;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.activiti.spring.boot.DataSourceProcessEngineAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.PlatformTransactionManager;

import com.knowprocess.bpm.impl.CustomActivityBehaviorFactory;
import com.knowprocess.bpm.impl.JobRetryMapper;
import com.knowprocess.bpm.impl.JsonManager;
import com.knowprocess.bpm.impl.TaskAllocationMapper;
import com.knowprocess.bpmn.BpmnRestHelper;

@Configuration
@ComponentScan(basePackages = { "com.knowprocess.bpm",
        "com.knowprocess.services", "link.omny.acctmgmt" })
@EntityScan({ "com.knowprocess.bpm", "link.omny.acctmgmt.model" })
@EnableJpaRepositories({
        "com.knowprocess.bpm.repositories", "link.omny.acctmgmt.repositories" })
@AutoConfigureBefore(DataSourceProcessEngineAutoConfiguration.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class BpmConfiguration extends AbstractProcessEngineAutoConfiguration {

    protected static final Logger LOGGER = LoggerFactory
            .getLogger(BpmConfiguration.class);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private MultiTenantActivitiProperties overrideProperties;

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            DataSource activitiDataSource,
            PlatformTransactionManager transactionManager,
            SpringAsyncExecutor springAsyncExecutor) throws IOException {

        SpringProcessEngineConfiguration config = this
                .baseSpringProcessEngineConfiguration(activitiDataSource,
                        transactionManager, springAsyncExecutor);
        config.setJpaEntityManagerFactory(entityManagerFactory);
        config.setTransactionManager(transactionManager);
        config.setJpaHandleTransaction(false);
        config.setJpaCloseEntityManager(false);

        config.setDataSource(activitiDataSource);
        config.setMailServers(overrideProperties.getServers());

        Set<Class<?>> mappers = new HashSet<Class<?>>();
        mappers.add(TaskAllocationMapper.class);
        mappers.add(JobRetryMapper.class);
        config.setCustomMybatisMappers(mappers);

        // instead diagrams created 'on the fly' by ProcessDefinitionController
        config.getProcessEngineConfiguration().setCreateDiagramOnDeploy(false);

        exposeBeansToProcessEngine(config);
        
        // override activity implementations
        config.setActivityBehaviorFactory(activityBehaviorFactory());

        Map<Object, Object> beans = new HashMap<Object, Object>();
        beans.put(BpmnRestHelper.ID, new BpmnRestHelper());
        config.setBeans(beans);

        return config;
    }

    protected void exposeBeansToProcessEngine(
            SpringProcessEngineConfiguration config) {
        if (config.getBeans() == null) {
            config.setBeans(new HashMap<Object,Object>());
        }
        config.getBeans().putIfAbsent("gbpFormatter", gbpFormatter());
        if (LOGGER.isInfoEnabled()) {
            Map<Object, Object> beans = config.getBeans();
            if (beans == null || beans.isEmpty()) {
                LOGGER.info("No beans available to processes");
            } else {
                LOGGER.info("Beans available to processes:");
                for (Entry<Object, Object> entries : config.getBeans().entrySet()) {
                    LOGGER.info("  {}: {}", entries.getKey(), entries.getValue().getClass().getName());
                }
            }
        }
    }

    @Bean
    public ActivityBehaviorFactory activityBehaviorFactory() {
        return new CustomActivityBehaviorFactory();
    }

    // TODO use separate datasource for activiti?
    // @Bean
    // public DataSource activitiDataSource() {
    // return DataSourceBuilder.create().url("jdbc:h2:mem:customer:H2")
    // .driverClassName("org.h2.Driver").username("sa").password("")
    // .build();
    // }

    // @Bean
    // public SpringProcessEngineConfiguration springProcessEngineConfiguration(
    // DataSource dataSource,
    // PlatformTransactionManager transactionManager,
    // SpringAsyncExecutor springAsyncExecutor) throws IOException {
    //
    // // setActivitiProperties(overrideProperties);
    //
    // SpringProcessEngineConfiguration config = this
    // .baseSpringProcessEngineConfiguration(dataSource,
    // transactionManager, springAsyncExecutor);
    // config.setJpaEntityManagerFactory(entityManagerFactory);
    // config.setTransactionManager(transactionManager);
    // config.setJpaHandleTransaction(false);
    // config.setJpaCloseEntityManager(false);
    //
    // config.setMailServers(overrideProperties.getServers());
    //
    // return config;
    // }

    @Bean
    public JsonManager jsonManager() {
        return new JsonManager();
    }

    @Bean
    protected PropertiesFactoryBean messageAliases() {
        PropertiesFactoryBean fact = new PropertiesFactoryBean();
        fact.setLocation(new ClassPathResource("messageAliases.properties"));
        return fact;
    }

    @Bean
    protected NumberFormat gbpFormatter() {
        NumberFormat gbpFormatter = NumberFormat.getCurrencyInstance(Locale.UK);
        gbpFormatter.setMinimumFractionDigits(2);
        gbpFormatter.setRoundingMode(RoundingMode.HALF_UP);
        return gbpFormatter;
    }

    @Bean
    public CommandLineRunner init(final RepositoryService repositoryService,
            final RuntimeService runtimeService,
            final IdentityService identityService, final TaskService taskService) {

        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(
                            String.format("Number of process definitions: %1$d",
                            repositoryService.createProcessDefinitionQuery()
                                            .count()));
                    LOGGER.info(
                            String.format("Number of users: %1$d",
                            identityService.createUserQuery().count()));
                    if (LOGGER.isDebugEnabled()) {
                        List<User> users = identityService.createUserQuery()
                                .list();
                        for (User user : users) {
                            LOGGER.debug(String.format("... : %1$s",
                                    user.getId()));
                        }
                    }
                }
            }
        };

    }

}
