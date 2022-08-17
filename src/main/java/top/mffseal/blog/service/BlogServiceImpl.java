package top.mffseal.blog.service;

import top.mffseal.blog.NotFoundException;
import top.mffseal.blog.dao.BlogRepository;
import top.mffseal.blog.po.Blog;
import top.mffseal.blog.po.Type;
import top.mffseal.blog.util.MarkdownUtils;
import top.mffseal.blog.util.MyBeanUtils;
import top.mffseal.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {
    public final BlogRepository blogRepository;

    @Autowired
    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.getOne(id);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                //关联查询，blog与tag多对多关联，从多对多关联表查询数据
                Join join = root.join("tags");
                return cb.equal(join.get("id"), tagId);
            }
        }, pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        //用HashMap读取顺序会随机
        Map<String, List<Blog>> map = new LinkedHashMap<>();
        //获取所有年份和每个年份下的文章
        for (String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }

        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.getOne(id);
        if (blog == null)
            throw new NotFoundException("该文章不存在");

        //md转html
        //实例化新对象，防止Hibernate自动更新数据库
        Blog b = new Blog();
        BeanUtils.copyProperties(blog, b);
        String content = b.getContent();
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

        //自增浏览次数
        blogRepository.updateViews(id);
        return b;
    }

    //分页动态查询
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            //(查询对象，查询条件容器，设置具体条件表达式)
            //把Blog映射成Root类型，可以获取表的属性字段
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                //拼接条件查询组合
                //标题，非空判断
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    //第一个查询条件，表title字段里like传入的title值
                    predicates.add(cb.like(root.<String>get("title"), "%" + blog.getTitle() + "%"));
                }

                //分类，非空判断
                if (blog.getTypeId() != null) {
                    //root获取到blog对象里的type对象上的id属性
                    predicates.add(cb.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }

                //是否推荐
                if (blog.isRecommend()) {
                    //指定类型时要用开头大写的Boolean对象类型
                    predicates.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }

                //开始查询，参数为条件数组，要把构造好的List转数组
                cq.where(predicates.toArray(new Predicate[predicates.size()]));

                return null;
            }
        }, pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        //判断时新增还是修改
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else
            blog.setUpdateTime(new Date());

        return blogRepository.save(blog);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.getOne(id);
        if (b == null)
            throw new NotFoundException("该博客不存在");
        //java浅拷贝会直接将blog里字段的空值复制过去，需要手动过滤属性值为空的属性，只copy非空属性
        //copyProperties第三个参数是需要忽略的属性名数组
        BeanUtils.copyProperties(blog, b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}
