package com.shield.order.service;

import com.shield.model.task.XcTask;
import com.shield.model.task.XcTaskHis;
import com.shield.order.dao.XcTaskHisRepository;
import com.shield.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    XcTaskRepository xcTaskRepository;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    //取出前n条任务，取出指定时间之前处理的任务
    public List<XcTask> findTaskList(Date updateTime, int n) {
        //设置分页参数，取出前n条记录
        Pageable pageable = new PageRequest(0, n);
        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return xcTasks.getContent();
    }

    /**
     * 发送消息
     *
     * @param xcTask     任务对象
     * @param ex         交换机id
     * @param routingKey
     */
    @Transactional
    public void publish(XcTask xcTask, String ex, String routingKey) {
        //查询任务(先从数据库查询任务）
        Optional<XcTask> taskOptional = xcTaskRepository.findById(xcTask.getId());
        if (taskOptional.isPresent()) {
            //发送
            //String exchange,String routingKey,Object object
            rabbitTemplate.convertAndSend(ex, routingKey, xcTask);
            //更新任务时间为当前时间
            XcTask xcTask1 = taskOptional.get();
            xcTask1.setUpdateTime(new Date());
            xcTaskRepository.save(xcTask1);
        }
    }

    //使用乐观锁方法校验任务(获取任务)
    @Transactional
    public int getTask(String taskId, int version) {
        //通过乐观锁的方式来更新数据表，如果结果大于0说明取到任务。
        int i = xcTaskRepository.updateTaskVersion(taskId, version);
        return i;
    }

    //完成任务
    @Transactional
    public void finishTask(String taskId) {
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            //当前任务
            XcTask xcTask = taskOptional.get();
//            xcTask.setDeleteTime(new Date());
            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }
}
