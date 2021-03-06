package com.brp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.brp.entity.AuthorityEntity;
import com.brp.entity.PositionEntity;
import com.brp.util.query.AuthorityQuery;
import com.brp.util.query.AuthorityVOQuery;
import com.brp.util.query.PositionQuery;
import com.brp.util.vo.AuthorityVO;
import com.brp.util.vo.UserAuthVO;

/** 
 * <p>Project: MyBase</p> 
 * <p>Title: PositionMapper.java</p> 
 * <p>Description: TODO</p> 
 * <p>Copyright (c) 2016 xjw Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:shenyuchuan@itiaoling.com">申鱼川</a>
 */
@Repository
public interface AuthorityMapper {
	void insertAuthority(AuthorityEntity authority);
	void updateAuthority(AuthorityEntity authority);
	AuthorityEntity getAuthorityById(Integer id);
	void deleteAuthorityById(String id);
	void startAuthorityById(String id);
	List<AuthorityEntity> getAuthorityPage(AuthorityQuery authorityQuery);	
	List<AuthorityVO> getAuthorityVOPage(AuthorityVOQuery authorityQuery);
	List<AuthorityEntity> getAuthListByUserId(String userId);
	
}

