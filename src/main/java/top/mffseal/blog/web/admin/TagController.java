package top.mffseal.blog.web.admin;

import top.mffseal.blog.po.Tag;
import top.mffseal.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TagController {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    //标签列表页
    @GetMapping("/tags")
    public String list(@PageableDefault(size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        model.addAttribute("page", tagService.listTag(pageable));
        return "admin/tags";
    }

    //标签新增页
    @GetMapping("/tags/input")
    public String input(Model model) {
        model.addAttribute("tag", new Tag());
        return "admin/tags-input";
    }

    //标签编辑页
    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.getTag(id));
        return "admin/tags-input";
    }

    //表单提交方法
    @PostMapping("/tags")
    public String post(@Valid Tag tag, BindingResult result, RedirectAttributes attributes) {
        //非空检测
        if (result.hasErrors())
            return "admin/tags-input";

        //重复添加检测
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name", "nameError", "该标签已存在");
            return "admin/tags-input";
        }

        Tag tag2 = tagService.saveTag(tag);
        if (tag2 == null)
            attributes.addFlashAttribute("message", "添加失败");
        else
            attributes.addFlashAttribute("message", "添加成功");

        return "redirect:/admin/tags";
    }

    //表单提交方法，用于编辑
    @PostMapping("/tags/{id}")
    public String editPost(@Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        //非空检测
        if (result.hasErrors())
            return "admin/tags-input";

        //重复添加检测
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name", "nameError", "该标签已存在");
            return "admin/tags-input";
        }

        Tag tag2 = tagService.updateTag(id, tag);
        if (tag2 == null)
            attributes.addFlashAttribute("message", "编辑失败");
        else
            attributes.addFlashAttribute("message", "编辑成功");

        return "redirect:/admin/tags";
    }
}
