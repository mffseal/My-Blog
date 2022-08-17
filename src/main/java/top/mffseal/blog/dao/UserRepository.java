package top.mffseal.blog.dao;

import top.mffseal.blog.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

//<dao操作的对象，主键类型>
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndPassword(String username, String password);
}
