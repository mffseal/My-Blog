package top.mffseal.blog.service;

import top.mffseal.blog.po.User;

public interface UserService {
    //检查用户名和密码的接口
    User checkUser(String username, String password);
}
