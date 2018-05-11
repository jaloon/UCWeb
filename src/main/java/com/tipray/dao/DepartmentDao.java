package com.tipray.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tipray.bean.Department;
import com.tipray.bean.Page;
import com.tipray.core.annotation.MyBatisAnno;
import com.tipray.core.base.BaseDao;

/**
 * DepartmentDao
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@MyBatisAnno
public interface DepartmentDao extends BaseDao<Department> {
	/**
	 * 根据父节点ID查询子节点
	 * 
	 * @param parentId
	 */
    List<Department> findByParentId(Long parentId);

	/**
	 * 根据部门名和父节点ID查询
	 * 
	 * @param name,
	 *            parentId
	 */
    Department getByNameAndParentId(Department dept);

	/**
	 * 获取最大的编码
	 * 
	 * @param parentId
	 * @return
	 */
    String getMaxCode(Long parentId);

	/**
	 * 根据条件查询部门集合
	 * 
	 * @param dept
	 * @param page
	 * @return
	 */
    List<Department> findByVo(@Param("dept") Department dept, @Param("page") Page page);

	/**
	 * 根据条件统计部门个数
	 * 
	 * @param dept
	 * @return
	 */
    Long countByVo(@Param("dept") Department dept);

	/**
	 * 获取所有的子节点
	 * 
	 * @param code
	 * @return
	 */
    List<Department> findByCode(String code);

	/**
	 * 标记删除部门
	 * 
	 * @param code
	 */
    void updateDel(Department dept);

	/**
	 * 判断部门是否被标记为删除
	 * 
	 * @param dept
	 * @return 被标记为删除的部门
	 */
    Long isDel(Department dept);

	/**
	 * 获取父部门、祖父部门等
	 * 
	 * @param code
	 * @return
	 */
    List<Department> finParentsByCode(String code);

	/**
	 * 获取本部门、父部门、祖父部门等
	 * 
	 * @param code
	 * @return
	 */
    List<Department> finDepartmentsByCode(String code);

	/**
	 * 根据部门Id集合获取部门
	 * 
	 * @param ids
	 * @return
	 */
    List<Department> findDepartmentByIds(String ids);

	/**
	 * 统计部门及其子孙部门的用户数
	 * 
	 * @param deptId
	 * @return
	 */
    Long countUserByDeptId(Long deptId);
}
