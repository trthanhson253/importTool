package vn.com.fwd.importtool.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import vn.com.fwd.importtool.model.Setting;
import vn.com.fwd.importtool.repository.SettingRepository;

@Service
public class SettingService {
	@Autowired
	private SettingRepository settingRepository;
	
	@CacheEvict(value={"settingsImportHeader",  "settingsImportSQLInsert", "settingsImportSQLUpdate", "settingsImportName", 
			"settingsImportColumnNotEmpty", "settingsImportServerName", "settingsSQLSelect"}, allEntries = true)
	public Setting save(Setting setting) {
		return settingRepository.save(setting);
	}
	
	public List<Setting> findAll() {
		return settingRepository.findAll();
	}
	
	public Setting findOne(Long id) {
		return settingRepository.findOne(id);
	}
	
	@CacheEvict(value={"settingsImportHeader",  "settingsImportSQLInsert", "settingsImportSQLUpdate", "settingsImportName", 
			"settingsImportColumnNotEmpty", "settingsImportServerName", "settingsSQLSelect"}, allEntries = true)
	public void delete(Long id) {
		Setting setting = settingRepository.findOne(id);
		if (setting != null) {
			settingRepository.delete(id);
		}
	}
	
	@CacheEvict(value={"settingsImportHeader",  "settingsImportSQLInsert", "settingsImportSQLUpdate", "settingsImportName", 
			"settingsImportColumnNotEmpty", "settingsImportServerName", "settingsSQLSelect"}, allEntries = true)
	public void delete(String key) {
		List<Setting> settings = settingRepository.findByTemplateName(key);
		if (settings != null) {
			settingRepository.delete(settings.get(0));
		}
	}
	
	@Cacheable(value="settingsImportHeader")
	public String getHeader(Long id) {
		Setting setting =settingRepository.findOne(id);
		return setting==null? null : setting.getHeader();
	}
	
	@Cacheable(value="settingsImportSQLInsert")
	public String getSQLInsert(Long id) {
		Setting setting =settingRepository.findOne(id);
		return setting==null? null : setting.getSqlInsert();
	}
	
	@Cacheable(value="settingsImportSQLUpdate")
	public String getSQLUpdate(Long id) {
		Setting setting =settingRepository.findOne(id);
		return setting==null? null : setting.getSqlUpdate();
	}
	
	@Cacheable(value="settingsImportName")
	public String getName(Long id) {
		Setting setting =settingRepository.findOne(id);
		return setting==null? null : setting.getTemplateName();
	}
	
	@Cacheable(value="settingsImportColumnNotEmpty")
	public String getColumnNotEmpty(Long id) {
		Setting setting =settingRepository.findOne(id);
		return setting==null? null : setting.getColumnsNotEmpty();
	}
	
	@Cacheable(value="settingsImportServerName")
	public String getServerName(Long id) {
		Setting setting =settingRepository.findOne(id);
		return setting==null? null : setting.getDb_execute();
	}
	
	@Cacheable(value="settingsSQLSelect")
	public String getSQLSelect(Long id) {
		Setting setting =settingRepository.findOne(id);
		return setting==null? null : setting.getSqlSelect();
	}
}
