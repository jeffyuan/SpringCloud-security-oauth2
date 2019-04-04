package org.springcloud.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jeffyuan
 * @version 1.0
 * @createDate 2019/4/2 13:48
 * @updateUser jeffyuan
 * @updateDate 2019/4/2 13:48
 * @updateRemark
 */
@Controller
public class MainController {
    @RequestMapping(value = {"", "/index"})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("ui1/aa");
        return modelAndView;
    }
}
