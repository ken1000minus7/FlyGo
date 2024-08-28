package com.unitedgo.user_service.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
	
	@Id
	String username;
	String password;
	String name;
	String email;
	String city;
	String phoneNumber;
	
	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("USER"));
	}
	
	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	@Transient
	public boolean isEnabled() {
		return true;
	}
}
