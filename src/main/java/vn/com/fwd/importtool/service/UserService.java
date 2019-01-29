package vn.com.fwd.importtool.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.com.fwd.importtool.model.User;
import vn.com.fwd.importtool.repository.UserRepository;

@Service
@Transactional
public class UserService implements UserDetailsService {

	private UserRepository userRepository;
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Cannot find username: " + username);
		}
		return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .authorities(retrieveAuthorities(username))
                .build();
	}
	
	public List<? extends GrantedAuthority> retrieveAuthorities(String username) {
        List<SimpleGrantedAuthority> result = new ArrayList<>();
        User user = userRepository.findByUsername(username);
        List<String> roles = user.getRoles();
        if (roles != null) {
        	for (String role : roles) {
        		result.add(new SimpleGrantedAuthority(role));
        	}
        }
        return result;
    }
	
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	public void save(User user) {
		userRepository.save(user);
	}
	
	public User fetch(String username) {
		return userRepository.findOne(username);
	}
	
	public void delete(String userName) {
		userRepository.delete(userName);
	}

}
