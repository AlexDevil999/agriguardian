package com.agriguardian.controller.app;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;


@RestController
@RequestMapping(value = "/health")
@Log4j2
@RequiredArgsConstructor
public class HealthCheckController {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    @Value("${tcp.ip}")
    private String ip;
    private static final String date = new Date().toString();

    @GetMapping("/check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/checkTCP")
    public String TCPCheck() {
        try {
            GreetClient client = new GreetClient();
            client.startConnection(ip, 8088);
            String response = client.sendMessage("tcp is working");
            client.stopConnection();
            return response;
        }
        catch (Exception e){
            log.error("tcp is not working: " + e.getMessage());
            return "tcp is not working";
        }
    }

    @RequestMapping("/date")
    public ResponseEntity<String> getInitialDate() {
        log.debug("[getInitialDate] request of date");
        return ResponseEntity.ok(date);
    }

    @GetMapping("/version")
    public ResponseEntity<String> version() {
        return ResponseEntity.ok("07.12");
    }


    class GreetClient {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public void startConnection(String ip, int port) throws IOException {
            clientSocket = new Socket(ip, port);
            clientSocket.setSoTimeout(10000);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public String sendMessage(String msg) throws IOException {
            out.println(msg);
            String resp = in.readLine();
            return resp;
        }

        public void stopConnection() throws IOException {
            in.close();
            out.close();
            clientSocket.close();
        }
    }
}
