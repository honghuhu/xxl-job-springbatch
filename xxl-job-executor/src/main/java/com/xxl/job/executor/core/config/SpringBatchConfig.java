package com.xxl.job.executor.core.config;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuxiang
 */
@Configuration
@EnableBatchProcessing(modular = true)
public class SpringBatchConfig implements ApplicationContextAware {
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;
    private ApplicationContext applicationContext;

    public SpringBatchConfig(JobLauncher jobLauncher, JobRegistry jobRegistry, JobExplorer jobExplorer, JobRepository jobRepository) {
        this.jobLauncher = jobLauncher;
        this.jobRegistry = jobRegistry;
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
    }

    @Bean
    public JobOperator jobOperator() {
        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
        simpleJobOperator.setJobLauncher(jobLauncher);
        simpleJobOperator.setJobExplorer(jobExplorer);
        simpleJobOperator.setJobRegistry(jobRegistry);
        simpleJobOperator.setJobRepository(jobRepository);
        simpleJobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
        return simpleJobOperator;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() throws Exception {
        JobRegistryBeanPostProcessor postProcessor = new JobRegistryBeanPostProcessor();
        postProcessor.setJobRegistry(jobRegistry);
        postProcessor.setBeanFactory(applicationContext);
        postProcessor.afterPropertiesSet();
        return postProcessor;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
