package kr.co.ucomp.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private String redisPort;

  @Value("${spring.data.redis.password}")
  private String redisPassword;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration = 
    	new RedisStandaloneConfiguration();
        
    redisStandaloneConfiguration.setHostName(redisHost);
    redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
    redisStandaloneConfiguration.setPassword(redisPassword);
    
    LettuceConnectionFactory lettuceConnectionFactory = 
    	new LettuceConnectionFactory(redisStandaloneConfiguration);
        
    return lettuceConnectionFactory;
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
	  RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(redisConnectionFactory);
      
      // Jackson2JsonRedisSerializer를 사용하여 세션 직렬화 설정
      Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
      
      redisTemplate.setDefaultSerializer(serializer);
      redisTemplate.setKeySerializer(RedisSerializer.string());
      redisTemplate.setValueSerializer(serializer);
      redisTemplate.setHashKeySerializer(RedisSerializer.string());
      redisTemplate.setHashValueSerializer(serializer);
    return redisTemplate;
  }

}