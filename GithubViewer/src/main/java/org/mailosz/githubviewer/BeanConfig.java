package org.mailosz.githubviewer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BeanConfig {

    @Bean
    public RestClient restClient(@Value("${github.api.default-url}") String defaultUrl, RestClient.Builder builder ){
        return builder
                .baseUrl(defaultUrl)
                .defaultHeader("X-GitHub-Api-Version","2022-11-28")
                .build();
    }
}
