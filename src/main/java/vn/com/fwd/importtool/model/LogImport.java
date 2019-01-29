package vn.com.fwd.importtool.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="import_log")
public class LogImport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long id;
	private String action;
	@Column(name="username")
	private String userName;
	@Column(name="create_time")
	private Date createTime;
	private String result;
	@Column(name="file_name")
	private String fileName;
	@Column(name="template_id")
	private Long templateId;
	@Column(name="log_message")
	private String logMessage;
	@Column(name="byte_content")
	private byte[] byteFileImport;
	
	public String getStrCreateTime() {
		if (createTime == null) {
			return "";
		}
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return df.format(createTime);
	}
}
