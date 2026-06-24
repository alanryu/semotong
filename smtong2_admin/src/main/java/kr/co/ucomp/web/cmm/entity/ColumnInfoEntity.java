package kr.co.ucomp.web.cmm.entity;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ColumnInfoEntity {
	private String tableName;
	private String columnName;
	private String columnComment;
}
