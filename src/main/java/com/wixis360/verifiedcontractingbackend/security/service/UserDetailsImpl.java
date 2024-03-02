package com.wixis360.verifiedcontractingbackend.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wixis360.verifiedcontractingbackend.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String email;
	private String profilePicture;
	@JsonIgnore
	private String password;
	private String telephone;
	private String zipCode;
	private String role;
	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(String id, String name, String email, String profilePicture, String password, String telephone,
						   String zipCode, String role, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.profilePicture = profilePicture;
		this.password = password;
		this.telephone = telephone;
		this.zipCode = zipCode;
		this.role = role;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(UserDto user) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(user.getRole()));

		return new UserDetailsImpl(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getProfilePicture(),
				user.getPassword(),
				user.getTelephone(),
				user.getZipCode(),
				user.getRole(),
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getId() {
		return id;
	}

	public String getRole() {
		return role;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	public String getTelephone() {
		return telephone;
	}

	public String getZipCode() {
		return zipCode;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}