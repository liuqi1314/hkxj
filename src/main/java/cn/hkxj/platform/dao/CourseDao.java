package cn.hkxj.platform.dao;

import cn.hkxj.platform.mapper.CourseMapper;
import cn.hkxj.platform.pojo.Course;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author junrong.chen
 * @date 2019/6/25
 */
@Service
public class CourseDao {
    @Resource
    private CourseMapper courseMapper;

    public List<Course> selectCourseByUid(List<String> courseIdList){
        return courseMapper.selectCourseByUid(courseIdList);
    }

    public List<Course> selectCourseByUid(String uid){
        return courseMapper.selectCourseByUid(Lists.newArrayList(uid));
    }

    public boolean ifExistCourse(String uid){
        return courseMapper.ifExistCourse(uid);
    }

    public int insert(Course course){
        return courseMapper.insert(course);
    }
}
