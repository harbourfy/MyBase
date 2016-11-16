package com.brp.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.brp.base.Constant;
import com.brp.entity.UserEntity;
import com.brp.model.BRPUserInfo;
import com.brp.model.LoginUser;
import com.brp.util.api.PMSApiUtils;
import com.brp.util.cookie.LoggedCookie;
import com.google.gson.Gson;

/** 
 * <p>Project: BRP</p> 
 * <p>Title: UserUtils.java</p> 
 * <p>Description: TODO</p> 
 * <p>Copyright (c) 2016 xjw Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:shenyuchuan@itiaoling.com">申鱼川</a>
 */
public class UserUtils {
	private static String THIS_TOKEN = "c32f5089-2d3b-4cd9-b3c2-8210bbecd70b";
	private static String USER_ID = "55560700";
	
	/**
	 * 获取用户登录信息
	 * @param request
	 * @return
	 */
	public static UserEntity getLoginUser(HttpServletRequest request){
		UserEntity loginUser = (UserEntity) request.getSession().getAttribute("loginUser");
		if(CommonUtils.getLocalEnv() && loginUser == null){
			//本地随便取一个
			loginUser = new UserEntity();
			loginUser.setUserName("申鱼川");
			loginUser.setId(1l);
			loginUser.setUpdateUser("申鱼川");
		}
		
		return loginUser;
	}
	
	/**
	 * 获取BRP用户登录信息
	 * @param request
	 * @return
	 */
	public static BRPUserInfo getBRPUser(HttpServletRequest request){
		BRPUserInfo user = (BRPUserInfo) request.getSession().getAttribute("brpUserInfo");
		if(CommonUtils.getLocalEnv() && user == null){
			//本地随便取一个
			String token = LoggedCookie.checkTokenCookie(request);
			if(StringUtils.isBlank(token) && CommonUtils.getLocalEnv()){
				token = PMSApiUtils.getLocalToken();
			}
			String result = PMSApiUtils.getBrpUserInfo(USER_ID, token);
            Gson gson = new Gson();
            user = gson.fromJson(result, BRPUserInfo.class);
            
		}else if(user != null && Constant.ZREO.equals(user.getRolelevel())){
        	user = null;
        }
		
		return user;
	}
}

