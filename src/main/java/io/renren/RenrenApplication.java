package io.renren;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@MapperScan("io.renren.dao")
public class RenrenApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(RenrenApplication.class).run(args);
    }
}
