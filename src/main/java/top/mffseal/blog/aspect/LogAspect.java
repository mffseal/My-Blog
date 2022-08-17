package top.mffseal.blog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect  //表示该组件是一个AOP
@Component  //让springboot能扫描到这个组件
public class LogAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //注解指定方法为切入点，参数指定拦截哪些类，保持空方法体
    //[修饰符]返回值类型 包名.类名.方法名（参数
    //拦截web下的所有类的所有方法
    //execution表达式注意格式，最后是(..)表示方法
    @Pointcut("execution(* top.mffseal.blog.web.*.*(..))")
    public void log() {
    }

    //在切面前执行
    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        //获取url和ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String url = request.getRequestURL().toString();
        String ip = request.getRemoteAddr();

        //获取切入点的类名，方法名，请求参数
        String classMethod = joinPoint.getSignature().getDeclaringTypeName() + '.'  //得到拦截到的类名
                + joinPoint.getSignature().getName();  //得到拦截到的方法名
        Object[] args = joinPoint.getArgs();
        RequestLog requestLog = new RequestLog(url, ip, classMethod, args);
        logger.info("Request : {}", requestLog);
    }

    //在切面后执行
    @After("log()")
    public void doAfter() {
        //logger.info("------doAfter------");
    }

    //返回值 将结果参数绑定到returning
    @AfterReturning(returning = "result", pointcut = "log()")
    public void doAfterReturn(Object result) {
        logger.info("Result : {}", result);
    }

    //内部类，日志内容容器
    private class RequestLog {
        private String url;
        private String ip;
        private String classMethod;
        private Object[] args;

        public RequestLog(String url, String ip, String classMethod, Object[] args) {
            this.url = url;
            this.ip = ip;
            this.classMethod = classMethod;
            this.args = args;
        }

        //便于记录日志后转化为字符串
        @Override
        public String toString() {
            return "{" +
                    "url='" + url + '\'' +
                    ", ip='" + ip + '\'' +
                    ", classMethod='" + classMethod + '\'' +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
