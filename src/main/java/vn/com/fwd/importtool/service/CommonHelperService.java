package vn.com.fwd.importtool.service;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class CommonHelperService {
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private SettingService settingService;
	
	public void saveDataToDB(Long templateId, Map<String, Object> mapParam) throws Exception {
    	try {
    		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
			String sqlUpdate = settingService.getSQLUpdate(templateId);
			if (template.update(sqlUpdate, mapParam) == 0) {
				String sqlInsert = settingService.getSQLInsert(templateId);
				template.update(sqlInsert, mapParam);
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		}
    }
	
	public List<Map<String, Object>> getData(String sql, Map<String, Object> param) throws Exception {
    	try {
    		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
    		// get Data
    		List<Map<String, Object>> lstResult = template.queryForList(sql, param);
    		return lstResult;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
    }
	
}
