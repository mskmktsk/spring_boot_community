package com.osozos.community.service.Impl;

import com.osozos.community.mapper.UserMapper;
import com.osozos.community.pojo.User;
import com.osozos.community.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper mapper;

    public User getGithubUser(Integer id) {
        return mapper.getGithubUser(id);
    }
}
