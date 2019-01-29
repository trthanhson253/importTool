package vn.com.fwd.importtool.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name="import_users")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(columnDefinition = "varchar(50)")
    private String username;

    @Deprecated
    @Builder.Default
    private String password = UUID.randomUUID().toString();

    @Builder.Default
    private boolean enabled = true;
    private String email;
    
    @ElementCollection
    @CollectionTable(
            name = "IMPORT_USER_ROLE",
            joinColumns = @JoinColumn(name = "username", columnDefinition = "varchar(50)"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "role"})}
    )
    @Column(name = "role", columnDefinition = "varchar(255)")
    private List<String> roles;
}
