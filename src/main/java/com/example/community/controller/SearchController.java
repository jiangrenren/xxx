package com.example.community.controller;

import com.example.community.entity.DiscussPost;
import com.example.community.entity.Page;
import com.example.community.service.ElasticsearchService;
import com.example.community.service.LikeService;
import com.example.community.service.UserService;
import com.example.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    /**
     * Search string.
     * <p>
     * 路径： /search?keyword=xxx
     *
     * @param keyword the keyword
     * @param page    the page
     * @param model   the model
     * @return the string
     */
    @GetMapping("/search")
    public String search(String keyword, @Validated Page page, Model model) {
        // 搜索帖子
        Map<String, Object> searchResult = null;
        try {
            searchResult = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        } catch (IOException e) {
            logger.error("es搜索失败：", e.getMessage());
        }
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null && !searchResult.isEmpty()) {
            List<DiscussPost> list = (List<DiscussPost>) searchResult.get("list");
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);
        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        long total = (long) searchResult.get("total");
        page.setRows(searchResult == null ? 0 : (int) total);

        return "site/search";
    }
}
