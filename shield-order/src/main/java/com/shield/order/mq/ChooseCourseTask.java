package com.shield.order.mq;

import com.shield.model.task.XcTask;
import com.shield.order.config.RabbitMQConfig;
import com.shield.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    TaskService taskService;

    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE})
    public void receiveFinishChooseTask(XcTask xcTask) {
        if (xcTask != null && StringUtils.isNotEmpty(xcTask.getId())) {
            taskService.finishTask(xcTask.getId());
        }
    }

    //每隔1分钟扫描消息表，向mq发送消息
    @Scheduled(fixedDelay = 60000)
//    @Scheduled(cron = "0 0/3 * * * *")
    public void sendChoosecourseTask() {
        //取出当前时间1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        List<XcTask> taskList = taskService.findTaskList(time, 1000);
        System.out.println(taskList);
        //调用service发布消息，将添加选课的任务发送给mq。(定时发送任务）
        //遍历任务列表
        for (XcTask xcTask : taskList) {
            //取任务。
            if (taskService.getTask(xcTask.getId(), xcTask.getVersion()) > 0) {
                //发送选课消息
                //taskService.publish(xcTask,xcTask.getMqExchange(),xcTask.getMqRoutingkey());
                //LOGGER.info("send choose course task id:{}",xcTask.getId());
                String ec = xcTask.getMqExchange();//要发送的交换机。
                String mqRoutingkey = xcTask.getMqRoutingkey();//发送消息要带routingkey
                taskService.publish(xcTask, ec, mqRoutingkey);
            }
        }
    }
}
