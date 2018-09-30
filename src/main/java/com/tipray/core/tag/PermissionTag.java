package com.tipray.core.tag;

import com.tipray.bean.Session;
import com.tipray.core.ThreadVariable;
import com.tipray.util.PermissionUtil;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 权限标签
 * 通过此标签包围的代码，只有在拥有对应权限的情况下，才会显示
 * @author chends
 *
 */
public class PermissionTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	/**
	 * 权限名称ename，如果多个权限，用逗号进行分隔
	 * 
	 * ename = true 也表示有权限
	 */
	private String ename;
	
	@Override
	public int doStartTag() {

		if (ename == null || ename.length() == 0) {
			return Tag.SKIP_BODY;
		}
		Session session = ThreadVariable.getSession();
		if (session == null || session.getUser() == null) {
			return Tag.SKIP_BODY;
		}
		if (ThreadVariable.getRole().getIsSuper()) {
			return Tag.EVAL_BODY_INCLUDE;
		}
		if("true".equals(ename)){
			return Tag.EVAL_BODY_INCLUDE;
		}
		else if (PermissionUtil.containPermission(ThreadVariable.getPermissions(),ename)) {
			return Tag.EVAL_BODY_INCLUDE;
		} 
		else {
			return Tag.SKIP_BODY;
		}
	}

	@Override
	public int doEndTag() {
		return Tag.EVAL_PAGE;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}
}
