package vn.com.fwd.importtool.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import vn.com.fwd.importtool.util.SpringPersistentUtils;


/**
 * @author VNUSER10
 *
 */
@Service
public class ODSHelperService {
	
	private Logger log = Logger.getLogger(ODSHelperService.class);
	
    private NamedParameterJdbcTemplate template;
    private SettingService settingService;

    @Autowired
    public ODSHelperService(SettingService settingService, @Qualifier("odsDataSourceProperties") DataSourceProperties dataSourceProperties) {
        this.template = new NamedParameterJdbcTemplate(SpringPersistentUtils.getDataSource(dataSourceProperties));
        this.settingService = settingService;
    }
    
    /**
     * Method save data to database 
     * 
     * @param templateId
     * @param mapParam
     * @throws Exception
     */
    public void saveDataToDB(Long templateId, Map<String, Object> mapParam) throws Exception {
    	try {
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
    		// get Data
    		List<Map<String, Object>> lstResult = template.queryForList(sql, param);
    		return lstResult;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
    }
}
