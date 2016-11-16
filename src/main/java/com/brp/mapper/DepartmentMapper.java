package com.brp.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.brp.entity.DepartmentEntity;
import com.brp.util.query.DepartmentQuery;

/** 
 * <p>Project: MyBase</p> 
 * <p>Title: DeparmentMapper.java</p> 
 * <p>Description: TODO</p> 
 * <p>Copyright (c) 2016 xjw Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:shenyuchuan@itiaoling.com">申鱼川</a>
 */
@Repository
public interface DepartmentMapper {
	void insertDepartment(DepartmentEntity department);
	List<DepartmentEntity> getDepartmentList(DepartmentQuery departmentQuery);
	void updateDepartment(DepartmentEntity department);
	DepartmentEntity getDepartmentById(Integer id);
}

