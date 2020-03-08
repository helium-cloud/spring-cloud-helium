package org.helium.http.utils;

/**
 * 
 * 
 * 1. 精确匹配
 * 2. 扩展名匹配
 * 3. 路径匹配
 * 4. 默认匹配
 * 5. 带通配符的Restful风格路径匹配 {helium框架扩展}
 * Created by Gao Lei on 1/12/17.
 */
public enum UrlPatternType {
	EXACT,
	EXTENSION,
	PATH,
	DEFAULT,
}


/*
精确匹配，servlet-mapping1：<url-pattern>/user/users.html</url-pattern>，servlet-mapping2：<url-pattern>/*</url-pattern>。当一个请求http://localhost:8080/appDemo/user/users.html来的时候，servlet-mapping1匹配到，不再用servlet-mapping2匹配
路径匹配，先最长路径匹配，再最短路径匹配servlet-mapping1：<url-pattern>/user/*</url-pattern>，servlet-mapping2：<url-pattern>/*</url-pattern>。当一个请求http://localhost:8080/appDemo/user/users.html来的时候，servlet-mapping1匹配到，不再用servlet-mapping2匹配
扩展名匹配，servlet-mapping1：<url-pattern>/user/*</url-pattern>，servlet-mapping2：<url-pattern>*.action</url-pattern>。当一个请求http://localhost:8080/appDemo/user/addUser.action来的时候，servlet-mapping1匹配到，不再用servlet-mapping2匹配
缺省匹配，以上都找不到servlet，就用默认的servlet，配置为<url-pattern>/</url-pattern>
 */
