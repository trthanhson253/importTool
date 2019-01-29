package vn.com.fwd.importtool.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import vn.com.fwd.importtool.model.LogImport;
import vn.com.fwd.importtool.model.Setting;
import vn.com.fwd.importtool.repository.LogImportRepository;
import vn.com.fwd.importtool.repository.SettingRepository;

@Controller
public class HomeController {
	
	@Autowired
	private SettingRepository settingRepository;
	
	@Autowired
	private LogImportRepository logImportRepository;
	
	@GetMapping("/")
    public String getHomePage() {
        return "redirect:/import";
    }

    @PostMapping("/")
    public String postHomePage() {
        return "redirect:/import";
    }


    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }


    @GetMapping("/import")
    public String getReportPage(Model model) {
    	boolean roleAdmin = false; 
    	// get setting by role. if role admin => get all
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
    	if (!lstSetting.isEmpty()) {
    		model.addAttribute("templateId", lstSetting.get(0).getId());
    	} else {
    		model.addAttribute("templateId", "");
    		throw new AccessDeniedException("Bạn chưa có quyền vào trang này !");
    	}
    	// get list log of template 1
    	// find history by user login. if admin => show all log
    	List<LogImport> logImports = new ArrayList<>();
    	String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (roleAdmin) {
        	logImports = logImportRepository.findByTemplateId(lstSetting.get(0).getId());
        } else {
        	if (!lstSetting.isEmpty()) {
        		logImports = logImportRepository.findByTemplateIdAndUserName(lstSetting.get(0).getId(), userName);
        	} else {
        		logImports = new ArrayList<>();
        	}
        }
    	model.addAttribute("logs", logImports);
        return "import";
    }
    
}
