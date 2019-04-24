package com.shield.learn.service;

import com.shield.learn.dao.XcLearningCourseRepository;
import com.shield.learn.dao.XcTaskHisRepository;
import com.shield.model.learning.XcLearningCourse;
import com.shield.model.response.CommonCode;
import com.shield.model.response.ResponseResult;
import com.shield.model.task.XcTask;
import com.shield.model.task.XcTaskHis;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class LearningService {
    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    XcLearningCourseRepository xcLearningCourseRepository;

    //完成选课
    @Transactional
    public ResponseResult addcourse(String userId, String courseId, String valid, Date
            startTime, Date endTime, XcTask xcTask) {
        XcLearningCourse xcLearningCourse =
                xcLearningCourseRepository.findXcLearningCourseByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse == null) {
            //没有选课记录则添加
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
//            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        } else {
            //有选课记录则更新选课记录
//            xcLearningCourse.setValid(valid);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus("501001");
            xcLearningCourseRepository.save(xcLearningCourse);
        }
        //向历史任务表播入记录
        Optional<XcTaskHis> optional = xcTaskHisRepository.findById(xcTask.getId());
        if (!optional.isPresent()) {
            //历史任务不存在，就添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
