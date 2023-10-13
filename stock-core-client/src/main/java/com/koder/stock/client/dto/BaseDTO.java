package com.koder.stock.client.dto;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseDTO implements Serializable {

    private Map<String, String> features = new HashMap<>();

    public String get(String key) {
        if(features!=null) {
            return features.get(key);
        }
        return null;
    }

    public String putIfAbsent(String key, String value) {
        if(features==null) {
            features = new HashMap<>();
        }
        return features.putIfAbsent(key, value);
    }

    public Map<String, String> getFeatures() {
        return this.features;
    }

    public void addFeatures(Map<String, String> features) {
        if(features==null){
            return;
        }
        this.features.putAll(features);
    }
}
