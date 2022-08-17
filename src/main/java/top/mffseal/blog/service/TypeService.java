package top.mffseal.blog.service;

import top.mffseal.blog.po.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {

    //保存
    Type saveType(Type type);

    //查询
    Type getType(Long id);

    Type getTypeByName(String name);

    //分页
    Page<Type> listType(Pageable pageable);

    //所有
    List<Type> listType();

    //定义拿到数据的多少
    List<Type> listTypeTop(Integer size);

    //修改
    Type updateType(Long id, Type type);

    //删除
    void deleteType(Long id);
}
