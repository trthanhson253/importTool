package vn.com.fwd.importtool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.com.fwd.importtool.model.Setting;

public interface SettingRepository extends JpaRepository<Setting, Long> {
	List<Setting> findByTemplateName (String templateName);
	
	
}
