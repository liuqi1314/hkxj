package cn.hkxj.platform.mapper;

import cn.hkxj.platform.pojo.wechat.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface TaskMapper {
    String isTaskBinding(@Param("openid") String openid,@Param("updateType") int updateType);

    int taskBinding(Task task);

    int taskCountUpdate(@Param("openid") String openid,@Param("updateType") int updateType);
}
