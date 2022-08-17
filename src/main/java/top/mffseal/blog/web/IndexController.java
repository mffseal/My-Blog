package top.mffseal.blog.web;

import top.mffseal.blog.service.BlogService;
import top.mffseal.blog.service.TagService;
import top.mffseal.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {
    private final BlogService blogService;
    private final TypeService typeService;
    private final TagService tagService;

    @Autowired
    public IndexController(BlogService blogService, TypeService typeService, TagService tagService) {
        this.blogService = blogService;
        this.typeService = typeService;
        this.tagService = tagService;
    }

    @GetMapping("/")
    public String index(@PageableDefault(size = 10, sort = {"updateTime"}, direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model) {
        //文章列表
        model.addAttribute("page", blogService.listBlog(pageable));
        //分类列表
        model.addAttribute("types", typeService.listTypeTop(6));
        //标签列表
        model.addAttribute("tags", tagService.listTagTop(6));
        //推荐文章
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(8));

        return "index";
    }

    //TODO: lucene搜索
    @PostMapping("/search")
    public String search(@PageableDefault(size = 10, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, @RequestParam String query, Model model) {
        //like查询不会自动处理query字符串
        //select * from t_blog where title like '%内容%'
        model.addAttribute("page", blogService.listBlog("%" + query + "%", pageable));
        //将查询的字符串返回到页面中
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model) {
        model.addAttribute("blog", blogService.getAndConvert(id));

        return "blog";
    }

    //页面底部三个最新文章推荐
    @GetMapping("footer/newblog")
    public String newblogs(Model model) {
        model.addAttribute("newblogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }
}
