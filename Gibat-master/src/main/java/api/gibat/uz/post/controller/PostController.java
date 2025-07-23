package api.gibat.uz.post.controller;

import api.gibat.uz.post.dto.PostDTO;
import api.gibat.uz.security.util.SpringSecurityUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/post")
public class PostController {
    @PostMapping("/create")
    public String createPost(@RequestBody PostDTO dto){
        System.out.println(SpringSecurityUtil.getCurrentUserId());
        System.out.println(SpringSecurityUtil.getCurrentProfile());
        return "create post";
    }
}
