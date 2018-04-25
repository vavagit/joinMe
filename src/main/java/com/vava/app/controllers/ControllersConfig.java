package com.vava.app.controllers;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages= {"com.vava.app.services", "com.vava.app.model.communication"})
public class ControllersConfig {
}
