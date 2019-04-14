package com.vincent.service.impl;

import com.vincent.mvcframework.annotation.VService;
import com.vincent.service.IVincentService;

@VService("vincentServiceImpl")
public class VincentServiceImpl implements IVincentService {
    @Override
    public String query(String name, String age) {
        return "name = " + name + "; age = " + age;
    }

    @Override
    public String insert(String param) {
        return "insert successful.....";
    }

    @Override
    public String update(String param) {
        return "update successful......";
    }
}
