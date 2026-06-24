package kr.co.ucomp.web.plan.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlanZzimEntity {
    private int id;
    private int userMngId;    
    private int prodId;
    private LocalDateTime createDate;

}
