package work.init.game.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import work.init.game.Services.SessionService;
import work.init.game.Messages.LoginBody;
import work.init.game.Messages.LoginResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller
public class HomeController {

    @Autowired
    private SessionService service;

    @GetMapping("/index")
    public String getHome(){
        return "/index.html";
    }

    @PostMapping("/login")
    @ResponseBody
    public LoginResponse handleLogin(@RequestBody LoginBody login, HttpServletResponse response)
            throws IOException, NoSuchAlgorithmException {
        //getting SHA-256 hash of a username
        MessageDigest msg = MessageDigest.getInstance("SHA-256");
        byte[] hash = msg.digest(login.getUsername().getBytes(StandardCharsets.UTF_8));
        StringBuilder s = new StringBuilder();
        for (byte b : hash) {
            s.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        //Token = username's hash, hence every session with the same username will have the same token
        String token = s.toString();
        LoginResponse result = new LoginResponse();
        result.setToken(token);
        System.out.printf("token: %s%n", result.getToken());
        service.addSession(token, login.getUsername());
        return result;
    }
}
