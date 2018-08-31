package com.tipray.constant;

/**
 * 操作Sqlite的SQL语句常量
 *
 * @author chenlong
 * @version 1.0 2018-08-23
 */
public class SqliteSqlConst {
    // 创建SQL
    /**
     * 应急卡创建SQL
     */
    public static final String CREATE_URGENT_CARD_SQL = new StringBuilder()
            .append("CREATE TABLE \"tbl_emergency_card\" (\n")
            .append("  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n")
            .append("  \"card_id\" integer NOT NULL,\n")
            .append("  \"owner_name\" text(20) NOT NULL\n")
            .append(");")
            .toString();
    /**
     * 管理卡创建SQL
     */
    public static final String CREATE_MANAGE_CARD_SQL = new StringBuilder()
            .append("CREATE TABLE \"tbl_management_card\" (\n")
            .append("  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n")
            .append("  \"card_id\" integer NOT NULL,\n")
            .append("  \"owner_name\" text(20) NOT NULL\n")
            .append(");")
            .toString();
    /**
     * 出入库卡创建SQL
     */
    public static final String CREATE_IN_OUT_CARD_SQL = new StringBuilder()
            .append("CREATE TABLE \"tbl_in_out_card\" (\n")
            .append("  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n")
            .append("  \"card_id\" integer NOT NULL,\n")
            .append("  \"type\" integer NOT NULL,\n")
            .append("  \"station_id\" integer NOT NULL,\n")
            .append("  \"owner_id\" text(50)")
            .append(");")
            .toString();
    /**
     * 出入库读卡器创建SQL
     */
    public static final String CREATE_IN_OUT_DEV_SQL = new StringBuilder()
            .append("CREATE TABLE \"tbl_in_out_dev\" (\n")
            .append("  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n")
            .append("  \"dev_id\" integer NOT NULL,\n")
            .append("  \"type\" integer NOT NULL,\n")
            .append("  \"station_id\" integer NOT NULL\n")
            .append(");")
            .toString();
    /**
     * 油库创建SQL
     */
    public static final String CREATE_OIL_DEPOT_SQL = new StringBuilder()
            .append("CREATE TABLE \"tbl_oildepot\" (")
            .append("  \"id\" integer NOT NULL PRIMARY KEY AUTOINCREMENT,")
            .append("  \"official_id\" integer NOT NULL,")
            .append("  \"name\" text(20) NOT NULL,")
            .append("  \"longitude\" real(4) NOT NULL,")
            .append("  \"latitude\" real(4) NOT NULL,")
            .append("  \"radius\" integer NOT NULL DEFAULT 0,")
            .append("  \"cover\" blob")
            .append(");")
            .toString();
    /**
     * 版本号创建SQL
     */
    public static final String CREATE_VERSION_SQL = new StringBuilder()
            .append("CREATE TABLE \"tbl_version\" (\n")
            .append("  \"version\" integer NOT NULL DEFAULT 0\n")
            .append(");")
            .toString();
    /**
     * 版本号初始化SQL
     */
    public static final String INIT_VERSION_SQL = "INSERT INTO tbl_version ( version ) VALUES ( 0 );";

