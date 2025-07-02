package io.cote.EasyChatDM;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EasyDMWeb {

    @GetMapping("/")
    public String index() {
        return "EasyChatDM running";
    }
}

