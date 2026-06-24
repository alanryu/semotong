package kr.co.ucomp.web.order.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface DailySequenceMapper {
    Integer getCurrentSequence(@Param("ymd") String ymd);
    void insertNewDate(@Param("param") Map<String, Object> param);
    void updateSequence(@Param("param") Map<String, Object> param);
}
