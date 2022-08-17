package top.mffseal.blog.web.admin;

import top.mffseal.blog.po.Type;
import top.mffseal.blog.service.TypeService;
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
public class TypeController {
    private final TypeService typeService;

    @Autowired
    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    //分页列表展示页面
    @GetMapping("/types")
    //默认一页显示10个，按照id倒叙排列
    public String list(@PageableDefault(size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        //将查询结果放到前端展示模型中，前端以此拿到page的json对象
        model.addAttribute("page", typeService.listType(pageable));
        return "admin/types";
    }

    @GetMapping("/types/input")
    public String input(Model model) {
        //跳转到分类添加页时，给前端传递一个空type对象用作数据容器
        model.addAttribute("type", new Type());
        return "admin/types-input";
    }

    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        //将要修改的type放到model中，并返回到页面上
        model.addAttribute("type", typeService.getType(id));
        return "admin/types-input";
    }

    //post和get类型不同，所以同一个url不冲突
    @PostMapping("/types")
    //页面传递过来是一个name值，参数接受Type类型则自动将name封装进type
    //@Valid指定要接受的参数，BindingResult类接收校验结果
    //参数中BindingResult一定要与传递过来的Type紧挨着
    public String post(@Valid Type type, BindingResult result, RedirectAttributes attributes) {
        //非空的后端校验
        if (result.hasErrors()) {
            return "admin/types-input";
        }

        //名称唯一的后端校验
        //type.getName()重新解封装
        Type type1 = typeService.getTypeByName(type.getName());
        //名称重复
        if (type1 != null) {
            //添加自定义错误(验证的值，自定义对应错误名称，错误消息)
            result.rejectValue("name", "nameError", "该分类已存在");
            return "admin/types-input";
        }

        Type type2 = typeService.saveType(type);
        if (type2 == null) {
            //新增失败
            attributes.addFlashAttribute("message", "添加失败");
        } else {
            //新增成功
            attributes.addFlashAttribute("message", "添加成功");
        }
        return "redirect:/admin/types";
    }

    @PostMapping("/types/{id}")
    public String editPost(@Valid Type type, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        //非空的后端校验
        if (result.hasErrors()) {
            return "admin/types-input";
        }

        //名称唯一的后端校验
        //type.getName()重新解封装
        Type type1 = typeService.getTypeByName(type.getName());
        //名称重复
        if (type1 != null) {
            //添加自定义错误(验证的值，自定义对应错误名称，错误消息)
            result.rejectValue("name", "nameError", "该分类已存在");
            return "admin/types-input";
        }

        Type type2 = typeService.updateType(id, type);
        if (type2 == null) {
            //新增失败
            attributes.addFlashAttribute("message", "更新失败");
        } else {
            //新增成功
            attributes.addFlashAttribute("message", "更新成功");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        typeService.deleteType(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/types";
    }
}