    // 查询SQL
    /**
     * 应急卡查询SQL
     */
    public static final String QUERY_URGENT_CARD_SQL = new StringBuilder()
            .append("SELECT\n")
            .append("  \"card_id\" \"cardId\",\n")
            .append("  \"owner_name\" \"ownerName\"\n")
            .append("FROM\n")
            .append("  \"tbl_emergency_card\"\n")
            .append("ORDER BY\n")
            .append("  \"card_id\";")
            .toString();
    /**
     * 管理卡查询SQL
     */
    public static final String QUERY_MANAGE_CARD_SQL = new StringBuilder()
            .append("SELECT\n")
            .append("  \"card_id\" \"cardId\",\n")
            .append("  \"owner_name\" \"ownerName\"\n")
            .append("FROM\n")
            .append("  \"tbl_management_card\"\n")
            .append("ORDER BY\n")
            .append("  \"card_id\";")
            .toString();
    /**
     * 出入库卡查询SQL
     */
    public static final String QUERY_IN_OUT_CARD_SQL = new StringBuilder()
            .append("SELECT\n")
            .append("  \"card_id\" \"cardId\",\n")
            .append("  \"type\",\n")
            .append("  \"station_id\" \"stationId\",\n")
            .append("  \"owner_id\" \"ownerId\"\n")
            .append("FROM\n")
            .append("  \"tbl_in_out_card\"\n")
            .append("ORDER BY\n")
            .append("  \"card_id\";")
            .toString();
    /**
     * 出入库读卡器查询SQL
     */
    public static final String QUERY_IN_OUT_DEV_SQL = new StringBuilder()
            .append("SELECT\n")
            .append("  \"dev_id\" \"devId\",\n")
            .append("  \"type\",\n")
            .append("  \"station_id\" \"stationId\"\n")
            .append("FROM\n")
            .append("  \"tbl_in_out_dev\"\n")
            .append("ORDER BY\n")
            .append("  \"dev_id\";")
            .toString();
    /**
     * 油库查询SQL
     */
    public static final String QUERY_OIL_DEPOT_SQL = new StringBuilder()
            .append("SELECT\n")
            .append("  \"official_id\" \"id\",\n")
            .append("  \"name\",\n")
            .append("  \"longitude\",\n")
            .append("  \"latitude\",\n")
            .append("  \"radius\",\n")
            .append("  \"cover\"\n")
            .append("FROM\n")
            .append("  \"tbl_oildepot\"\n")
            .append("ORDER BY\n")
            .append("  \"official_id\";")
            .toString();
    /**
     * 版本号查询SQL
     */
    public static final String QUERY_VERSION_SQL = "SELECT \"version\" FROM \"tbl_version\";";

    // 插入SQL
    /**
     * 添加应急卡SQL
     */
    public static final String INSERT_URGENT_CARD_SQL = new StringBuilder()
            .append("INSERT INTO \"tbl_emergency_card\"(\"owner_name\", \"card_id\")\n")
            .append("VALUES(?, ?);")
            .toString();
    /**
     * 添加管理卡SQL
     */
    public static final String INSERT_MANAGE_CARD_SQL = new StringBuilder()
            .append("INSERT INTO \"tbl_management_card\"(\"owner_name\", \"card_id\")\n")
            .append("VALUES(?, ?);")
            .toString();
    /**
     * 添加出入库卡SQL
     */
    public static final String INSERT_IN_OUT_CARD_SQL = new StringBuilder()
            .append("INSERT INTO \"tbl_in_out_card\"(\"card_id\", \"type\", \"station_id\")\n")
            .append("VALUES(?, ?, ?);")
            .toString();
    /**
     * 添加出入库读卡器SQL
     */
    public static final String INSERT_IN_OUT_DEV_SQL = new StringBuilder()
            .append("INSERT INTO \"tbl_in_out_dev\"(\"dev_id\", \"type\", \"station_id\")\n")
            .append("VALUES(?, ?, ?);")
            .toString();
    /**
     * 添加油库SQL
     */
    public static final String INSERT_OIL_DEPOT_SQL = new StringBuilder()
            .append("INSERT INTO \"tbl_oildepot\"(\"official_id\", \"name\", \"longitude\", \"latitude\", \"radius\", \"cover\")\n")
            .append("VALUES(?, ?, ?, ?, ?, ?);")
            .toString();

