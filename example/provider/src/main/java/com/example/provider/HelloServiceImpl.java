package com.example.provider;

import annotation.RpcService;

@RpcService(value = "HelloService",version = "1")
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String name) {
        return "hello " + name;
    }

}

