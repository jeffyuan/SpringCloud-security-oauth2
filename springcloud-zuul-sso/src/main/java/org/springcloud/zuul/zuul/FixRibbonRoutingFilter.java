package org.springcloud.zuul.zuul;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * @author jeffyuan
 * @version 1.0
 * @createDate 2019/4/3 14:38
 * @updateUser jeffyuan
 * @updateDate 2019/4/3 14:38
 * @updateRemark
 */

//开启这个注解，zuul会在配置时自动添加至过滤器链
//@Component
public class FixRibbonRoutingFilter extends RibbonRoutingFilter {

    //实例化过滤器需要的辅助类，在自动化配置中已经有实例，直接注解注入即可
    @Autowired
    ProxyRequestHelper helper;

    //实例化过滤器需要的工厂类，在自动化配置中已经有实例，直接注解注入即可
    @Autowired

    RibbonCommandFactory<?> ribbonCommandFactory;

    //如果重定向之前没有页面，则给一个默认的地址  
    String defaultSuccessUrl="/index";

    //辅助方法，下面会介绍
    public void setDefaultSuccessUrl(String url) {
        if(url.equals("/")||url.equals("")) { 
            return ; 
        }
        
        this.defaultSuccessUrl=url.startsWith("/")?url:"/"+url;
    }

    //构造类，集成付方法即可
    public FixRibbonRoutingFilter(ProxyRequestHelper helper, RibbonCommandFactory<?> ribbonCommandFactory) {
    super(helper, ribbonCommandFactory, Collections.emptyList());

    // TODO Auto-generated constructor stub
    }

    //辅助方法类，下面会介绍

    private void addPathCache(String requestPath,String requestServiceId ){

        if(requestPath.equals("/")||requestPath.equals("")) {
            requestPath=defaultSuccessUrl;
        }

        HttpSession cache = RequestContext.getCurrentContext().getRequest().getSession();
        if(!isHasCache(requestPath)) {
            cache.setAttribute(requestPath,requestServiceId);
        }

    }

    //辅助方法类，判断是否有缓存
    private boolean isHasCache(String requestPath)  {
        HttpSession cache=RequestContext.getCurrentContext().getRequest().getSession();
        return cache.getAttribute(requestPath)!=null?true:false;
    }

    //辅助方法类，下面会介绍
    private String getServiceIdAndRemove(String requestPath)
    {
        HttpSession cache=RequestContext.getCurrentContext().getRequest().getSession();

        String serviceId="";
        if(isHasCache(requestPath))        {
            serviceId= (String) cache.getAttribute(requestPath);
            cache.removeAttribute(requestPath);
        }
        return serviceId;
    }

    //辅助方法类，组装正确的重定向地址
    private void assembleRealPath(ClientHttpResponse response, URI location, String nowPath, String serviceId) {

        int nowPort=location.getPort()<=0?80:location.getPort();
        String newPath=location.getScheme()+"://"+location.getHost()+":"+nowPort+"/"+serviceId+nowPath;
        newPath=location.getQuery()==null?newPath:(newPath+"?"+location.getQuery());

        newPath=location.getFragment()==null?newPath:(newPath+"#"+location.getFragment());

        URI newLocation=null;

        try {
            newLocation = new URI(newPath);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        response.getHeaders().setLocation(newLocation);
    }

    //核心业务逻辑，其他都不变，增加对reponse的处理逻辑即可
    @Override

    public Object run() {

        // TODO Auto-generated method stub
        RequestContext context = RequestContext.getCurrentContext();
        this.helper.addIgnoredHeaders();

        try {
            RibbonCommandContext commandContext = buildCommandContext(context);

            //获取重定向前访问的url的资源路径，这个地址是不包含host的
            String preUrl=commandContext.getUri();
            //如果现在被重定向到的是登录页面，则缓存访问前一刻资源的路径和服务ID，并且只缓存记录这个SESSION访问的第一个ServiceID

            if(preUrl.equals("/login")) {
                //记录登录时的serviceID作为默认的serviceID
                addPathCache(defaultSuccessUrl,commandContext.getServiceId());
            }

            ClientHttpResponse response = forward(commandContext);

            //下面是具体的reponse处理逻辑
            URI location=response.getHeaders().getLocation();

            if(response.getStatusCode()== HttpStatus.FOUND&&location!=null) {
                String nowPath=location.getPath();
                if(nowPath.equals("/login")) {

                //如果是被重定向了，则记录之前的路径
                String serviceId=commandContext.getServiceId();
                addPathCache(preUrl,serviceId);
                assembleRealPath(response,location,nowPath, serviceId);

                } else if(isHasCache(nowPath)){

                    //如果是缓存过这个页面，则获取缓存路径重新封装并重定向到缓存位置
                    String serviceId=getServiceIdAndRemove(nowPath);
                    assembleRealPath(response,location,nowPath,serviceId);
                } else if(nowPath.equals("/")||nowPath.equals("")) {
                        //如果资源是"/"或为空，则代表是直接在浏览器输入login页面登录的，转到默认页面。 
                     String serviceId=getServiceIdAndRemove(defaultSuccessUrl);
                     assembleRealPath(response,location,defaultSuccessUrl, serviceId);
                } else {//其他情况则什么也不做
                }

            }

            setResponse(response);

            return response;

        } catch (ZuulException ex) {
            throw new ZuulRuntimeException(ex);
        } catch (Exception ex) {
            throw new ZuulRuntimeException(ex);
        }
    }

}