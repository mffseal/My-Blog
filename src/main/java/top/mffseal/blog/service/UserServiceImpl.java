package top.mffseal.blog.service;

import top.mffseal.blog.dao.UserRepository;
import top.mffseal.blog.po.User;
import top.mffseal.blog.util.SHA256Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    //注入，基于字段的依赖注入已不被推荐使用
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, SHA256Utils.code(password));
        return user;
    }
}
