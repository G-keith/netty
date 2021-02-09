package com.keith.demo.task;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keith
 * @version 1.0
 * @date 2021-02-06
 */
@RestController
@RequestMapping("/netty/task")
public class QuartzController {

    @Autowired
    private Scheduler scheduler;

    @GetMapping("/add")
    public String addTimeJob(String job, int time) throws SchedulerException {
        Map<String, Object> param = new HashMap<>(2);
        param.put("name", job);
        QuartzUtils.createJob(scheduler, GatewayJob.class, job, "gateway", time, param);
        return "OK";
    }

    @GetMapping("/pause")
    public String pauseJob(String job) throws SchedulerException {
        QuartzUtils.pauseJob(scheduler, job, "gateway");
        return "OK";
    }

    @GetMapping("/resume")
    public String resumeJob(String job) throws SchedulerException {
        QuartzUtils.resumeJob(scheduler, job, "gateway");
        return "OK";
    }

    @GetMapping("/update")
    public String updateJob(String job, int time) throws SchedulerException {
        QuartzUtils.refreshJob(scheduler, job, "gateway", time);
        return "OK";
    }

    @GetMapping("/delete")
    public String deleteJob(String job) throws SchedulerException {
        QuartzUtils.deleteJob(scheduler, job, "gateway");
        return "OK";
    }

    @GetMapping("/trigger")
    public String triggerJob(String job) throws SchedulerException {
        QuartzUtils.triggerJob(scheduler, job, "gateway");
        return "OK";
    }

}
