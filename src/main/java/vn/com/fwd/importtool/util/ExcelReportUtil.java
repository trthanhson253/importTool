package vn.com.fwd.importtool.util;

import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.com.fwd.importtool.service.SettingService;

@Service
public class ExcelReportUtil {
	private Logger log = Logger.getLogger(ExcelReportUtil.class);
	
	@Autowired
	private SettingService settingService;
	
	public boolean validateExcel(Long templateId, MultipartFile fileExcel, List<Map<String, Object>> lstExcell) throws Exception {
		Workbook workbook = null;
		try {
			String header = settingService.getHeader(templateId);
			if (header != null) {
				String[] headers = header.split(";");
				InputStream is = fileExcel.getInputStream();
				
//				workbook = new XSSFWorkbook(is);
				workbook = WorkbookFactory.create(is);
				
				// check header not empty, null
				Sheet sheet0 = workbook.getSheetAt(0);
				Row row0 = sheet0.getRow(0);
				int lengthHeader = headers.length;
				for (int count = 0; count < lengthHeader; count ++) {
					Cell cell = row0.getCell(count);
					DataFormatter formatter = new DataFormatter();
					String strValue = formatter.formatCellValue(cell);
					if (strValue == null || strValue.isEmpty()) {
						return false;
					}
				}
				
				// check columns not empty
//				lstExcell = readFileExcel(templateId, fileExcel);
				String columnsNotEmpty = settingService.getColumnNotEmpty(templateId);
				if (columnsNotEmpty != null && !columnsNotEmpty.isEmpty()) {
					String[] columnNotEmptys = columnsNotEmpty.split(";");
					for (String columnIndex : columnNotEmptys) {
						int columnNumber = -1;
						try {
							columnNumber = Integer.parseInt(columnIndex);
						} catch (Exception e) {
						}
						if (columnNumber > -1) {
							for (Map<String, Object> row : lstExcell) {
								if (row.get(headers[columnNumber]) == null || String.valueOf(row.get(headers[columnNumber])).isEmpty()) {
									return false;
								}
							}
						}
					}
					
				}
				
				return true;
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			if (workbook!=null) {workbook.close(); workbook =null;}
		}
		return false;
	}
	
	public List<Map<String, Object>> readFileExcel(Long templateId, MultipartFile file) throws Exception {
		Workbook workbook = null;
		try {
			List<Map<String, Object>> lstResult = new ArrayList<>();
			
			String header = settingService.getHeader(templateId);
			DataFormatter formatter = new DataFormatter();
			if (header != null) {
				String[] headers = header.split(";");
				int headersLeng = headers.length;
				InputStream is = file.getInputStream();
				
//				workbook = new XSSFWorkbook(new File(filePathTmp));
				workbook = WorkbookFactory.create(is);
				
				Sheet sheet0 = workbook.getSheetAt(0);
				for (Row currentRow : sheet0) {
					if (currentRow.getRowNum() == 0) {
						continue;
					}
					Map<String, Object> rowData = new HashMap<>();
//					int cellIndex = 0;
					int size = currentRow.getLastCellNum();
					for (int cellIndex = 0; cellIndex < size; cellIndex++) {
						Cell currentCell = currentRow.getCell(cellIndex);
						if (currentCell != null) {
							if (cellIndex >= headersLeng) {
								break;
							}
							try {
								if (DateUtil.isCellDateFormatted(currentCell) ) {
									rowData.put(headers[cellIndex], currentCell.getDateCellValue());
									continue;
								}
							} catch (Exception e) {}
			            	switch (currentCell.getCellTypeEnum()) {
			            		case STRING:
			            			String strValueCell = currentCell.getStringCellValue();
			            			strValueCell = strValueCell.trim();
			            			rowData.put(headers[cellIndex], strValueCell);
			            			break;
			            		case BOOLEAN:
			            			rowData.put(headers[cellIndex], currentCell.getBooleanCellValue());
				                    break;
			            		default:
			            			String strValueCellDefault = formatter.formatCellValue(currentCell);
			            			strValueCellDefault = strValueCellDefault.trim();
			            			rowData.put(headers[cellIndex], strValueCellDefault);
				                    break;
				            }
						}
					}
					//
//					boolean addrow = true;
//					for (String strColumn: headers) {
//						if (rowData.get(strColumn) == null || String.valueOf(rowData.get(strColumn)).isEmpty()){
//							addrow = false;
//							break;
//						}
//					}
//					if (addrow) {
						lstResult.add(rowData);
//					}
	            }
			}
			return lstResult;
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			if (workbook!=null) {workbook.close(); workbook =null;}
		}
	}
	
	// validate excel
	public String validateExcelBeforSubmit(Long templateId, MultipartFile fileExcel, List<Map<String, Object>> lstExcell) throws Exception {
		Workbook workbook = null;
		try {
			String header = settingService.getHeader(templateId);
			if (header != null) {
				String[] headers = header.split(";");
				InputStream is = fileExcel.getInputStream();
				
//				workbook = new XSSFWorkbook(is);
				workbook = WorkbookFactory.create(is);
				
				// check header not empty, null
				Sheet sheet0 = workbook.getSheetAt(0);
				Row row0 = sheet0.getRow(0);
				int lengthHeader = headers.length;
				int sizeHeaderExcel = row0.getLastCellNum();
				if (sizeHeaderExcel != lengthHeader) {
					return "File Excel của bạn không đúng định dạng ! \nVui lòng download Template mẫu và xem lại !";
				}
				
				for (int count = 0; count < lengthHeader; count ++) {
					Cell cell = row0.getCell(count);
					DataFormatter formatter = new DataFormatter();
					String strValue = formatter.formatCellValue(cell);
					if (strValue == null || strValue.isEmpty()) {
						return "File Excel của bạn không đúng định dạng ! \nHeader không được trống";
					}
				}
				
				// check columns not empty
//				lstExcell = readFileExcel(templateId, fileExcel);
				String columnsNotEmpty = settingService.getColumnNotEmpty(templateId);
				if (columnsNotEmpty != null && !columnsNotEmpty.isEmpty()) {
					String[] columnNotEmptys = columnsNotEmpty.split(";");
					int rowIndex = 0;
					String messageReturn = "Các dòng thứ";
					String rowStatus = "";
					boolean blnFormatTrue = true;
					for (Map<String, Object> row : lstExcell) {
						rowIndex ++;
						for (String columnIndex : columnNotEmptys) {
							int columnNumber = -1;
							try {
								columnNumber = Integer.parseInt(columnIndex);
							} catch (Exception e) {
							}
							if (columnNumber > -1) {
								if (row.get(headers[columnNumber]) == null || String.valueOf(row.get(headers[columnNumber])).isEmpty()) {
									if (rowStatus.isEmpty()) {
										rowStatus += " " + rowIndex;
									} else {
										rowStatus += ", " + rowIndex;
									}
									blnFormatTrue = false;
									break;
								}
							}
						}
					}
					if (!blnFormatTrue) {
						messageReturn += rowStatus + " có chứa những field không được trống, \nVui lòng kiểm tra lại !";
						return messageReturn;
					}
					
				}
				
				return "TRUE";
			} else {
				return "Configuration header của bạn không có, \nVui lòng kiểm tra lại configuration !";
			}
		} catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			if (workbook!=null) {workbook.close(); workbook =null;}
		}
	}
}
