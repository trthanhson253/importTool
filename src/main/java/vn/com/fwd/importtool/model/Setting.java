package vn.com.fwd.importtool.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="import_setting")
public class Setting implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue
	private long id;
	@Column(name="name")
	private String templateName;
	@Column(name="columns")
	private String header;
	@Column(name="sql_update")
	private String sqlUpdate;
	@Column(name="sql_insert")
	private String sqlInsert;
	
	private String description;
	@Column(name="colums_not_empty")
	private String columnsNotEmpty;
	@Column(name="fis_template")
	private byte[] fisTemplate;
	@Column(name="extension")
	private String extensionFile;
	@Column(name="db_execute")
	private String db_execute;
	@Column(name="sql_select")
	private String sqlSelect;
}
