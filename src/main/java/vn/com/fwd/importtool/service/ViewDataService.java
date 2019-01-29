package vn.com.fwd.importtool.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.com.fwd.importtool.constants.ImportConstants;
import vn.com.fwd.importtool.dto.SearchDataDTO;

@Service
@Slf4j
public class ViewDataService {
	@Autowired
    private SettingService settingService;
    @Autowired
    private ODSHelperService odsHelper;
    @Autowired
    private CommonHelperService commonHelper;
    
    public List<Object[]> getDataForViewData_Old(SearchDataDTO searchParam) throws Exception {
    	try {
			List<Object[]> lstResult = new ArrayList<>();
			
			// get sql insert
			String sqlInsert = settingService.getSQLInsert(searchParam.getTemplateId());
			//extract table name, field select, condition
			String[] strPartSql = sqlInsert.toUpperCase().split("VALUES");
			
			// get table name and select field
			String[] strPart1 = strPartSql[0].split("\\(");
			String tableName = strPart1[0].replace("INSERT ", "").replace("INTO", "").trim();
			String selectField = strPart1[1].replace(")", "").trim();
			String[] lstField = selectField.split(",");
			
			// get sql final
			String sqlFinal = "SELECT " + selectField + " FROM " + tableName;
			if (searchParam.getFromDate() != null || searchParam.getToDate() != null) {
				// get index of "GETDATE()"
				String strPartValues = strPartSql[1].trim();
				List<String> lstVals = new ArrayList<>();
				List<Integer> lstIndex = new ArrayList<>();
				int numberOfOpen = 0;
				String strValTemp = "";
				for (int i = 0; i< strPartValues.length(); i++) {
					String charTemp = String.valueOf(strPartValues.charAt(i));
					if (i != 0) {
						if (charTemp.equals(",") && numberOfOpen == 0) {
							lstVals.add(strValTemp);
							strValTemp = "";
						} else if (charTemp.equals("(")) {
							strValTemp += charTemp;
							numberOfOpen ++;
						} else if (charTemp.equals(")")) {
							strValTemp += charTemp;
							numberOfOpen --;
						} else {
							strValTemp += charTemp;
						}
					}
				}
				if (! strValTemp.isEmpty()) {
					lstVals.add(strValTemp);
					strValTemp = "";
				}
				for (int indexOfVal = 0; indexOfVal < lstVals.size(); indexOfVal ++ ) {
					 String strValue = lstVals.get(indexOfVal); 
					if (strValue.contains("GETDATE")) {
						lstIndex.add(indexOfVal);
					}
				}
				// generate condition where
				String where = "";
				if (lstIndex.size() > 0) {
					for (int index : lstIndex) {
						String strWherePartTmp = "";
						if (searchParam.getFromDate() != null && searchParam.getToDate() != null) {
							strWherePartTmp = " (" + lstField[index] + " BETWEEN :STARTDATE AND :ENDDATE) ";
						} else if (searchParam.getFromDate() != null) {
							strWherePartTmp = lstField[index] + " >= :STARTDATE ";
						} else {
							strWherePartTmp = lstField[index] + " <= :ENDDATE ";
						}
						if (where.isEmpty()) {
							where = strWherePartTmp;
						} else {
							where += " OR " + strWherePartTmp;
						}
					}
				}
				if (!where.isEmpty()) {
					where = "WHERE " + where;
				}
				
				sqlFinal += " " + where;
			}
			
			// set object param
			Map<String, Object> param = new HashMap<>();
			param.put("STARTDATE", searchParam.getFromDate());
			param.put("ENDDATE", searchParam.getToDate());
			
			// list result tempt;
			List<Map<String, Object>> lstResultTempt;
			
			String serverName = settingService.getServerName(searchParam.getTemplateId());
			if (ImportConstants.DBNames.Server24.equals(serverName)) {
				lstResultTempt = odsHelper.getData(sqlFinal, param);
			} else {
				lstResultTempt = commonHelper.getData(sqlFinal, param);
			}
			
			if (lstResultTempt != null && lstResultTempt.size() > 0) {
				// header :
				Map<String, Object> row0 = lstResultTempt.get(0);
				int rowSize = row0.size();
				Object[] header = new Object[rowSize];
				int indexHeader = 0;
				for(Map.Entry<String, Object> entry : row0.entrySet()) {
				    String key = entry.getKey();
				    header[indexHeader] = key;
				    indexHeader ++;
				}
				lstResult.add(header);
				
				for (Map<String, Object> row : lstResultTempt) {
					Object[] rowData = new Object[rowSize];
					for (int columnIndex = 0; columnIndex < rowSize; columnIndex ++) {
						rowData[columnIndex] = row.get(header[columnIndex]);
					}
					lstResult.add(rowData);
				}
			}
			
			return lstResult;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
    }

    public List<Object[]> getDataForViewData(SearchDataDTO searchParam) throws Exception {
    	try {
			List<Object[]> lstResult = new ArrayList<>();
			String sqlFinal = settingService.getSQLSelect(searchParam.getTemplateId());
			if (sqlFinal == null || sqlFinal.isEmpty()) {
				return lstResult;
			}
			// list result tempt;
			List<Map<String, Object>> lstResultTempt;
			// set object param
			DateFormat df = new SimpleDateFormat("yyyyMMdd");
			Map<String, Object> param = new HashMap<>();
			if (searchParam.getFromDate() == null) {
				param.put("STARTDATE", "10000101");
			} else {
				param.put("STARTDATE", df.format(searchParam.getFromDate()));
			}
			if (searchParam.getToDate() == null) {
				param.put("ENDDATE", "99991231");
			} else {
				param.put("ENDDATE", df.format(searchParam.getToDate()));
			}
			
			String serverName = settingService.getServerName(searchParam.getTemplateId());
			if (ImportConstants.DBNames.Server24.equals(serverName)) {
				lstResultTempt = odsHelper.getData(sqlFinal, param);
			} else {
				lstResultTempt = commonHelper.getData(sqlFinal, param);
			}
			
			if (lstResultTempt != null && lstResultTempt.size() > 0) {
				// header :
				Map<String, Object> row0 = lstResultTempt.get(0);
				int rowSize = row0.size();
				Object[] header = new Object[rowSize];
				int indexHeader = 0;
				for(Map.Entry<String, Object> entry : row0.entrySet()) {
				    String key = entry.getKey();
				    header[indexHeader] = key;
				    indexHeader ++;
				}
				lstResult.add(header);
				
				for (Map<String, Object> row : lstResultTempt) {
					Object[] rowData = new Object[rowSize];
					for (int columnIndex = 0; columnIndex < rowSize; columnIndex ++) {
						rowData[columnIndex] = row.get(header[columnIndex]);
					}
					lstResult.add(rowData);
				}
			}
			
			return lstResult;
    	} catch (Exception e) {
    		log.error(e.getMessage());
			throw e;
    	}
    }
}
