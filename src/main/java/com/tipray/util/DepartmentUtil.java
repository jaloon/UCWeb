package com.tipray.util;

import java.util.ArrayList;
import java.util.List;

import com.tipray.bean.Department;
import com.tipray.bean.TreeNode;
/**
 * 部门工具类
 * @author chends
 *
 */
public class DepartmentUtil {
	/**
	 * 获取第一个部门的内部编码
	 * @return
	 */
	public static String getRootCode() {
		return getFirstChildCode("");
	}
	/**
	 * 获取部门内部编码，格式：1.1. ,  1.2. ,  1.2.1.
	 * @param maxCode
	 * @return
	 */
	public static String getNextCode(Department parentDept,String maxCode) {
		String parentCode = parentDept==null?"":parentDept.getCode();
		int max = 0;//第一个子部门
		if(maxCode!=null && !maxCode.trim().equals("")){
			max = Integer.parseInt(maxCode.substring(parentCode.length(), maxCode.length()-1));
		}
		return parentCode + (max+1) + ".";
	}
	public static String getNextCode(String code) {
		StringBuffer nextCode = new StringBuffer();
		String[] codes = code.split("\\.");
		for (int i = 0; i < codes.length; i++) {
			int subcode = Integer.parseInt(codes[i]);
			if(i==codes.length-1){
				subcode+=1;
			}
			nextCode.append(subcode).append(".");
		}
		return nextCode.toString();
	}
	/**
	 * 获取第一个子节点的code
	 * @param code
	 * @return
	 */
	public static String getFirstChildCode(String code) {
		return code+"1.";
	}
	public static Department getZeroDept(){
		Department dept = new Department();
		dept.setId(0L);
		dept.setCode(".");
		dept.setName("无");
		return dept;
	}
	
	public final static String parseId(List<Department> departments) {
		StringBuffer ids=new StringBuffer();
		for (Department department : departments) {
			ids.append(ids.length()>0?",":"");
			ids.append(department.getId());
		}
		return ids.toString();
	}
	/**
	 * 获取部门Code的集合
	 * @param departments
	 * @return
	 */
	public final static List<String> parseCode(List<Department> departments) {
		List<String> list = new ArrayList<String>();
		for (Department department : departments) {
			if(department!=null && department.getCode()!=null){
				list.add(department.getCode());
			}
		}
		return list;
	}
	
	public final static boolean isSubDept(List<Department> list,Department dept){
		if(list!=null && dept!=null && dept.getCode()!=null){
			for (Department department : list) {
				if(dept.getCode().startsWith(department.getCode())){
					return true;
				}
			}
		}
		return false;
	}
	public static String[] split(String deptPath) {
		if(deptPath==null){
			return new String[0];
		}
		return deptPath.split("->");
	}
	
	public static List<TreeNode> toDeptTree(List<Department> depts){
		List<TreeNode> tree = new ArrayList<>();
		while (depts.size()>0) {
			TreeNode rootNode = appendParentTreeNode(toTreeNode(depts.remove(0)), depts);
			appendChildrenTreeNode(rootNode, depts);
			tree.add(rootNode);
		}
		return tree;
	}
	
	private static TreeNode appendParentTreeNode(TreeNode node,List<Department> depts){
		if(depts!=null && depts.size()>0){
			Long parentId = Long.parseLong(node.getParentId());
			for (Department dept : depts) {
				if(dept.getId()==parentId){
					depts.remove(dept);
					TreeNode parentNode = toTreeNode(dept);
					appendParentTreeNode(parentNode,depts);
					parentNode.addChildren(node);
					return parentNode;
				}
			}
		}
		return node;
	}
	private static void appendChildrenTreeNode(TreeNode node,List<Department> depts){
		if(depts!=null && depts.size()>0){
			Long id = Long.parseLong(node.getId());
			for (int i = 0; i < depts.size(); i++) {
				Department dept = depts.get(i);
				if(dept.getParent().getId()==id){
					depts.remove(i);
					i--;
					TreeNode childrenNode = toTreeNode(dept);
					appendChildrenTreeNode(childrenNode,depts);
					node.addChildren(childrenNode);
				}
			}
		}
	}
	
	private static TreeNode toTreeNode(Department dept){
		TreeNode node = new TreeNode();
		node.setName(dept.getName());
		node.setId(dept.getId()+"");
		node.setKeyId(dept.getId()+"");
		node.setParentId(dept.getParent().getId()+"");
		return node;
	}
}
