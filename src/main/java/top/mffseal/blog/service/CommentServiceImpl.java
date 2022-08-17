package top.mffseal.blog.service;

import top.mffseal.blog.dao.CommentRepository;
import top.mffseal.blog.po.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        //按创建时间正序排序
        Sort sort = Sort.by(Sort.Direction.ASC, "createTime");

        return eachTopComment(commentRepository.findByBlogIdAndParentCommentNull(blogId, sort));
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        //判断是全新评论还是评论回复
        Long parentCommentId = comment.getParentComment().getId();
        //-1代表是新评论
        if (parentCommentId != -1) {
            //根据parentCommentId拿到实例对象，注入当前回复这个对象中
            comment.setParentComment(commentRepository.getOne(parentCommentId));
        } else {
            //ParentComment在上面实例化了，没有持久化，不这样操作会报错
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }

    //找到所有顶级评论节点
    private List<Comment> eachTopComment(List<Comment> comments) {
        //拷贝一份，防止对原数据库内容造成影响
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment tempComment = new Comment();
            BeanUtils.copyProperties(comment, tempComment);
            commentsView.add(tempComment);
        }
        //合并评论的各层子代到第一级子代集合中
        combineReplies(commentsView);

        return commentsView;
    }

    //找出所有头节点下的子节点，平级处理
    private void combineReplies(List<Comment> comments) {
        for (Comment comment : comments) {
            List<Comment> replies = comment.getReplyComments();
            for (Comment reply : replies) {
                //循环迭代，找出子代，存放在tempReplies中
                traverseReplies(reply);
            }
            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReplyComments(tempReplies);
            //清除临时存放区
            tempReplies = new ArrayList<>();
        }
    }

    //存放迭代找出的所有子代集合
    private List<Comment> tempReplies = new ArrayList<>();

    //找出所有当前评论的回复
    private void traverseReplies(Comment comment) {
        tempReplies.add(comment);
        if (comment.getReplyComments().size() > 0) {
            List<Comment> replies = comment.getReplyComments();
            for (Comment reply : replies) {
                tempReplies.add(reply);
                if (reply.getReplyComments().size() > 0)
                    traverseReplies(reply);
            }
        }
    }
}
