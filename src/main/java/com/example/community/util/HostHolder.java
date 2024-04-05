package com.example.community.util;

import com.example.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    // 已线程为单位存取值，把user存入当前线程，可供本次请求随时获取。
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
