package com.jyd.es_demo2.entitry;

import java.util.List;

/**
 * @author ZT
 * @since 2023/1/12 11:24
 */
public class Xkx {

    private Integer id;

    /**
     * 父id
     */
    private Integer parentId;

    private String name;

    /**
     * 子节点
     */
    private List<Xkx> child;

    private Integer money;

    public Xkx(Integer id, Integer parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public Xkx(Integer id, Integer parentId, String name, Integer money) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.money = money;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Xkx> getChild() {
        return child;
    }

    public void setChild(List<Xkx> child) {
        this.child = child;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\" : " + id +
                ", \"parentId\" : " + parentId +
                ", \"name\" : \"" + name + '\"' +
                ", \"child\" : " + child +
                '}';
    }
}
