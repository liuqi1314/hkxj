package cn.hkxj.platform.dao;

import cn.hkxj.platform.mapper.UrpClassroomMapper;
import cn.hkxj.platform.pojo.UrpClassroom;
import cn.hkxj.platform.pojo.example.UrpClassroomExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UrpClassRoomDao {
    @Resource
    private UrpClassroomMapper urpClassroomMapper;

    public List<UrpClassroom> getAllClassroom(){
        return selectByClassroom(new UrpClassroom());
    }

    public List<UrpClassroom> selectByClassroom(UrpClassroom urpClassroom){
        UrpClassroomExample urpClassroomExample = new UrpClassroomExample();
        UrpClassroomExample.Criteria criteria = urpClassroomExample.createCriteria();

        if(urpClassroom.getNumber() != null){
            criteria.andNumberEqualTo(urpClassroom.getNumber());
        }

        return urpClassroomMapper.selectByExample(urpClassroomExample);
    }

    public void insertSelective(UrpClassroom urpClassroom){
        urpClassroomMapper.insertSelective(urpClassroom);
    }
}
