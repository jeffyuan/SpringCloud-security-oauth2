package org.springcloud.zuul.controller;

import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jeffyuan
 * @version 1.0
 * @createDate 2019/4/3 18:13
 * @updateUser jeffyuan
 * @updateDate 2019/4/3 18:13
 * @updateRemark
 */
@Controller
public class HomeController {
    /**
     * 服务端会在注册回调的地址 获取code
     * @param code
     * @return
     */
    @RequestMapping({"/bklogin/code"})
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam(value = "code", required = false) String  code) throws IOException {
        SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");

        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String loginIndex(HttpServletRequest request) {
        SecurityContextImpl securityContextImpl = (SecurityContextImpl)request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContextImpl == null) {
            return "login";
        } else {
            return "index";
        }
    }

    @RequestMapping({ "/anonymous" })
    public String anonymous(){
        return "anonymous";
    }


    @RequestMapping("/hello")
    public String hello() throws Exception {
        throw new Exception("发生错误");
    }
}
