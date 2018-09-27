<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/functions' prefix='fn' %>
<%@ taglib prefix="pop" uri="/pop-tags" %>
<pop:Permission ename="viewOildepot">
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="renderer" content="webkit">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <meta name="viewport" content="width=center-width, initial-scale=1, maximum-scale=1">
        <title>油库管理</title>
        <script src="../../resources/js/base.js"></script>
        <link rel="stylesheet" href="../../resources/css/base.css ">
        <link rel="stylesheet" href="../../resources/css/tabEdit.css ">
        <script src="../../resources/plugins/jquery-1.8.3.min.js"></script>
        <link rel="stylesheet" href="../../resources/plugins/jtab/jtab.css ">
        <script src="../../resources/plugins/jtab/jtab.js"></script>
        <script src="../../resources/plugins/layer/layer.js"></script>
        <script src="../../resources/plugins/verify.js"></script>
        <script src="../../resources/js/oildepot/oildepotEdit.js"></script>
    </head>

    <body>
    <div class="container">
        <input type="hidden" id="mode" value="${mode}">
        <c:if test="${mode=='view'}">
            <div class="info-zone" style="height:540px">
                <div class="tab-title">
                    <div class="on">油库基本信息</div>
                    <div>读卡器信息</div>
                    <div>油库卡信息</div>
                </div>
                <div class="tab-con">
                    <div class="tab-con-list">
                        <table class="base-table">
                            <tr>
                                <td>油库ID:</td>
                                <td>
                                    <input type="text" class="editInfo" id="id" value="${oilDepot.id}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>油库编号:</td>
                                <td>
                                    <input type="text" class="editInfo" id="officialId" value="${oilDepot.officialId}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>油库名称:</td>
                                <td>
                                    <input type="text" class="editInfo" id="name" value="${oilDepot.name}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>油库简称:</td>
                                <td>
                                    <input type="text" class="editInfo" id="abbr" value="${oilDepot.abbr}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>负责人:</td>
                                <td>
                                    <input type="text" class="editInfo" id="director" value="${oilDepot.director}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>联系电话:</td>
                                <td>
                                    <input type="tel" class="editInfo" id="phone" value="${oilDepot.phone}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>联系地址:</td>
                                <td>
                                    <input type="text" class="editInfo" id="address" value="${oilDepot.address}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>所属公司:</td>
                                <td>
                                    <input type="text" class="editInfo" id="company" value="${oilDepot.company}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>经度:</td>
                                <td>
                                    <input type="text" class="editInfo" id="longitude" value="${oilDepot.longitude}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>纬度:</td>
                                <td>
                                    <input type="text" class="editInfo" id="latitude" value="${oilDepot.latitude}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>施解封半径:</td>
                                <td>
                                    <input type="text" class="editInfo" id="radius" value="${oilDepot.radius}" readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>占地范围:</td>
                                <td>
                                    <input type="text" class="editInfo" id="region" value="${oilDepot.coverRegion}"
                                           readonly>
                                </td>
                            </tr>
                            <tr>
                                <td>备注:</td>
                                <td>
                                    <input type="text" class="editInfo" id="remark" value="${oilDepot.remark}" readonly>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="tab-con-list">
                        <table class="sub-table">
                            <tr>
                                <td>序号</td>
                                <td>读卡器ID</td>
                                <td>读卡器类型</td>
                                <td>是否用于道闸转发通知</td>
                                <td>读卡器型号</td>
                            </tr>
                            <c:forEach var="reader" items="${readers}" varStatus="status">
                                <tr>
                                    <td>${status.index+1}</td>
                                    <td>${reader.devId}</td>
                                    <td>${reader.typeName}</td>
                                    <td>${reader.barrierName}</td>
                                    <td>${reader.model}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <div class="tab-con-list">
                        <table class="sub-table">
                            <tr>
                                <td>序号</td>
                                <td>卡类型</td>
                                <td>卡ID</td>
                            </tr>
                            <c:forEach var="card" items="${cards}" varStatus="status">
                                <tr>
                                    <td>${status.index+1}</td>
                                    <td>${card.typeName}</td>
                                    <td>${card.cardId}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
        <pop:Permission ename="editOildepot">
            <c:if test="${mode=='add'}">
                <div class="info-zone" style="height:480px">
                    <!-- <div class="tab-title">
                        <div class="on">油库基本信息</div>
                    </div> -->
                    <div class="tab-con">
                        <div class="tab-con-list">
                            <table class="base-table">
                                <tr>
                                    <td>油库编号:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="officialId" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>油库名称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="name" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>油库简称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="abbr" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>负责人:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="director">
                                    </td>
                                </tr>
                                <tr>
                                    <td>联系电话:</td>
                                    <td>
                                        <input type="tel" class="editInfo" id="phone">
                                    </td>
                                </tr>
                                <tr>
                                    <td>联系地址:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="address">
                                    </td>
                                </tr>
                                <tr>
                                    <td>所属公司:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="company">
                                    </td>
                                </tr>
                                <tr>
                                    <td>经度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="longitude" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>纬度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="latitude" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>施解封半径:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="radius" required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>占地范围:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="region">
                                    </td>
                                </tr>
                                <tr>
                                    <td>备注:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="remark">
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="oper-zone">
                    <input type="button" id="cancel" value="取消">
                    <input type="button" id="confirm" value="确认">
                </div>
            </c:if>
            <c:if test="${mode=='edit'}">
                <div class="info-zone" style="height:540px">
                    <div class="tab-title">
                        <div class="on">油库基本信息</div>
                        <div>读卡器信息</div>
                        <div>油库卡信息</div>
                    </div>
                    <div class="tab-con">
                        <div class="tab-con-list">
                            <table class="base-table">
                                <tr>
                                    <td>油库ID:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="id" value="${oilDepot.id}" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>油库编号:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="officialId"
                                               value="${oilDepot.officialId}" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>油库名称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="name" value="${oilDepot.name}" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>油库简称:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="abbr" value="${oilDepot.abbr}" readonly>
                                    </td>
                                </tr>
                                <tr>
                                    <td>负责人:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="director" value="${oilDepot.director}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>联系电话:</td>
                                    <td>
                                        <input type="tel" class="editInfo" id="phone" value="${oilDepot.phone}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>联系地址:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="address" value="${oilDepot.address}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>所属公司:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="company" value="${oilDepot.company}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>经度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="longitude" value="${oilDepot.longitude}"
                                               required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>纬度:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="latitude" value="${oilDepot.latitude}"
                                               required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>施解封半径:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="radius" value="${oilDepot.radius}"
                                               required>
                                    </td>
                                </tr>
                                <tr>
                                    <td>占地范围:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="region" value="${oilDepot.coverRegion}">
                                    </td>
                                </tr>
                                <tr>
                                    <td>备注:</td>
                                    <td>
                                        <input type="text" class="editInfo" id="remark" value="${oilDepot.remark}">
                                    </td>
                                </tr>
                            </table>
                        </div>
                        <div class="tab-con-list">
                            <input type="hidden" id="barrierCount" value="${barrierCount}">
                            <table class="sub-table" id="reader_info">
                                <tr>
                                    <td width="100px">读卡器ID</td>
                                    <td width="120px">读卡器类型</td>
                                    <td width="200px">是否用于道闸转发通知</td>
                                    <td width="60px">操作</td>
                                </tr>
                                <c:forEach var="reader" items="${readers}" varStatus="status">
                                    <tr class="readerTrs">
                                        <td>${reader.devId}</td>
                                        <td>
                                            <select>
                                                <c:if test="${reader.type==1}">
                                                    <option value=1 selected>入库读卡器</option>
                                                    <option value=2>出库读卡器</option>
                                                    <option value=3>出入库读卡器</option>
                                                </c:if>
                                                <c:if test="${reader.type==2}">
                                                    <option value=1>入库读卡器</option>
                                                    <option value=2 selected>出库读卡器</option>
                                                    <option value=3>出入库读卡器</option>
                                                </c:if>
                                                <c:if test="${reader.type==3}">
                                                    <option value=1>入库读卡器</option>
                                                    <option value=2>出库读卡器</option>
                                                    <option value=3 selected>出入库读卡器</option>
                                                </c:if>
                                            </select>
                                        </td>
                                        <td>
                                            <select>
                                                <c:if test="${reader.barrier==0}">
                                                    <option value=0 selected>未指定</option>
                                                    <option value=1>入库道闸</option>
                                                    <option value=2>出库道闸</option>
                                                    <option value=3>出入库道闸</option>
                                                </c:if>
                                                <c:if test="${reader.barrier==1}">
                                                    <option value=0>未指定</option>
                                                    <option value=1 selected>入库道闸</option>
                                                    <option value=2>出库道闸</option>
                                                    <option value=3>出入库道闸</option>
                                                </c:if>
                                                <c:if test="${reader.barrier==2}">
                                                    <option value=0>未指定</option>
                                                    <option value=1>入库道闸</option>
                                                    <option value=2 selected>出库道闸</option>
                                                    <option value=3>出入库道闸</option>
                                                </c:if>
                                                <c:if test="${reader.barrier==3}">
                                                    <option value=0>未指定</option>
                                                    <option value=1>入库道闸</option>
                                                    <option value=2>出库道闸</option>
                                                    <option value=3 selected>出入库道闸</option>
                                                </c:if>
                                            </select>
                                        </td>
                                        <td><img alt="删除" title="删除" src="../../resources/images/operate/delete.png"
                                                 onclick="deleteTr(this)"></td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td><img style="cursor: pointer;" alt="添加" title="添加"
                                             src="../../resources/images/operate/addNew.png" onclick="addReaderTr()">
                                    </td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </table>
                        </div>
                        <div class="tab-con-list">
                            <table class="sub-table" id="card_info">
                                <tr>
                                    <td width="100px">序号</td>
                                    <td width="150px">卡类型</td>
                                    <td width="120px">卡ID</td>
                                    <td width="100px">操作</td>
                                </tr>
                                <c:forEach var="card" items="${cards}" varStatus="status">
                                    <tr>
                                        <td class="serialNo">${status.index+1}</td>
                                        <td>${card.typeName}</td>
                                        <td class="cardIds">${card.cardId}</td>
                                        <td><img alt="删除" title="删除" src="../../resources/images/operate/delete.png"
                                                 onclick="deleteTr(this,1)"></td>
                                    </tr>
                                </c:forEach>
                                <tr>
                                    <td><img style="cursor: pointer;" alt="添加" title="添加"
                                             src="../../resources/images/operate/addNew.png" onclick="addTr()"></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
                <div class="oper-zone">
                    <input type="button" id="cancel" value="取消">
                    <input type="button" id="confirm" value="确认">
                </div>
            </c:if>
        </pop:Permission>
    </div>
    </body>

    </html>
</pop:Permission>