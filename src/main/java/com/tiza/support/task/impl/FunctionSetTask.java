package com.tiza.support.task.impl;

import com.tiza.support.cache.ICache;
import com.tiza.support.dao.FunctionSetDao;
import com.tiza.support.model.CanPackage;
import com.tiza.support.model.FunctionInfo;
import com.tiza.support.model.NodeItem;
import com.tiza.support.task.ITask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Description: FunctionSetTask
 * Author: DIYILIU
 * Update: 2018-02-06 09:15
 */

public class FunctionSetTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ICache functionSetCache;
    private FunctionSetDao functionSetDao;

    public FunctionSetTask(FunctionSetDao functionSetDao, ICache functionSetCache) {
        this.functionSetDao = functionSetDao;
        this.functionSetCache = functionSetCache;
    }

    @Override
    public void execute() {
        logger.info("刷新功能集列表...");
        try {
            List<FunctionInfo> infoList = functionSetDao.selectFunctionInfo();
            for (FunctionInfo info : infoList) {
                String functionXml = info.getFunctionXml();

                Document document = DocumentHelper.parseText(functionXml);
                Map canMap = dealCan(document);
                info.setCanPackages(canMap);
            }

            refresh(infoList, functionSetCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh(List<FunctionInfo> infoList, ICache functionCache) {
        if (infoList == null || infoList.size() < 1){
            logger.warn("无功能集!");
            return;
        }

        Set oldKeys = functionCache.getKeys();
        Set tempKeys = new HashSet(infoList.size());

        for (FunctionInfo info : infoList) {
            functionCache.put(info.getSoftVersion(), info);
            tempKeys.add(info.getSoftVersion());
        }

        Collection subKeys = CollectionUtils.subtract(oldKeys, tempKeys);
        for (Iterator iterator = subKeys.iterator(); iterator.hasNext();){
            String key = (String) iterator.next();
            functionCache.remove(key);
        }
    }


    private Map<String, CanPackage> dealCan(Document document) {
        List<Node> rootPackageNodes = document.selectNodes("modbus/address");

        Map<String, CanPackage> canPackages = new HashedMap();
        for (Node node : rootPackageNodes) {
            CanPackage canPackage = dealPackage(node);
            canPackages.put(canPackage.getPackageId(), canPackage);
        }

        return canPackages;
    }

    private CanPackage dealPackage(Node packageNode) {
        String packageId = packageNode.valueOf("@function");
        int length = Integer.parseInt(packageNode.valueOf("@length"));
        int period = Integer.parseInt(packageNode.valueOf("@frequency"));

        CanPackage canPackage = new CanPackage(packageId, length);
        canPackage.setPeriod(period);
        List<Node> nodeItems = packageNode.selectNodes("point");

        if (nodeItems != null && nodeItems.size() > 0) {
            List<NodeItem> itemList = new ArrayList();
            for (Node node : nodeItems) {
                NodeItem nodeItem = dealItem(node);
                if (nodeItem != null) {
                    itemList.add(nodeItem);
                }
            }
            canPackage.setItemList(itemList);
        }

        return canPackage;
    }

    private NodeItem dealItem(Node itemNode) {
        NodeItem itemBean = null;
        try {
            String nameKey = itemNode.selectSingleNode("tag").getText();

            String field = nameKey;
            Node fieldNode = itemNode.selectSingleNode("field");
            if (fieldNode != null) {
                field = fieldNode.getText();
            }

            String name = itemNode.selectSingleNode("name").getText();
            String type = itemNode.selectSingleNode("type").getText();
            String endian = itemNode.selectSingleNode("endian") == null ? "big" : itemNode.selectSingleNode("endian").getText();
            Node position = itemNode.selectSingleNode("parse/position");
            Node byteNode = position.selectSingleNode("byte");
            Node bitNode = byteNode.selectSingleNode("bit");
            String byteStart = byteNode.valueOf("@offset");
            String byteLen = byteNode.valueOf("@length");
            Node expNode = itemNode.selectSingleNode("parse/formula");

            itemBean = new NodeItem();
            if (null == bitNode) {
                itemBean.setOnlyByte(true);
            } else {
                itemBean.setOnlyByte(false);
                String bitStart = bitNode.valueOf("@offset");
                String bitLen = bitNode.valueOf("@length");
                itemBean.setBitStart(Integer.parseInt(bitStart));
                itemBean.setBitLen(Integer.parseInt(bitLen));
            }

            // 数值表达式
            if (expNode != null) {
                itemBean.setExpression(expNode.getText());
            }

            itemBean.setNameKey(nameKey);
            itemBean.setName(name);
            itemBean.setType(type);
            itemBean.setEndian(endian);
            itemBean.setByteStart(Integer.parseInt(byteStart));
            itemBean.setByteLen(Integer.parseInt(byteLen));
            itemBean.setField(field);
        } catch (Exception e) {
            logger.error("解析功能集错误![{}]", e.getMessage());
            e.printStackTrace();
        }

        return itemBean;
    }
}
