package org.fhi360.module.Hts.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@RestController
public class ViewController {
    @Autowired
    ResourceLoader resourceLoader;

    @RequestMapping("/home")
    public String home() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/home.html");
        InputStream inputStream = resource.getInputStream();
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        String data = new String(bdata, StandardCharsets.UTF_8);
        return data;
    }
    @RequestMapping("/download")
    public String download() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/download.html");
        InputStream inputStream = resource.getInputStream();
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        String data = new String(bdata, StandardCharsets.UTF_8);
        return data;
    }

}
