package com.wixis360.verifiedcontractingbackend.security.service;

import com.wixis360.verifiedcontractingbackend.dao.RoleDao;
import com.wixis360.verifiedcontractingbackend.dao.UserDao;
import com.wixis360.verifiedcontractingbackend.dto.UserDto;
import com.wixis360.verifiedcontractingbackend.model.Role;
import com.wixis360.verifiedcontractingbackend.model.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserDao userDao;
	private final RoleDao roleDao;
	private final ModelMapper mapper;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> optionalUser = userDao.findByEmail(username);

		if (!optionalUser.isPresent()) {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}

		User user = optionalUser.get();
		UserDto userDto = getUserDto(user);

		Optional<Role> optionalRole = roleDao.findById(user.getRoleId());
		if (optionalRole.isPresent()) {
			userDto.setRole(optionalRole.get().getName());
		}

		return UserDetailsImpl.build(userDto);
	}

	private UserDto getUserDto(User user) {
		return mapper.map(user, UserDto.class);
	}
}
