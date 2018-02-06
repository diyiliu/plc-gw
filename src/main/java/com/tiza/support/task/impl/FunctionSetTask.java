package com.tiza.support.task.impl;

import com.tiza.support.cache.ICache;
import com.tiza.support.model.CanPackage;
import com.tiza.support.model.NodeItem;
import com.tiza.support.task.ITask;
import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: FunctionSetTask
 * Author: DIYILIU
 * Update: 2018-02-06 09:15
 */

public class FunctionSetTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ICache functionSetCache;

    public FunctionSetTask(ICache functionSetCache) {
        this.functionSetCache = functionSetCache;
    }

    @Override
    public void execute() {
        String filePath = "plc-functionSet.xml";
        Resource resource = new ClassPathResource(filePath);

        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(resource.getFile());

            dealCan(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealCan(Document document) {
        List<Node> rootPackageNodes = document.selectNodes("modbus/read/address");

        Map<String, CanPackage> canPackages = new HashedMap();
        for (Node node : rootPackageNodes) {
            CanPackage canPackage = dealPackage(node);
            canPackages.put(canPackage.getPackageId(), canPackage);
        }

        functionSetCache.put(canPackages);
    }

    private CanPackage dealPackage(Node packageNode) {
        String packageId = packageNode.valueOf("@function");
        int length = Integer.parseInt(packageNode.valueOf("@length"));

        CanPackage canPackage = new CanPackage(packageId, length);
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
            Node fieldNode =  itemNode.selectSingleNode("field");
            if (fieldNode != null){
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
            if (expNode != null){
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
