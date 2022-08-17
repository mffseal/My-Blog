package top.mffseal.blog.web;

import top.mffseal.blog.po.Type;
import top.mffseal.blog.service.BlogService;
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

import java.util.List;

@Controller
public class TypeShowController {
    private final TypeService typeService;
    private final BlogService blogService;

    @Autowired
    public TypeShowController(TypeService typeService, BlogService blogService1) {
        this.typeService = typeService;
        this.blogService = blogService1;
    }

    @GetMapping("/types/{id}")
    public String types(@PageableDefault(size = 10, sort = {"updateTime"},
            direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long id, Model model) {
        List<Type> types = typeService.listTypeTop(10000);
        //从导航栏点入
        if (id == -1)
            id = types.get(0).getId();  //拿到博客最多的标签

        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setTypeId(id);
        model.addAttribute("types", types);
        model.addAttribute("page", blogService.listBlog(pageable, blogQuery));

        //用于前端渲染选中状态
        model.addAttribute("activeTypeId", id);

        return "types";
    }
}
