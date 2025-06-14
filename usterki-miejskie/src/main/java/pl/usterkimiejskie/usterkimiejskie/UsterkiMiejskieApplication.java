package pl.usterkimiejskie.usterkimiejskie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UsterkiMiejskieApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsterkiMiejskieApplication.class, args);
    }

}
