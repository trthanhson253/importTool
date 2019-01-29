package vn.com.fwd.importtool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.com.fwd.importtool.model.LogImport;

public interface LogImportRepository extends JpaRepository<LogImport, Long> {
	List<LogImport> findByTemplateId(Long tempateId);
	List<LogImport> findByTemplateIdAndUserName(Long tempateId, String userName);
}
