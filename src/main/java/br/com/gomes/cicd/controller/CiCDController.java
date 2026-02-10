package br.com.gomes.cicd.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/cicd/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class CiCDController {

    private final Environment environment;

    @Autowired
    public CiCDController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("entries")
    public Map<String, Object> entrypoint(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        String podIp = request.getLocalAddr();
        int localPort = request.getLocalPort();
        String serverPort = environment.getProperty("server.port");

        response.put("podIp", podIp);
        response.put("localPort", localPort);
        response.put("serverPort", serverPort == null ? "Nao informado" : serverPort);
        response.put("CICD", "Jenkins + ArgoCD + K8S");

        return response;
    }
}