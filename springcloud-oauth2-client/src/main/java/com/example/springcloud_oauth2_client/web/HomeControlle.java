package com.example.springcloud_oauth2_client.web;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class HomeControlle {
	
	 @Value("${security.oauth2.resource.userInfoUri}")
	 String userInfoUrl;
	
	 @Autowired
	 RestTemplate restTemplate;
	 
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

	  @RequestMapping({"/github"})
	  public String logingitlab(@RequestParam(value = "code", required = false) String  code) {
		 System.out.println("github/"+"我回调了没"+code);
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
	
	@RequestMapping({ "/getUserInfo"})
	  public String getUserInfo(String access_token) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("access_token", access_token);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "bearer " + access_token);
		HttpEntity<String> request = new HttpEntity(params, headers);
		HttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
		HttpMessageConverter stringHttpMessageConverternew = new StringHttpMessageConverter();
		restTemplate.setMessageConverters(Arrays
				.asList(new HttpMessageConverter[] {
						formHttpMessageConverter,
						stringHttpMessageConverternew }));
		ResponseEntity<String> result = restTemplate.exchange(userInfoUrl, HttpMethod.POST, request, String.class);
		System.out.println(	result.getBody());
	//	UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginname, DigestUtils.sha256Hex(password));
	    return "index";
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
