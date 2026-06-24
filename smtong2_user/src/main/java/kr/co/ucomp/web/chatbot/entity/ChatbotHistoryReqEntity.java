package kr.co.ucomp.web.chatbot.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatbotHistoryReqEntity {

    private boolean mvnoUseYn;

    private String preMno;

    private String prevPayment;

    private String selData;

    private int selCompany;

    private int elPlanId;

    private Long user;

    public ChatbotHistoryReqEntity(
            boolean mvnoUseYn,
            String preMno,
            String prevPayment,
            String selData,
            int selCompany,
            int elPlanId,
            Long user
    ) {
        this.mvnoUseYn = mvnoUseYn;
        this.preMno = preMno == null ? null : preMno;
        this.prevPayment = prevPayment == null ? null : prevPayment;
        this.selData = selData == null ? null : selData;
        this.selCompany = selCompany;
        this.elPlanId = elPlanId;
        this.user = user;
    }
}
