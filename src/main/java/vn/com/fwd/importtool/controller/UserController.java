package vn.com.fwd.importtool.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.com.fwd.importtool.model.Role;
import vn.com.fwd.importtool.model.User;
import vn.com.fwd.importtool.repository.RoleRepository;
import vn.com.fwd.importtool.service.UserService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@ModelAttribute("roles")
    public List<Role> getRoles() {
//		Role role = roleRepository.findOne("ROLE_ADMIN");
		List<Role> lstReult = roleRepository.findAll();
//		lstReult.remove(role);
        return lstReult;
    }
	
	@GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }
	
	@PostMapping("/users")
    public String updateUser(@ModelAttribute User user, @RequestParam(required = false) String agentCodes) {
        user.setEnabled(true);
        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/users/new")
    public String createUser(@ModelAttribute User user, Model model) {
        return "user/detail";
    }


    @GetMapping(value = "/users", params = "username")
    public String getUser(@RequestParam String username, Model model) {
        User user = userService.fetch(username);
        if (user == null) {
            user = new User();
        }
        model.addAttribute("user", user);
        return "user/detail";
    }


    @DeleteMapping("/users")
    public ResponseEntity removeUser(@RequestParam(required = false) String username) {
        if (StringUtils.isNotEmpty(username)) {
            userService.delete(username);
        }
        return ResponseEntity.ok().build();
    }
}
