package com.example.consumer;

import annotation.Reference;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.consumer.HelloService;

@RestController
public class HelloController {

    @Reference(value = "HelloService",version = "1")
    private HelloService helloService;

    @GetMapping("/hello/sayHello")
    public String sayHello(@RequestParam(defaultValue = "lin") String name){
        StopWatch stopwatch = new StopWatch();
        stopwatch.start();
        String returnString= helloService.say(name);
        stopwatch.stop();
        return returnString;
    }

}