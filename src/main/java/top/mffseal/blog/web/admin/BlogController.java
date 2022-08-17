package top.mffseal.blog.web.admin;

import top.mffseal.blog.po.Blog;
import top.mffseal.blog.po.User;
import top.mffseal.blog.service.BlogService;
import top.mffseal.blog.service.TagService;
import top.mffseal.blog.service.TypeService;
import top.mffseal.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {
    private final BlogService blogService;
    private final TypeService typeService;
    private final TagService tagService;

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    public BlogController(BlogService blogService, TypeService typeService, TagService tagService) {
        this.blogService = blogService;
        this.typeService = typeService;
        this.tagService = tagService;
    }

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 10, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model) {
        //在页面加载时加载类型的下拉选框内容
        //TODO: 通过semantic ui支持的ajax获得下拉选框内容
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));

        return LIST;
    }

    //thymeleaf+springMVC+AJAX实现局部刷新
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 10, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, BlogQuery blog, Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));

        //返回admin文件夹下的blogs文件中的blogList片段
        return "admin/blogs :: blogList";
    }

    //注入type和tag
    private void setTypeAndTag(Model model) {
        //初始化分类列表
        model.addAttribute("types", typeService.listType());
        //初始化标签列表
        model.addAttribute("tags", tagService.listTag());
    }

    //新增博客
    @GetMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());

        return INPUT;
    }

    //修改博客
    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();  //把多个Tag id 处理成一个字符串
        model.addAttribute("blog", blog);

        return INPUT;
    }

    //新增和修改共用一个方法
    @PostMapping("/blogs")
    //session中有user，用于设置blog中的user
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        //TODO: 后端校验，参照type

        blog.setUser((User) session.getAttribute("user"));
        //前端隐含域的name值为type.id,会自动在传入的blog对象内实例化一个Type类，并调用setId，这个type类数据不完整
        //通过不完整的type中的id值，调用typeService查询到数据库内完整的type类并注入到blog中
        //TODO: 调试验证
        blog.setType(typeService.getType(blog.getType().getId()));
        //前端传递过来的tagIds是形如1,2,3的数列字符串
        blog.setTags(tagService.listTag(blog.getTagIds()));

        //TODO: 自动添加标签
        //List<Tag> tags = blog.getTags();
        //for (Tag tag : tags)
        //    if (tagService.getTagByName(tag.getName())!=null)
        //        tagService.saveTag(tag);

        Blog b;
        if (blog.getId() == null) {
            b = blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog.getId(), blog);
        }

        if (b == null)
            attributes.addFlashAttribute("message", "操作失败");
        else
            attributes.addFlashAttribute("message", "操作成功");
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "删除成功");
        return REDIRECT_LIST;
    }
}
