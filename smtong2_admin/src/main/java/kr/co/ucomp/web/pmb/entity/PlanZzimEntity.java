package kr.co.ucomp.web.pmb.entity;

import java.time.LocalDateTime;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanZzimEntity {
    private int id;
    private int userMngId;    
    private int prodId;
    private LocalDateTime createDate;

}
