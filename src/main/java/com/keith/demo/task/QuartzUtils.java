package com.keith.demo.task;

import org.quartz.*;

import java.util.Map;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-06
 */
public class QuartzUtils {

    /**
     * 创建定时任务
     * @param scheduler
     * @param jobClass
     * @param jobName
     * @param jobGroup
     * @param time
     * @param param
     * @throws SchedulerException
     */
    public static void createJob(Scheduler scheduler, Class<? extends Job> jobClass,
                                 String jobName, String jobGroup,
                                 int time, Map<String, Object> param) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .build();
        if (param != null && !param.isEmpty()) {
            param.forEach((key, value) -> jobDetail.getJobDataMap().put(key, value));
        }
        scheduler.scheduleJob(jobDetail, buildCronTrigger(jobName, jobGroup, time));
    }

    /**
     * 更新定时任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @param time
     * @throws SchedulerException
     */
    public static void refreshJob(Scheduler scheduler, String jobName, String jobGroup,
                                  int time) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        scheduler.rescheduleJob(triggerKey, buildCronTrigger(jobName, jobGroup, time));
    }

    /**
     * 构建Trigger对象
     * @param jobName
     * @param jobGroup
     * @param time
     * @return
     */
    private static Trigger buildCronTrigger(String jobName, String jobGroup, int time) {
        SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(time).repeatForever();
        return TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroup)
                .withSchedule(simpleSchedule)
                .build();
    }

    /**
     * 恢复定时任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public static void resumeJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 暂停定时任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public static void pauseJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 删除定时任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public static void deleteJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, jobGroup));
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 立即执行一次任务
     * @param scheduler
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public static void triggerJob(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        scheduler.triggerJob(JobKey.jobKey(jobName, jobGroup));
    }
}
