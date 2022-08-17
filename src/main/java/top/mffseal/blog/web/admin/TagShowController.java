package top.mffseal.blog.web.admin;

import top.mffseal.blog.po.Tag;
import top.mffseal.blog.service.BlogService;
import top.mffseal.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagShowController {
    private final TagService tagService;
    private final BlogService blogService;

    @Autowired
    public TagShowController(TagService tagService, BlogService blogService1) {
        this.tagService = tagService;
        this.blogService = blogService1;
    }

    @GetMapping("/tags/{id}")
    public String tags(@PageableDefault(size = 10, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long id, Model model) {
        List<Tag> tags = tagService.listTagTop(10000);
        //从导航栏点入
        if (id == -1)
            id = tags.get(0).getId();  //拿到博客最多的标签

        model.addAttribute("tags", tags);
        model.addAttribute("page", blogService.listBlog(id, pageable));

        //用于前端渲染选中状态
        model.addAttribute("activeTagId", id);

        return "tags";
    }
}
