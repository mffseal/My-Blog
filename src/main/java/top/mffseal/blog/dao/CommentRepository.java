package top.mffseal.blog.dao;

import top.mffseal.blog.po.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //子条件 parentComment 不为空
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);
}
