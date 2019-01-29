package vn.com.fwd.importtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.com.fwd.importtool.model.Role;

public interface RoleRepository extends JpaRepository<Role, String> {

}
