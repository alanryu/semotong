package kr.co.ucomp.web.bizpurio.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BizPurioMsgResEntity {
	private int id;
	@JsonProperty("DEVICE")
	private String DEVICE;
	
	@JsonProperty("CMSGID")
	private String CMSGID;
	
	@JsonProperty("MSGID")
	private String MSGID;
	
	@JsonProperty("PHONE")
	private String PHONE;
	
	@JsonProperty("MEDIA")
	private String MEDIA;
	
	@JsonProperty("TO_NAME")
	private String TO_NAME;
	
	@JsonProperty("UNIXTIME")
	private String UNIXTIME;
	
	@JsonProperty("RESULT")
	private String RESULT;
	
	@JsonProperty("USERDATA")
	private String USERDATA;
	
	@JsonProperty("WAPINFO")
	private String WAPINFO;
	
	@JsonProperty("TELRES")
	private String TELRES;
	
	@JsonProperty("TELTIME")
	private String TELTIME;
	
	@JsonProperty("KAORES")
	private String KAORES;
	
	@JsonProperty("KAOTIME")
	private String KAOTIME;
	
	@JsonProperty("RCSRES")
	private String RCSRES;
	
	@JsonProperty("RCSTIME")
	private String RCSTIME;
	
	@JsonProperty("RETRY_FLAG")
	private String RETRY_FLAG;
	
	@JsonProperty("RESEND_FLAG")
	private String RESEND_FLAG;
	
	@JsonProperty("REFKEY")
	private String REFKEY;
	
	private LocalDateTime createDate;

}
