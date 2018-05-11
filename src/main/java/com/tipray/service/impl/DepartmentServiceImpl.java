package com.tipray.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tipray.bean.Department;
import com.tipray.bean.GridPage;
import com.tipray.bean.Page;
import com.tipray.core.GridConfig;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.ServiceException;
import com.tipray.dao.DepartmentDao;
import com.tipray.service.DepartmentService;
import com.tipray.util.DepartmentUtil;
import com.tipray.util.StringUtil;

/**
 * 部门管理业务层
 * 
 * @author chenlong
 * @version 1.0 2017-12-22
 *
 */
@Transactional(rollbackForClassName = { "ServiceException", "Exception" })
@Service("departmentService")
public class DepartmentServiceImpl extends BaseAction implements DepartmentService {
	@Resource
	private DepartmentDao departmentDao;

	private String getCode(Department bean) {
		String code = "";
		String maxCode = departmentDao.getMaxCode(bean.getParent().getId());
		if (maxCode != null) {
			code = DepartmentUtil.getNextCode(maxCode);
		} else {
			Department parentDept = departmentDao.getById(bean.getParent().getId());
			if (parentDept != null) {
				code = DepartmentUtil.getFirstChildCode(parentDept.getCode());
			} else {
				code = DepartmentUtil.getRootCode();
			}
		}
		return code;
	}

	@Override
	public Department addDepartment(Department bean) {
		if (bean != null) {
			bean.setCode(getCode(bean));
			departmentDao.add(bean);
		}
		return bean;
	}

	@Override
	public Department updateDepartment(Department bean) {
		if (bean != null) {
			departmentDao.update(bean);
		}
		return bean;
	}

	@Override
	public void deleteDepartmentsByIds(String ids) throws ServiceException {
		if (ids == null) {
			return;
		}
		for (String id : ids.split(",")) {
			Long deptId = Long.parseLong(id);
			Department department = departmentDao.getById(deptId);
			if (department != null) {
				if (deptId == GridConfig.ROOT_DEPT_ID) {
					throw new ServiceException("禁止删除根部门！");
				}
				if (departmentDao.countUserByDeptId(deptId) > 0) {
					throw new ServiceException("禁止删除，所选部门或子部门中包含用户！");
				}
				departmentDao.delete(deptId);
			}
		}
	}

	@Override
	public Department getDepartmentById(Long id) {
		if (id == null) {
			return null;
		}
		return departmentDao.getById(id);
	}

	@Override
	public Department getDepartmentByNameAndParentId(Department dept) {
		return departmentDao.getByNameAndParentId(dept);
	}

	@Override
	public List<Department> finSubDepartments(Long id) {
		if (id != null) {
			return departmentDao.findByParentId(id);
		}
		return null;
	}

	@Override
	public GridPage<Department> findDepartmentsForPage(Department dept, Page page) {
		Long total = departmentDao.countByVo(dept);
		List<Department> list = departmentDao.findByVo(dept, page);
		return new GridPage<Department>(list, total, page.getPageId(), page.getRows(), list.size(), dept);
	}

	@Override
	public List<Department> findChildDepartmentByCodes(List<String> deptCodes) {
		List<Department> deptList = new ArrayList<Department>();
		if (deptCodes != null) {
			for (String code : deptCodes) {
				deptList.addAll(departmentDao.findByCode(code));
			}
		}
		return deptList;
	}

	@Override
	public List<Department> finParentDepartments(Long id) {
		Department dept = departmentDao.getById(id);
		if (dept != null) {
			return departmentDao.finParentsByCode(dept.getCode());
		}
		return null;

	}

	@Override
	public List<Department> findDepartmentByIds(String ids) {
		if (StringUtil.isNotEmpty(ids)) {
			return departmentDao.findDepartmentByIds(ids);
		}
		return null;
	}

	@Override
	public List<Department> findAllDepartment() {
		return departmentDao.findAll();
	}

}
