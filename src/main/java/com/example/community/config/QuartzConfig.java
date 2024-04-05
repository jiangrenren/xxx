package com.example.community.config;

import com.example.community.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    /**
     * BeanFactory是IOC容器的顶层接口
     * FactoryBean可简化Bean的实例化过程，工厂模式
     *  1. 通过FactoryBean封装Bean的实例化过程
     *  2. 将FactoryBean装配到Spring容器中
     *  3. 将FactoryBean注入给其他的Bean
     *  4. 该Bean得到的是FactoryBean所管理的对象实例
     */

    /**
     * 配置JobDetail
     * @return
     */
    /**
     * 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     * @return
     */

    // 刷新帖子分数任务
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 触发器
    @Bean
    public SimpleTriggerFactoryBean  postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
