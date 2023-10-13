package com.koder.stock.coreservice.util;

import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

import java.util.*;

public class CollectionUtil {

    public static <T> List<List<T>> splitCollections(List<T> collection, int size) {
        List<List<T>> result = new ArrayList<List<T>>();
        if (collection == null || collection.size() <= size) {
            result.add(collection);
            return result;
        }
        //1245 , 1000
        int collectionSize = collection.size();
        int count = (collectionSize + size - 1) / size;
        for (int i = 0; i < count; i++) {
            int fromIndex = i * size;
            int toIndex = (i + 1) * size;
            if (toIndex > collectionSize) {
                toIndex = collectionSize;
            }
            List<T> subList = collection.subList(fromIndex, toIndex);
            result.add(subList);
        }
        return result;
    }

    public static Map<String,String> convertFeaturesMap(String features){
        Map<String,String> retMap = new HashMap<>();
        if (StringUtils.isEmpty(features)) {
            return retMap;
        }
        retMap = JSON.parseObject(features, HashMap.class);
        return retMap;
    }




}
