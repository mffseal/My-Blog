package top.mffseal.blog.web.admin;

import top.mffseal.blog.po.User;
import top.mffseal.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {
    private final UserService userService;

    //自动装配时扫描到依赖一个UserService类型，自动ByType找到对应的Impl实现并实例化，通过构造函数注入
    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String loginPage() {
        return "admin/login";
    }

    //验证用户登录信息
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, RedirectAttributes attributes) {
        User user = userService.checkUser(username, password);
        if (user != null) {
            user.setPassword(null);  //不要把密码传到session中
            session.setAttribute("user", user);  //将用户信息存入session
            return "admin/index";
        } else {
            //向重定向页面传递参数，这里不能用Model设置参数，否则目标页面拿不到数据
            attributes.addFlashAttribute("message", "用户名或密码错误");
            //不能直接return "admin/login", 否则第二次登录会出问题
            //redirect跳转会经过控制方法而不是直接返回目标页面
            return "redirect:/admin";
        }
    }

    //注销
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/admin";
    }
}
