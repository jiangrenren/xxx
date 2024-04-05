package com.example.community.config;

import org.springframework.context.annotation.Bean;

import java.text.SimpleDateFormat;

//@Configuration
public class AlphaConfig {
    @Bean
    public SimpleDateFormat simpleDateFormat(){
        return new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
    }
}
