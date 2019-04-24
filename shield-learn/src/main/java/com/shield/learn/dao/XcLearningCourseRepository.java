package com.shield.learn.dao;

import com.shield.model.learning.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse, String> {
    //根据用户和课程查询选课记录，用于判断是否添加选课
    //根据用户id和课程id查询
    XcLearningCourse findXcLearningCourseByUserIdAndCourseId(String userId, String courseId);
}
