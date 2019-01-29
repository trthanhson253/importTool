package vn.com.fwd.importtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.com.fwd.importtool.model.User;

public interface UserRepository extends JpaRepository<User, String> {
	User findByUsername(String username);
}
