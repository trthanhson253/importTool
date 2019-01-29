package vn.com.fwd.importtool.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;


import lombok.Data;

@Data
@Entity
@Table(name="import_roles")
public class Role implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private String name;
	@Column(columnDefinition = "nvarchar(max)")
    public String description;
	@ElementCollection
    @CollectionTable(
            name = "IMPORT_USER_ROLE",
            joinColumns = @JoinColumn(name = "role"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "role"})}
    )
    @Column(name = "username")
    private List<String> userNames;
}
