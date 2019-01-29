package vn.com.fwd.importtool.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.com.fwd.importtool.constants.ImportConstants;
import vn.com.fwd.importtool.model.Role;
import vn.com.fwd.importtool.model.Setting;
import vn.com.fwd.importtool.repository.RoleRepository;
import vn.com.fwd.importtool.service.SettingService;
import vn.com.fwd.importtool.util.ImportUtils;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class ConfigController {

    @Autowired
    private SettingService settingService;
    @Autowired
    private RoleRepository roleRepository;

    @ModelAttribute("lstDBName")
    public List<String> getAllDBName() {
    	return ImportConstants.DBNames.getListDBName();
    }
    
    @GetMapping("/configs")
    public String getAllConfig(Model model) {
        model.addAttribute("configs", settingService.findAll());
        return "config/list";
    }

    @GetMapping("/configs/new")
    public String getNewConfig(@ModelAttribute("form") Setting form) {
        return "config/detail";
    }

    @GetMapping(value = "/configs/{id}")
    public String getConfig(@PathVariable Long id, Model model) {
        model.addAttribute("form", settingService.findOne(id));
        return "config/detail";
    }

    @PostMapping("/configs")
    public String createNewConfig(@ModelAttribute("form") Setting form, @RequestParam("file") MultipartFile file) throws Exception {
        // save new role by new configuration
        long idSetting = form.getId();
        String roleName = form.getTemplateName();
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (idSetting > 0) {
        	// check if template name is changing => delete role by template name old 
        	Setting settingOld = settingService.findOne(idSetting);
        	if (roleName != null && !roleName.equals(settingOld.getTemplateName())) {
        		// delete role by template name old
        		try {
        			roleRepository.delete(settingOld.getTemplateName());
				} catch (Exception e) {}
        	}
        	// 
        	if(filename.isEmpty()) {
        		form.setExtensionFile(settingOld.getExtensionFile());
    	        form.setFisTemplate(settingOld.getFisTemplate());
        	}
        }
        Role role = roleRepository.findOne(roleName);
        if (role == null) {
        	role = new Role();
        	role.setName(form.getTemplateName());
        	role.setDescription("Role import " + form.getTemplateName());
        	roleRepository.save(role);
        }
        // save new configuration (Setting)
        if(!filename.isEmpty()) {
        	byte[] fis = file.getBytes();
	        form.setExtensionFile(ImportUtils.getExtensionFile(filename));
	        form.setFisTemplate(fis);
        }
        settingService.save(form);
        
        return "redirect:/configs";
    }

    @DeleteMapping("/configs")
    public ResponseEntity deleteConfig(@RequestParam(required = false) String key) {
        if (key != null) {
        	settingService.delete(key);
        	// delete Role
        	try {
        		roleRepository.delete(key);
			} catch (Exception e) {}
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/configs/checkExistFileTemplate")
    @ResponseBody
    public ResponseEntity<Boolean> checkExistTemplateFile(Long templateId)  throws Exception {
    	try {
    		if (templateId < 1) {
    			return ResponseEntity.ok(Boolean.FALSE);
    		}
    		
    		Setting setting = settingService.findOne(templateId);
    		if (setting.getFisTemplate() != null) {
    			return ResponseEntity.ok(Boolean.TRUE);
    		} else {
    			return ResponseEntity.ok(Boolean.FALSE);
    		}
		} catch (Exception e) {
			throw e;
		}
    }

}
