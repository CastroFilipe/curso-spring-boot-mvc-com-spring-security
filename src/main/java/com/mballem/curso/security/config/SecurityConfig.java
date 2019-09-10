package com.mballem.curso.security.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Arquivo de configuração que sobreescreve a configuração default do spring security.
 * 
 * */
//Anotação usada para informar ao Springframework que essa é uma classe de configuração do springsecurity
@EnableWebSecurity
//WebSecurityConfigurerAdapter possui métodos de configurações prontos e que serão sobrescritos de acordo com a necessidade.
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//Solicita autenticação para todos os recursos
		http.authorizeRequests()
		.antMatchers("/webjars/**","/css/**","/image/**", "/js/**").permitAll()
		.antMatchers("/", "/home").permitAll()
		.anyRequest().authenticated()
		.and()//define o comportamento de login em caso de sucesso ou falha
			.formLogin()
			.loginPage("/login")//define a nossa página de login
			.defaultSuccessUrl("/", true)
			.failureUrl("/login-error")
			.permitAll()//todos devem ter acesso aos endipoints /login e /login-error
		
		.and()//define o comportamento de logout
			.logout()
			.logoutSuccessUrl("/");
	}

}
