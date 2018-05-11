package com.tipray.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tipray.bean.Department;
import com.tipray.bean.GridPage;
import com.tipray.bean.Message;
import com.tipray.bean.Page;
import com.tipray.bean.TreeNode;
import com.tipray.core.annotation.PermissionAnno;
import com.tipray.core.base.BaseAction;
import com.tipray.core.exception.ServiceException;
import com.tipray.service.DepartmentService;
import com.tipray.util.DepartmentUtil;

/**
 * 部门管理控制器
 * 
 * @author chenlong
 * @version 1.0 2017-10-10
 *
 */
@Controller
@RequestMapping("/manage/dept")
@Scope("prototype")
public class DepartmentController extends BaseAction {
	@Autowired
	private DepartmentService departmentService;

	@PermissionAnno("departmentModule")
	@RequestMapping(value = "dispatch.do")
	public String dispatch(String mode, String callback, Department dept, ModelMap modelMap) {
		if (dept != null) {
			if (dept.getId() != null) {
				dept = departmentService.getDepartmentById(dept.getId());
			} else if (dept.getParent() != null && dept.getParent().getId() != null) {
				Department parentDept = departmentService.getDepartmentById(dept.getParent().getId());
				dept.setParent(parentDept);
			}

		}
		modelMap.put("mode", mode);
		modelMap.put("callback", callback);
		modelMap.put("dept", dept);
		return "normal/department/deptEditDlg.jsp";
	}

	@PermissionAnno("departmentModule")
	@RequestMapping(value = "add.do")
	@ResponseBody
	public Message addDepartment(@ModelAttribute Department dept) {
		try {
			departmentService.addDepartment(dept);
			return Message.success();
		} catch (ServiceException e) {
			return Message.error(e);
		}
	}

	@PermissionAnno("departmentModule")
	@RequestMapping(value = "update.do")
	@ResponseBody
	public Message updateDepartment(@ModelAttribute Department dept) {
		try {
			departmentService.updateDepartment(dept);
			return Message.success();
		} catch (ServiceException e) {
			return Message.error(e);
		}
	}

	@PermissionAnno("departmentModule")
	@RequestMapping(value = "delete.do")
	@ResponseBody
	public Message deleteDepartments(String ids) {
		try {
			departmentService.deleteDepartmentsByIds(ids);
			return Message.success();
		} catch (ServiceException e) {
			return Message.error(e);
		}
	}

	@RequestMapping("isExist.do")
	@ResponseBody
	public Boolean isDepartmentExist(@ModelAttribute Department dept) {
		Department department = departmentService.getDepartmentByNameAndParentId(dept);
		return department != null && dept.getId() != department.getId();
	}

	@PermissionAnno("departmentModule")
	@RequestMapping("findByParentId.do")
	@ResponseBody
	public List<Department> findDepartmentByParentId(@ModelAttribute Department dept) {
		List<Department> list = new ArrayList<>();
		if (dept == null || dept.getId() == null) {
			list.add(departmentService.getDepartmentById(1L));
		} else {
			list.addAll(departmentService.finSubDepartments(dept.getId()));
		}
		return list;
	}

	@PermissionAnno("departmentModule")
	@RequestMapping("getDepartmentTree.do")
	@ResponseBody
	public List<TreeNode> getDepartmentTree() {
		List<Department> list = departmentService.findAllDepartment();
		return DepartmentUtil.toDeptTree(list);
	}

	@PermissionAnno("departmentModule")
	@RequestMapping("findForPage.do")
	@ResponseBody
	public GridPage<Department> findDepartmentsForPage(@ModelAttribute Department dept, @ModelAttribute Page page) {
		return departmentService.findDepartmentsForPage(dept, page);
	}

}
