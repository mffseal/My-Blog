package top.mffseal.blog.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {
    //请求未到达控制器前预处理
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // request里有session id(相当于密码)
        if (request.getSession().getAttribute("user") == null) {
            //验证失败则重定向并终止后续操作
            /**
             * request.getRequestDispatcher():
             * 是请求转发，前后页面共享一个request。
             * 返回的是一个RequestDispatcher对象。
             *
             * response.sendRedirect():
             * 是重新定向，前后页面不是一个request。
             *
             * RequestDispatcher.forward():
             * 在服务器端运行，对于浏览器来说是“透明的”。
             * 直接将请求转发到指定URL。
             * 能够直接获得上一个请求的数据，也就是说采用请求转发，request对象始终存在，不会重新创建。
             * 可以一个Servlet接收request请求，另一个Servlet用这个request请求来产生response。
             * request传递的请求，response是客户端返回的信息。
             * forward要在response到达客户端之前调用，也就是 before response body output has been flushed。
             * 如果不是的话，它会报出异常。
             *
             * HttpServletResponse.sendRedirect():
             * 通过向客户浏览器发送命令来完成，对于浏览器来说是“不透明”。
             * 跳转到指定的URL地址，产生一个新的request，上一个request中的数据会丢失。
             * 所以要传递参数只有在url后加参。
             *
             * */
            request.setAttribute("message", "非法访问，请先登录！");
            request.getRequestDispatcher("/admin").forward(request, response);  // request用于保存到来的请求参数

            //response.sendRedirect("/admin");
            return false;
        }

        //验证通过则继续后续处理
        return true;
    }
}