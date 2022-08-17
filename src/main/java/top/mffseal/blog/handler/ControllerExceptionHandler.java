package top.mffseal.blog.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice  //拦截所有带Controller注解的控制器
public class ControllerExceptionHandler {
    //org.slf4j.Logger
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //标识该方法可以做异常处理，参数代表拦截Exception（所有异常的父类）级别的异常
    @ExceptionHandler(Exception.class)
    //ModelAndView对象类型用于返回一个页面并向前端输出信息
    //参数用于获取页面信息和错误传递
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception e) throws Exception {
        //日志记录
        logger.error("Request URL : {}, Exception : {}", request.getRequestURL(), e);

        //如果错误存在状态码，则不由自定义拦截器处理
        //findAnnotation获取类上的注解
        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        ModelAndView mv = new ModelAndView();
        //添加属性，用于向前端模版传递数据，将错误信息显示在前端页面代码中
        mv.addObject("url", request.getRequestURL());  //获取URL
        mv.addObject("exception", e);  //获取异常信息
        mv.setViewName("error/error");  //设置返回的错误页面

        return mv;
    }
}
