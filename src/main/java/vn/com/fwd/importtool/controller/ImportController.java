package vn.com.fwd.importtool.controller;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import vn.com.fwd.importtool.model.LogImport;
import vn.com.fwd.importtool.model.Setting;
import vn.com.fwd.importtool.repository.LogImportRepository;
import vn.com.fwd.importtool.repository.SettingRepository;
import vn.com.fwd.importtool.service.ImportService;
import vn.com.fwd.importtool.service.SettingService;
import vn.com.fwd.importtool.util.ExcelReportUtil;

@Slf4j
@Controller
public class ImportController {
	@Autowired
	private SettingRepository settingRepository;
	@Autowired
	private LogImportRepository logImportRepository;
	@Autowired
	private ImportService importService;
	@Autowired
	private SettingService settingService;
	@Autowired
	private ExcelReportUtil excelReportUtil;
	
	@PostMapping("/import")
	public String importData(@RequestParam("file") MultipartFile file, Model model, Long templateId ) throws Exception{
		importService.importData(templateId, file);
		
		boolean roleAdmin = false;
		List<Setting> lstSetting = new ArrayList<>();
    	Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority auth : authorities) {
            String roleName = auth.getAuthority();
            if ("ROLE_ADMIN".equals(roleName)) {
            	roleAdmin = true;
            	lstSetting = new ArrayList<>();
            	lstSetting = settingRepository.findAll();
            	break;
            } else {
            	// find setting by role
            	lstSetting.addAll(settingRepository.findByTemplateName(roleName));
            }
        }
    	model.addAttribute("templates", lstSetting);
    	model.addAttribute("templateId", String.valueOf(templateId.intValue()));
    	List<LogImport> logImports = new ArrayList<>();
    	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (roleAdmin) {
        	logImports = logImportRepository.findByTemplateId(templateId);
        } else {
        	logImports = logImportRepository.findByTemplateIdAndUserName(templateId, userName);
        }
    	model.addAttribute("logs", logImports);
		return "import";
	}
	
	@GetMapping("/import/checkExistFileImport")
	@ResponseBody
	public ResponseEntity<Boolean> checkExistFileImport(Long logId) throws Exception{
		LogImport logImport = logImportRepository.findOne(logId);
    	if (logImport.getByteFileImport() != null) {
			return ResponseEntity.ok(Boolean.TRUE);
		}
		return ResponseEntity.ok(Boolean.FALSE);
	}
	
	@GetMapping("/dowload/{id}")
	public void downloadFileImport(@PathVariable Long id, HttpServletResponse response) throws Exception {
		
		OutputStream out = response.getOutputStream();
		File fileImport = importService.getFileUpload(id);
        if (fileImport == null) {
        	response.setContentType("application/x-download");
            response.setHeader("Content-disposition", "form-data;");
        	return;
        }
        response.setContentType("application/x-download");
        response.setHeader("Content-disposition", "attachment; filename=" + fileImport.getName());
        if (fileImport.exists()) {
            InputStream in = new FileInputStream(fileImport);
            int c = 0;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            out.flush();
            out.close();
            in.close();
            fileImport.delete();
        }
//        Resource file = importService.getResourceFileUpload(id, fileName);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
	
	@GetMapping("/import/history")
	@ResponseBody
	public List<LogImport> getListLogByTemplateId(Long templateId) throws Exception {
		List<LogImport> lstLogs = new ArrayList<>();
		try {
			// get list log of template 1
	    	// find history by user login. if admin => show all log
	    	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
	    	lstLogs = logImportRepository.findByTemplateIdAndUserName(templateId, userName);
	    	Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	        for (GrantedAuthority auth : authorities) {
	            String roleName = auth.getAuthority();
	            if ("ROLE_ADMIN".equals(roleName)) {
	            	lstLogs = logImportRepository.findByTemplateId(templateId);
	            	break;
	            }
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw e;
		}
		return lstLogs;
	}
	
	@GetMapping("/import/checkExistFileTemplate")
	@ResponseBody
	public ResponseEntity<Boolean> checkExistFileTemplate(Long templateId) throws Exception{
		Setting setting = settingService.findOne(templateId);
    	if (setting.getFisTemplate() != null) {
			return ResponseEntity.ok(Boolean.TRUE);
		}
		return ResponseEntity.ok(Boolean.FALSE);
	}
	
	@GetMapping("/import/download/template/{templateId}")
	public void downloadFileTemplate(@PathVariable Long templateId, HttpServletResponse response) throws Exception {
		
		OutputStream out = response.getOutputStream();
        File fileTmp = importService.getFileTemplate(templateId);
        if (fileTmp == null) {
        	response.setContentType("application/x-download");
            response.setHeader("Content-disposition", "form-data;");
        	return;
        }
        response.setContentType("application/x-download");
        response.setHeader("Content-disposition", "attachment; filename=" + fileTmp.getName());
        if (fileTmp.exists()) {
            InputStream in = new FileInputStream(fileTmp);
            int c = 0;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            out.flush();
            out.close();
            in.close();
            fileTmp.delete();
        }
		
//        Resource file = importService.getResourceFileTemplate(templateId);
//        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
//                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
	
	@PostMapping("/import/loadData")
	@ResponseBody
	public List<Object[]> changeFileImport(Long templateId, @RequestParam("file") MultipartFile fileImport) throws Exception {
		try {
			List<Object[]> lstResult = new ArrayList<>();
			// read excel
			lstResult = importService.readExcel4LoadData(fileImport);
			// return list object
			return lstResult;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
	
	@PostMapping("/import/validateExcel")
	@ResponseBody
	public ResponseEntity<?> validateExcel(Long templateId, @RequestParam("file") MultipartFile fileImport) throws Exception {
		try {
			// return list object
			List<Map<String, Object>> lstReadExcel = excelReportUtil.readFileExcel(templateId, fileImport);
			Map<String, Object> response = new HashMap<>();
			response.put("message", excelReportUtil.validateExcelBeforSubmit(templateId, fileImport, lstReadExcel));
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
	}
}
