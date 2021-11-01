package com.xxl.job.executor.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
@AllArgsConstructor
public class SampleXxlJob {

    private final JobOperator jobOperator;

    @XxlJob(value = "enableSpringBatchJobHandler")
    public void enableSpringBatchJobHandler() throws Exception {
        String jobParam = XxlJobHelper.getJobParam();
        XxlJobHelper.log("XXL-JOB, " + jobParam);
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addDate("date", new Date());
        jobOperator.start(Objects.requireNonNull(jobParam), builder.toJobParameters().toString());
        XxlJobHelper.log("XXL-JOB, end.");
    }
}
