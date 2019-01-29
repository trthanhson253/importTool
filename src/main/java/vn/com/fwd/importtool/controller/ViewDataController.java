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
import org.springframework.web.bind.annotation.*;

import vn.com.fwd.importtool.dto.SearchDataDTO;
import vn.com.fwd.importtool.model.Setting;
import vn.com.fwd.importtool.repository.SettingRepository;
import vn.com.fwd.importtool.service.ViewDataService;

@Controller
public class ViewDataController {
	@Autowired
	private SettingRepository settingRepository;
	@Autowired
	private ViewDataService viewDataService;
	
	@GetMapping("/view-data")
	public String viewData(Model model) {
    	// get setting by role. if role admin => get all
    	List<Setting> lstSetting = new ArrayList<>();
    	Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority auth : authorities) {
            String roleName = auth.getAuthority();
            if ("ROLE_ADMIN".equals(roleName)) {
            	lstSetting = new ArrayList<>();
            	lstSetting = settingRepository.findAll();
            	break;
            } else {
            	// find setting by role
            	lstSetting.addAll(settingRepository.findByTemplateName(roleName));
            }
        }
    	
    	model.addAttribute("templates", lstSetting);
    	SearchDataDTO searchData = new SearchDataDTO();
    	if (!lstSetting.isEmpty()) {
    		searchData.setTemplateId(lstSetting.get(0).getId());
    	} else {
    		throw new AccessDeniedException("Bạn chưa có quyền vào trang này !");
    	}
    	model.addAttribute("searchData", searchData);
    	
		return "viewData/viewData";
	}
	
	@PostMapping("/view-data/searchData")
	public String searchData(SearchDataDTO searchData, Model model) {
		try {
			List<Object[]> lstData = viewDataService.getDataForViewData(searchData);
			model.addAttribute("searchData", searchData);
			model.addAttribute("lstData", lstData);
			
			// get setting by role. if role admin => get all
	    	List<Setting> lstSetting = new ArrayList<>();
	    	Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	        for (GrantedAuthority auth : authorities) {
	            String roleName = auth.getAuthority();
	            if ("ROLE_ADMIN".equals(roleName)) {
	            	lstSetting = new ArrayList<>();
	            	lstSetting = settingRepository.findAll();
	            	break;
	            } else {
	            	// find setting by role
	            	lstSetting.addAll(settingRepository.findByTemplateName(roleName));
	            }
	        }
	    	model.addAttribute("templates", lstSetting);
	    	model.addAttribute("templateId", String.valueOf(searchData.getTemplateId().intValue()));
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "viewData/viewData";
	}
}
