package vn.com.fwd.importtool.service.impl;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import vn.com.fwd.importtool.constants.ImportConstants;
import vn.com.fwd.importtool.model.LogImport;
import vn.com.fwd.importtool.model.Setting;
import vn.com.fwd.importtool.repository.LogImportRepository;
import vn.com.fwd.importtool.service.*;
import vn.com.fwd.importtool.util.ExcelReportUtil;
import vn.com.fwd.importtool.util.ImportUtils;

@Slf4j
@Service
public class ImportServiceImpl implements ImportService {
	
	@Value("${base.folder.temptFolfer}")
	private String folderTempt;
	
	@Autowired
	private ODSHelperService osdHeperService;
	@Autowired
	private SettingService settingService;
	@Autowired
	private ExcelReportUtil excelReportUtil;
	@Autowired
	private CommonHelperService commonHelper;
	
	@Autowired
	LogImportRepository logImportRepository;
	
    public File getFileUpload(Long logId) throws Exception {
    	try {
    		// get file from database
        	LogImport logImport = logImportRepository.findOne(logId);
        	if (logImport.getByteFileImport() == null) {
        		return null;
        	}
        	
        	File tempt = new File(folderTempt);
    		if (!tempt.exists()) {
    			tempt.mkdirs();
    		}
        	
        	String filePathTempt = folderTempt + File.separator + logImport.getFileName();
        	OutputStream targetFile=  new FileOutputStream(filePathTempt);
            targetFile.write(logImport.getByteFileImport());
            targetFile.close();
        	
        	return new File(filePathTempt);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
    }
    
    public File getFileTemplate(Long templateId) throws Exception {
    	try {
        	// get file from database
        	Setting setting = settingService.findOne(templateId);
        	if (setting.getFisTemplate() == null) {
        		return null;
        	}
        	
        	File tempt = new File(folderTempt);
    		if (!tempt.exists()) {
    			tempt.mkdirs();
    		}
    		
        	String filePathTempt = folderTempt + File.separator + setting.getTemplateName() + "." + setting.getExtensionFile();
        	OutputStream targetFile=  new FileOutputStream(filePathTempt);
            targetFile.write(setting.getFisTemplate());
            targetFile.close();
        	
        	return new File(filePathTempt);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
    }
    
    public void importData(Long templateId, MultipartFile file) throws Exception {
    	try {
    		List<Map<String, Object>> lstReadExcel = excelReportUtil.readFileExcel(templateId, file);
    		// validate file
    		if(excelReportUtil.validateExcel(templateId, file, lstReadExcel)) {
    			// read file
        		if (lstReadExcel != null && !lstReadExcel.isEmpty()) {
        			// import database
        			String serverName = settingService.getServerName(templateId);
        			boolean server24 = false;
        			if (ImportConstants.DBNames.Server24.equals(serverName)) {
        				server24 = true;
        			}
        			int i = 0;
        			String userName = ImportUtils.getLoginUser().getUsername();
        			for (Map<String, Object> mapParam : lstReadExcel) {
        				// add user login
        				mapParam.put("USERNAME", userName);
        				i ++;
        				try {
        					if (server24) {
        						osdHeperService.saveDataToDB(templateId, mapParam);
        					} else {
        						// import by common
        						commonHelper.saveDataToDB(templateId, mapParam);
        					}
        				} catch (Exception e) {
        					log.error(e.getMessage());
        					log.error("Error import DB in line " + i);
        					LogImport logImport = new LogImport();
                			logImport.setAction(ImportConstants.INSERT);
                			logImport.setCreateTime(new Date());
                			logImport.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
                			logImport.setResult(ImportConstants.FAILURE);
                			logImport.setTemplateId(templateId);
                			logImport.setUserName(userName);
                			logImport.setByteFileImport(file.getBytes());
                			logImportRepository.save(logImport);
        					throw new Exception (e + "Error import DB in line " + i);
        				}
        			}
        			// save log
        			LogImport logImport = new LogImport();
        			logImport.setAction(ImportConstants.INSERT);
        			logImport.setCreateTime(new Date());
//        			File fileTmp = new File(filePath);
        			// save new configuration (Setting)
        	        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        			logImport.setFileName(filename);
        			logImport.setByteFileImport(file.getBytes());
        			logImport.setResult(ImportConstants.SUCCESS);
        			logImport.setTemplateId(templateId);
        			logImport.setUserName(userName);
        			logImportRepository.save(logImport);
        			
        		}
    		} else {
    			log.error("The format of File Excel not true !!!");
    			throw new Exception("The format of File Excel not true !!!");
    		}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw e;
		}
    }
    
    public List<Object[]> readExcel4LoadData(MultipartFile fileImport) throws Exception {
    	Workbook workbook = null;
    	List<Object[]> lstResutl = new ArrayList<>();
    	try {
    		DataFormatter formatter = new DataFormatter();
    		InputStream is = fileImport.getInputStream();
    		workbook = WorkbookFactory.create(is);
    		Sheet sheet0 = workbook.getSheetAt(0);
    		for (Row currentRow : sheet0) {
    			int size = currentRow.getLastCellNum();
				Object[] rowData = new Object[size];
				for (int cellIndex = 0; cellIndex < size; cellIndex++) {
					Cell currentCell = currentRow.getCell(cellIndex);
					String strValueCellDefault = formatter.formatCellValue(currentCell);
					rowData[cellIndex] = strValueCellDefault;
				}
				lstResutl.add(rowData);
    		}
    		
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		} finally {
			if (workbook!=null) {workbook.close(); workbook =null;}
		}
    	
    	return lstResutl;
    }
}