    // 更新SQL
    /**
     * 版本号更新SQL
     */
    public static final String UPDATE_VERSION_SQL = "UPDATE \"tbl_version\" SET \"version\" = ?;";
    /**
     * 应急卡更新SQL
     */
    public static final String UPDATE_URGENT_CARD_SQL = new StringBuilder()
            .append("UPDATE \"tbl_emergency_card\" SET\n")
            .append("  \"owner_name\" = ?\n")
            .append("WHERE\n")
            .append("  \"card_id\" = ?;")
            .toString();
    /**
     * 管理卡更新SQL
     */
    public static final String UPDATE_MANAGE_CARD_SQL = new StringBuilder()
            .append("UPDATE \"tbl_management_card\" SET\n")
            .append("  \"owner_name\" = ?\n")
            .append("WHERE\n")
            .append("  \"card_id\" = ?;")
            .toString();
    /**
     * 出入库卡更新SQL
     */
    public static final String UPDATE_IN_OUT_CARD_SQL = new StringBuilder()
            .append("UPDATE \"tbl_in_out_card\" SET\n")
            .append("  owner_name = ?\n")
            .append("WHERE\n")
            .append("  \"card_id\" = ?;")
            .toString();
    /**
     * 油库更新SQL
     */
    public static final String UPDATE_OIL_DEPOT_SQL = new StringBuilder()
            .append("UPDATE \"tbl_oildepot\" SET\n")
            .append("  \"longitude\" = ?,\n")
            .append("  \"latitude\" = ?,\n")
            .append("  \"radius\" = ?,\n")
            .append("  \"cover\" = ?\n")
            .append("WHERE\n")
            .append("  \"official_id\" = ?;")
            .toString();

    // 删除SQL
    /**
     * 删除应急卡SQL
     */
    public static final String DELETE_URGENT_CARD_SQL = "DELETE FROM \"tbl_emergency_card\";";
    /**
     * 删除管理卡SQL
     */
    public static final String DELETE_MANAGE_CARD_SQL = "DELETE FROM \"tbl_management_card\";";
    /**
     * 删除出入库卡SQL
     */
    public static final String DELETE_IN_OUT_CARD_SQL = "DELETE FROM \"tbl_in_out_card\";";
    /**
     * 删除出入库读卡器SQL
     */
    public static final String DELETE_IN_OUT_DEV_SQL = "DELETE FROM \"tbl_in_out_dev\";";
    /**
     * 删除油库SQL
     */
    public static final String DELETE_OIL_DEPOT_SQL = "DELETE FROM \"tbl_oildepot\";";

    // 条件删除
    /**
     * 根据卡ID删除应急卡SQL
     */
    public static final String DELETE_URGENT_CARD_BY_CARD_ID_SQL = new StringBuilder()
            .append("DELETE FROM\n")
            .append("  \"tbl_emergency_card\"\n")
            .append("WHERE\n")
            .append("  \"card_id\" = ?;")
            .toString();
    /**
     * 根据卡ID删除管理卡SQL
     */
    public static final String DELETE_MANAGE_CARD_BY_CARD_ID_SQL = new StringBuilder()
            .append("DELETE FROM\n")
            .append("  \"tbl_management_card\"\n")
            .append("WHERE\n")
            .append("  \"card_id\" = ?;")
            .toString();
    /**
     * 根据卡ID删除出入库卡SQL
     */
    public static final String DELETE_IN_OUT_CARD_BY_CARD_ID_SQL = new StringBuilder()
            .append("DELETE FROM\n")
            .append("  \"tbl_in_out_card\"\n")
            .append("WHERE\n")
            .append("  \"card_id\" = ?;")
            .toString();
    /**
     * 根据站点ID删除出入库卡SQL
     */
    public static final String DELETE_IN_OUT_CARD_BY_STATION_ID_SQL = new StringBuilder()
            .append("DELETE FROM\n")
            .append("  \"tbl_in_out_card\"\n")
            .append("WHERE\n")
            .append("  \"station_id\" = ?;")
            .toString();
    /**
     * 根据站点ID删除出入库读卡器SQL
     */
    public static final String DELETE_IN_OUT_DEV_BY_STATION_ID_SQL = new StringBuilder()
            .append("DELETE FROM\n")
            .append("  \"tbl_in_out_dev\"\n")
            .append("WHERE\n")
            .append("  \"station_id\" = ?;")
            .toString();
    /**
     * 根据MySQL记录ID删除油库SQL
     */
    public static final String DELETE_OIL_DEPOT_BY_MYSQL_ID_SQL = new StringBuilder()
            .append("DELETE FROM\n")
            .append("  \"tbl_oildepot\"\n")
            .append("WHERE\n")
            .append("  \"official_id\" = ?;")
            .toString();
}