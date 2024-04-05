package com.example.community.controller;

import com.example.community.annotation.LoginRequired;
import com.example.community.entity.Comment;
import com.example.community.entity.DiscussPost;
import com.example.community.entity.Event;
import com.example.community.event.EventProducer;
import com.example.community.service.CommentService;
import com.example.community.service.DiscussPostService;
import com.example.community.util.CommunityConstant;
import com.example.community.util.HostHolder;
import com.example.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Add comment string.
     * 发布评论
     *
     * @param discussPostId the discuss post id
     * @param comment       the comment
     * @return the string
     */
    @PostMapping("/add/{discussPostId}")
    @LoginRequired
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        int rows = commentService.addComment(comment);
        if (rows == 1) {
            // 触发评论事件
            Event event = new Event()
                    .setTopic(TOPIC_COMMENT)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(comment.getEntityType())
                    .setEntityId(comment.getEntityId())
                    .setData("postId", discussPostId);
            // 根据实体类型的不同，获取不同的作者
            if (comment.getEntityType() == ENTITY_TYPE_POST) {
                DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
                Comment target = commentService.findCommentById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }
            // 发布评论事件(Kafka是异步的，程序不会阻塞在这里，会继续向下运行，评论发布在另一个线程中实现)
            eventProducer.fireEvent(event);

            if (comment.getEntityType() == ENTITY_TYPE_POST) {
                // 触发发帖事件（修改帖子）
                event = new Event()
                        .setTopic(TOPIC_PUBLISH)
                        .setUserId(comment.getUserId())
                        .setEntityType(ENTITY_TYPE_POST)
                        .setEntityId(discussPostId);
                eventProducer.fireEvent(event);

                // 计算帖子分数
                String redisKey = RedisKeyUtil.getPostScoreKey();
                redisTemplate.opsForSet().add(redisKey, discussPostId);
            }

            // 返回帖子列表
            return "redirect:/discuss/detail/" + discussPostId;
        } else {
            return "site/error/500";
        }
    }
}
