package com.brp.service;

import java.util.List;

import com.brp.entity.MemoEventEntity;

/** 
 * <p>Project: MyBase</p> 
 * <p>Title: MemoEventService.java</p> 
 * <p>Description: TODO</p> 
 * <p>Copyright (c) 2016 xjw Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:shenyuchuan@itiaoling.com">申鱼川</a>
 */
public interface MemoEventService {
	void insertMemoEvent(MemoEventEntity memoEvent);
	List<MemoEventEntity> getMemoEventByUserId(Integer userId);
}

