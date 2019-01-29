package vn.com.fwd.importtool.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class SearchDataDTO {
	private Long templateId;
	private String strFromDate;
	private String strToDate;
	
	public Date getFromDate() {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(strFromDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public Date getToDate() {
		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(strToDate);
		} catch (ParseException e) {
			return null;
		}
	}
}
