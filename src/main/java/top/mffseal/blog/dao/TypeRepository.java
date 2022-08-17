package top.mffseal.blog.dao;

import top.mffseal.blog.po.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type, Long> {
    Type findByName(String name);

    //TODO: 不懂
    //自定义查询语句
    @Query("select t from Type t")
    List<Type> findTop(Pageable pageable);
}
