package kr.co.ucomp.web.order.service;

import kr.co.ucomp.web.order.mapper.DailySequenceMapper;
import kr.co.ucomp.web.order.mapper.PlanOrderMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class DailySequenceService {
	
	 @Autowired private DailySequenceMapper mapper;


    public int getNextSequence() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 현재 날짜에 해당하는 순번 조회
        Integer currentSeq = mapper.getCurrentSequence(today);

        if (currentSeq == null) {
            // 날짜가 없으면 새로운 날짜로 초기화
            Map<String, Object> param = new HashMap<>();
            param.put("ymd", today);
            param.put("seq", 1);
            mapper.insertNewDate(param);
            return 1;
        } else {
            // 날짜가 이미 있으면 순번 업데이트
            Map<String, Object> param = new HashMap<>();
            param.put("ymd", today);
            mapper.updateSequence(param);
            return currentSeq + 1;
        }
    }
}
