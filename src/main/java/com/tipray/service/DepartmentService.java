package com.tipray.service;

import java.util.List;

import com.tipray.bean.Department;
import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.core.exception.ServiceException;

/**
 * DepartmentService
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
public interface DepartmentService {
	/**
	 * 新增部门
	 * 
	 * @param user
	 * @throws ServiceException
	 */
    Department addDepartment(Department bean) throws ServiceException;

	/**
	 * 、 修改部门
	 * 
	 * @param user
	 */
    Department updateDepartment(Department bean) throws ServiceException;

	/**
	 * 根据Id删除部门
	 * 
	 * @param user
	 */
    void deleteDepartmentsByIds(String ids) throws ServiceException;

	/**
	 * 根据Id获取部门
	 * 
	 * @param id
	 * @return
	 */
    Department getDepartmentById(Long id);

	/**
	 * 根据名称和父部门ID获取部门
	 * 
	 * @param dept
	 * @return
	 */
    Department getDepartmentByNameAndParentId(Department dept);

	/**
	 * 获取子部门
	 * 
	 * @param id
	 * @return
	 */
    List<Department> finSubDepartments(Long id);

	/**
	 * 获取所有的父节点、祖父节点等
	 * 
	 * @param id
	 * @return
	 */
    List<Department> finParentDepartments(Long id);

	/**
	 * 分页查询
	 * 
	 * @param dept
	 * @param pageVo
	 * @return
	 */
    GridPage<Department> findDepartmentsForPage(Department dept, Page page);

	/**
	 * 根据部门Id集合获取部门
	 * 
	 * @param ids
	 * @return
	 */
    List<Department> findDepartmentByIds(String ids);

	/**
	 * 根据部门编码获取部门及其子部门
	 * 
	 * @param deptCodes
	 * @return
	 */
    List<Department> findChildDepartmentByCodes(List<String> deptCodes);

	List<Department> findAllDepartment();
}
