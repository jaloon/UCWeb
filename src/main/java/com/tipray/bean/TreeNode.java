package com.tipray.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 树节点属性
 *
 * @author chends
 */
public class TreeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 节点Id
     */
    private String id;
    /**
     * 父节点Id
     */
    private String parentId;
    /**
     * 节点主键
     */
    private String keyId;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 是否选中,运用于树
     */
    private Boolean checked;
    /**
     * 节点的子节点数据集合
     */
    private List<TreeNode> children;
    /**
     * 设置节点的 checkbox / radio 是否禁用
     */
    private Boolean chkDisabled;
    /**
     * 节点自定义图标的 URL 路径
     */
    private String icon;
    /**
     * 记录 treeNode 节点是否为父节点
     */
    private Boolean isParent;
    /**
     * 设置节点是否隐藏 checkbox / radio
     */
    public Boolean nocheck;
    /**
     * 记录 treeNode 节点的 展开 / 折叠 状态
     */
    private Boolean open;

    public void addChildren(TreeNode node) {
        if (node != null) {
            if (children == null) {
                children = new ArrayList<>();
            }
            children.add(node);
        }
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsParent() {
        return isParent;
    }

    public void setIsParent(Boolean isParent) {
        this.isParent = isParent;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Boolean getNocheck() {
        return nocheck;
    }

    public void setNocheck(Boolean nocheck) {
        this.nocheck = nocheck;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public Boolean getChkDisabled() {
        return chkDisabled;
    }

    public void setChkDisabled(Boolean chkDisabled) {
        this.chkDisabled = chkDisabled;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (id != null) {
            sb.append(", id='").append(id).append('\'');
        }
        if (parentId != null) {
            sb.append(", parentId='").append(parentId).append('\'');
        }
        if (keyId != null) {
            sb.append(", keyId='").append(keyId).append('\'');
        }
        if (name != null) {
            sb.append(", name='").append(name).append('\'');
        }
        if (checked != null) {
            sb.append(", checked=").append(checked);
        }
        if (children != null) {
            sb.append(", children=").append(children);
        }
        if (chkDisabled != null) {
            sb.append(", chkDisabled=").append(chkDisabled);
        }
        if (icon != null) {
            sb.append(", icon='").append(icon).append('\'');
        }
        if (isParent != null) {
            sb.append(", isParent=").append(isParent);
        }
        if (nocheck != null) {
            sb.append(", nocheck=").append(nocheck);
        }
        if (open != null) {
            sb.append(", open=").append(open);
        }
        if (sb.length() > 0) {
            sb.replace(0, 2, "TreeNode{");
            sb.append('}');
        }
        return sb.toString();
    }
}
