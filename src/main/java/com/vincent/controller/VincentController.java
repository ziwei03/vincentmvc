package com.vincent.controller;

import com.vincent.mvcframework.annotation.*;
import com.vincent.service.IVincentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@VController
@VRequestMapping("/vincent")
public class VincentController {

    @VAutowired()
    private IVincentService vincentService;

    @VRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @VRequestParam("name") String name, @VRequestParam("age") String age) {
        PrintWriter pw;
        try {
            pw = response.getWriter();
            String result = vincentService.query(name, age);
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @VRequestMapping("/add")
    public void add(HttpServletRequest request, HttpServletResponse response, @VRequestParam("id") Integer id) {
        PrintWriter pw;
        try {
            pw = response.getWriter();
            String result = vincentService.insert(String.valueOf(id));
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @VRequestMapping("/update")
    public void update(HttpServletRequest request, HttpServletResponse response, @VRequestParam("id") Integer id) {
        PrintWriter pw;
        try {
            pw = response.getWriter();
            String result = vincentService.update(String.valueOf(id));
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
