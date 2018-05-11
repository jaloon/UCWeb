package com.tipray.bean;

import java.io.Serializable;

/**
 * Dropdown 控件数据格式
 *
 * @author chenlong
 * @version 1.0 2018-04-02
 */
public class DropdownData implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * value值
     */
    private long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 分组ID
     */
    private long groupId;
    /**
     * 分组名
     */
    private String groupName;
    /**
     * 是否禁选
     */
    private boolean disabled;
    /**
     * 是否选中
     */
    private boolean selected;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DropdownData{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", groupId=").append(groupId);
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", disabled=").append(disabled);
        sb.append(", selected=").append(selected);
        sb.append('}');
        return sb.toString();
    }
}
