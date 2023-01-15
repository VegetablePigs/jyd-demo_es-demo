package com.jyd.es_demo2.entitry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        // 如果parentId=0  即是根节点
        Xkx root1 = new Xkx(1, 0, "小欣爸爸", 100);
        Xkx root2 = new Xkx(2, 0, "小通爸爸", 100);
        Xkx root3 = new Xkx(3, 0, "小星爸爸", 200);
        Xkx root1Child1 = new Xkx(5, 1, "小欣", 500);
        Xkx root1Child2 = new Xkx(6, 1, "小欣弟弟", 100);
        Xkx root2Child = new Xkx(7, 2, "小通", 400);
        Xkx root1Child1Child = new Xkx(8, 5, "小欣的孩子", 50);
        Xkx root1Child1Child2 = new Xkx(9, 8, "小欣的孩子的孩子", 100);

        List<Xkx> list = Arrays.asList(root1, root2, root3, root1Child1, root1Child2, root2Child, root1Child1Child,root1Child1Child2);
        // 原始结果
//        System.out.println(list);
        // 调用方法
        recursion(list);
    }


    private static void recursion(List<Xkx> list) {
        // 查找符合查询条件的集合
        List<Xkx> rootList = list.stream().filter(xkx -> xkx.getParentId() == 0).collect(Collectors.toList());
        // 循环list，获取list中的指定数据
        rootList.forEach(root -> {
            buildChild(list, root);
        });
        // 树形结果
//        System.out.println(rootList);

        // 计算家庭钱总数
        Integer moneyCount = 0;
        for (Xkx xkx : rootList) {
            moneyCount = xkx.getMoney();
            moneyCount = familyMoneyTotal(xkx,moneyCount);
            System.out.println(xkx.getName()+"家钱总数："+ moneyCount);
        }

        // 计算元素之和
//        int sum = list.stream().mapToInt(Xkx::getMoney).sum();
//        System.out.println(sum);
        rootList.forEach(root -> System.out.println(root.getName() + "家有：" + getMoney(root)));
    }
    // stream流
    private static Integer getMoney(Xkx root) {
        // 如果list集合不为空，将list集合赋值给newList,mapToInt返回一个IntStream
        return root.getMoney() + Optional.ofNullable(root.getChild()).orElseGet(ArrayList::new).stream().mapToInt(Test::getMoney).sum();
    }

    // 计算家庭钱总数 常规
    private static Integer familyMoneyTotal(Xkx xkx, Integer moneyCount) {
        if (xkx.getChild() != null && xkx.getChild().size() > 0){
            for (Xkx xkx1 : xkx.getChild()) {
                moneyCount += xkx1.getMoney();
                moneyCount = familyMoneyTotal(xkx1,moneyCount);
            }
        }
        return moneyCount;
    }

    private static void buildChild(List<Xkx> list, Xkx root) {
        List<Xkx> children = list.stream().filter(xkx -> xkx.getParentId().equals(root.getId())).collect(Collectors.toList());
        if (!children.isEmpty()) {
            for (Xkx child : children) {
                buildChild(list, child);
            }
            root.setChild(children);
        }
    }
}