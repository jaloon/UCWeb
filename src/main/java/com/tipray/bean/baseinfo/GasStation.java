package com.tipray.bean.baseinfo;

import com.github.crab2died.annotation.ExcelField;
import com.tipray.core.base.BaseBean;

/**
 * 加油站信息表
 *
 * @author chenlong
 * @version 1.0 2017-11-29
 */
public class GasStation extends BaseBean {
    private static final long serialVersionUID = 1L;
    /**
     * 加油站编号
     */
    @ExcelField(title = "ERP代码", order = 2)
    private String officialId;
    /**
     * 加油站名称
     */
    @ExcelField(title = "油站名", order = 3)
    private String name;
    /**
     * 加油站简称
     */
    @ExcelField(title = "油站简称", order = 4)
    private String abbr;
    /**
     * 负责人
     */
    @ExcelField(title = "负责人", order = 5)
    private String director;
    /**
     * 联系号码
     */
    @ExcelField(title = "联系电话", order = 6)
    private String phone;
    /**
     * 联系地址
     */
    @ExcelField(title = "联系地址", order = 7)
    private String address;
    /**
     * 所属公司
     */
    @ExcelField(title = "所属公司", order = 8)
    private String company;
    /**
     * 经度
     */
    @ExcelField(title = "经度", order = 9)
    private Float longitude;
    /**
     * 纬度
     */
    @ExcelField(title = "纬度", order = 10)
    private Float latitude;
    /**
     * 允许开关锁坐标半径（米）
     */
    @ExcelField(title = "允许开关锁坐标半径（米）", order = 11)
    private Integer radius;
    /**
     * 占地范围 （经纬度浮点值字节数组）
     */
    private byte[] cover;
    /**
     * 占地范围 （经纬度集合，用逗号,相隔 ）
     */
    @ExcelField(title = "占地范围（经纬度集合）", order = 12)
    private String coverRegion;

    public String getOfficialId() {
        return officialId;
    }

    public void setOfficialId(String officialId) {
        this.officialId = officialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getCoverRegion() {
        return coverRegion;
    }

    public void setCoverRegion(String coverRegion) {
        this.coverRegion = coverRegion;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (getId() != null) {
            sb.append(", id=").append(getId());
        }
        if (officialId != null) {
            sb.append(", officialId='").append(officialId).append('\'');
        }
        if (name != null) {
            sb.append(", name='").append(name).append('\'');
        }
        if (abbr != null) {
            sb.append(", abbr='").append(abbr).append('\'');
        }
        if (director != null) {
            sb.append(", director='").append(director).append('\'');
        }
        if (phone != null) {
            sb.append(", phone='").append(phone).append('\'');
        }
        if (address != null) {
            sb.append(", address='").append(address).append('\'');
        }
        if (company != null) {
            sb.append(", company='").append(company).append('\'');
        }
        if (longitude != null) {
            sb.append(", longitude=").append(longitude);
        }
        if (latitude != null) {
            sb.append(", latitude=").append(latitude);
        }
        if (radius != null) {
            sb.append(", radius=").append(radius);
        }
        if (cover != null && cover.length > 0) {
            sb.append(", cover=");
            sb.append('[');
            for (int i = 0, len = cover.length; i < len; ++i)
                sb.append(i == 0 ? "" : ", ").append(cover[i]);
            sb.append(']');
        }
        if (coverRegion != null) {
            sb.append(", coverRegion='").append(coverRegion).append('\'');
        }
        if (getRemark() != null) {
            sb.append(", remark='").append(getRemark()).append('\'');
        }
        if (sb.length() > 0) {
            sb.replace(0,2,"GasStation{");
            sb.append('}');
        }
        return sb.toString();
    }
}
