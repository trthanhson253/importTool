package vn.com.fwd.importtool.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.UserDetailsServiceLdapAuthoritiesPopulator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

import vn.com.fwd.importtool.service.UserService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SercurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/css**", "/css/**", "/fonts**", "/fonts/**", "/img**", "/img/**", "/js**", "/js/**", "/vendor**", "/vendor/**").permitAll()
			.antMatchers("/import**", "/import/**").authenticated()
			.anyRequest().authenticated()
			.and()
            .formLogin()
            .loginPage("/login").permitAll()
            .successForwardUrl("/")
            .and()
            .httpBasic()
            .and()
            .csrf().disable();
	}
	
	@Bean
	@ConfigurationProperties("staff.ldap")
	public LdapContextSource staffLdapContextSource() {
		return new LdapContextSource();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth, UserService userService) throws Exception {
		LdapContextSource ldapContextSource = staffLdapContextSource();
		FilterBasedLdapUserSearch staffUserSearch = new FilterBasedLdapUserSearch("", "(sAMAccountName={0})", ldapContextSource);
		BindAuthenticator staffAuthencator = new BindAuthenticator(ldapContextSource);
		staffAuthencator.setUserSearch(staffUserSearch);
		auth.authenticationProvider(new LdapAuthenticationProvider(staffAuthencator, new UserDetailsServiceLdapAuthoritiesPopulator(userService)));
		
		auth.inMemoryAuthentication().withUser("user").password("P@ssw0rd").roles("ADMIN");
	}
}
