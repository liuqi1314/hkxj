
package cn.hkxj.platform.config;


import de.codecentric.boot.admin.server.config.AdminServerMarkerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouqinglai
 * @version version
 * @title BasicsConfig
 * @desc  基础配置
 * @date: 2019年05月03日
 */
@Configuration
public class BasicsConfig {

    @Bean
    public AdminServerMarkerConfiguration.Marker adminServerMarker() {
        return new AdminServerMarkerConfiguration.Marker();
    }
}