package com.example.community.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.example.community.service.*.*(..))")
    public void pointcut() {

    }

    /**
     * Before.
     * 实现针对service记录日志的业务逻辑
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户[1.2.3.4]，在[xxx时间]，访问了[com.nowcoder.community.service.xxx].
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String ip = "";
        if (attributes == null) {
            // 这是一个特殊的调用（Kafka消息队列调用service，没有来源ip，无法记日志）
            ip = "系统服务";
        } else {
            // 正常用户调用，记录ip日志
            HttpServletRequest request = attributes.getRequest();
            ip = request.getRemoteHost();
        }
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s]，在[%s]，访问了[%s]。", ip, now, target));
    }
}
