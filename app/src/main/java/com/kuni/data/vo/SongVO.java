package com.kuni.data.vo;

import java.io.Serializable;

/**
 * VO名：SongVO 描述：Song 表名：t_song DB：C:\Users\Kw\Desktop\BookStore.db
 * 时间：2017/01/13 16:19:08
 */
public class SongVO implements Serializable {
    private static final long serialVersionUID = 212433467062029902L;

    // VO私有成员定义
    private Integer _id;
    private String vname;
    private String vsinger;
    private Integer iduration;
    private String vlocation;
    private String valbumArt;// 专辑封面路径

    // VO对应数据库字段名定义
    public static final String _ID = "_id";
    public static final String VNAME = "vname";
    public static final String VSINGER = "vsinger";
    public static final String IDURATION = "iduration";
    public static final String VLOCATION = "vlocation";

    /**
     * 建表语句
     */
    public static final String SQL_CREATE_TABLE = "create table t_song("
            + "_id integer primary key autoincrement," + "vname text,"
            + "vsinger text," + "iduration integer," + "vlocation text" + ")";

    /**
     * vo构造函数
     */
    public SongVO() {
    }

    /**
     * vo构造函数
     *
     * @param _id      _id
     * @param name     歌名
     * @param singer   歌手
     * @param duration 时长（秒）
     * @param location 路径
     */
    public SongVO(Integer _id, String name, String singer, Integer duration,
                  String location) {
        this._id = _id;
        this.vname = name;
        this.vsinger = singer;
        this.iduration = duration;
        this.vlocation = location;
    }

    /**
     * vo构造函数
     *
     * @param _id      _id
     * @param name     歌名
     * @param singer   歌手
     * @param duration 时长（秒）
     */
    public SongVO(Integer _id, String name, String singer, Integer duration) {
        this._id = _id;
        this.vname = name;
        this.vsinger = singer;
        this.iduration = duration;
        this.vlocation = "Not Find";
    }

    /**
     * vo赋值方法
     *
     * @param _id      _id
     * @param name     歌名
     * @param singer   歌手
     * @param duration 时长（秒）
     * @param location 路径
     */
    public void setValue(Integer _id, String name, String singer,
                         Integer duration, String location) {
        this._id = _id;
        this.vname = name;
        this.vsinger = singer;
        this.iduration = duration;
        this.vlocation = location;
    }

    /**
     * 获取当前vo的副本
     *
     * @return 当前vo的副本
     */
    public SongVO clone() {
        SongVO copyvo = new SongVO();
        copyvo.setValue(this._id, this.vname, this.vsinger, this.iduration,
                this.vlocation);
        return copyvo;
    }

    /**
     * 获取VO名称
     *
     * @return String
     */
    static public String getVoName() {
        return "SongVO";
    }

    /**
     * 获取VO对应的数据表名
     *
     * @return String
     */
    static public String getTableName() {
        return "t_song";
    }

    /**
     * 获取VO的显示名称
     *
     * @return String
     */
    static public String getEntityName() {
        return "Song";
    }

    /**
     * 获取VO所有字段
     *
     * @return String
     */
    static public String getAllFieldName() {
        return "_id,vname,vsinger,iduration";
    }

    /**
     * 获取VO主键字段
     *
     * @return String
     */
    static public String getPKFieldName() {
        return _ID;
    }

    /**
     * 获取VO主键PK
     *
     * @return String
     */
    public Integer getPK() {
        return this._id;
    }

    /**
     * 输出对象
     *
     * @return String
     */
    @Override
    public String toString() {
        // StringBuilder sb = new StringBuilder();
        // sb.append(String.format("_id:%d;", this._id));
        // sb.append(String.format("vname:%s;", this.vname));
        // sb.append(String.format("vsinger:%s;", this.vsinger));
        // sb.append(String.format("iduration:%d;", this.iduration));
        // return sb.toString();
        return String.format("%s-%s", this.vname, this.vsinger);
    }

    @Override
    public int hashCode() {
        // 重写hashcode，equals，用于在容器中的比较
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        // 重写hashcode，equals，用于在容器中的比较
        return this.hashCode() == o.hashCode();
    }

    /**
     * 获取 _id(_id)
     *
     * @return Integer
     */
    public Integer getId() {
        return this._id;
    }

    /**
     * 设置 _id(_id)
     *
     * @para _id Integer
     */
    public void setId(Integer id) {
        this._id = id;
    }

    /**
     * 获取 vname(vname)
     *
     * @return String
     */
    public String getName() {
        return this.vname;
    }

    /**
     * 设置 vname(vname)
     *
     * @para name String
     */
    public void setName(String name) {
        this.vname = name;
    }

    /**
     * 获取 vsinger(vsinger)
     *
     * @return String
     */
    public String getSinger() {
        return this.vsinger;
    }

    /**
     * 设置 vsinger(vsinger)
     *
     * @para singer String
     */
    public void setSinger(String singer) {
        this.vsinger = singer;
    }

    /**
     * 获取 iduration(iduration)
     *
     * @return Integer
     */
    public Integer getDuration() {
        return this.iduration;
    }

    /**
     * 设置 iduration(iduration)
     *
     * @para duration Integer
     */
    public void setDuration(Integer duration) {
        this.iduration = duration;
    }

    /**
     * 获取 vlocation(vlocation)
     *
     * @return
     */
    public String getLocation() {
        return vlocation;
    }

    /**
     * 设置 location(location)
     *
     * @param location
     */
    public void setLocation(String location) {
        this.vlocation = location;
    }

    public String getAlbumArt() {
        return valbumArt;
    }

    public void setAlbumArt(String valbumArt) {
        this.valbumArt = valbumArt;
    }

}
