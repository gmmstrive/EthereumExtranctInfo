package com.gikee.ethereum.config;

import com.gikee.ethereum.utils.tuple.TwoTuple;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lucas
 * @Date 2019.03.25
 */
@Configuration
public class MongoConfig {

    public TwoTuple<List<ServerAddress>, MongoClientOptions> getMongoSettings(MongoSettingsProperties properties) {

        // 客户端配置（连接数，副本集群验证）
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(properties.getMaxConnectionsPerHost());
        builder.minConnectionsPerHost(properties.getMinConnectionsPerHost());
        builder.threadsAllowedToBlockForConnectionMultiplier(properties.getThreadsAllowedToBlockForConnectionMultiplier());
        builder.serverSelectionTimeout(properties.getServerSelectionTimeout());
        builder.maxWaitTime(properties.getMaxWaitTime());
        builder.maxConnectionIdleTime(properties.getMaxConnectionIdelTime());
        builder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime());
        builder.connectTimeout(properties.getConnectTimeout());
        builder.socketTimeout(properties.getSocketTimeout());
        builder.sslEnabled(properties.getSslEnabled());
        builder.sslInvalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
        builder.alwaysUseMBeans(properties.getAlwaysUseMBeans());
        builder.heartbeatFrequency(properties.getHeartbeatFrequency());
        builder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency());
        builder.heartbeatConnectTimeout(properties.getHeartbeatConnectTimeout());
        builder.heartbeatSocketTimeout(properties.getHeartbeatSocketTimeout());
        MongoClientOptions mongoClientOptions = builder.build();

        // MongoDB地址列表
        List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
        for (String address : properties.getAddress()) {
            String[] hostAndPort = address.split(":");
            String host = hostAndPort[0];
            Integer port = Integer.parseInt(hostAndPort[1]);
            ServerAddress serverAddress = new ServerAddress(host, port);
            serverAddresses.add(serverAddress);
        }

        return new TwoTuple<>(serverAddresses, mongoClientOptions);
    }

    @Bean
    @Autowired
    public MongoDbFactory getMongoDbFactory(MongoSettingsProperties properties) {
        TwoTuple<List<ServerAddress>, MongoClientOptions> mongoSettings = getMongoSettings(properties);
        // 创建非认证客户端
        MongoClient mongoClient = new MongoClient(mongoSettings.getFirst(), mongoSettings.getSecond());
        // 创建MongoDbFactory
        return new SimpleMongoDbFactory(mongoClient, properties.getDatabase());

    }

    @Bean
    @Autowired
    public MongoDbFactory getMongoDbFactoryAuth(MongoSettingsProperties properties) {
        MongoClient mongoClient;
        TwoTuple<List<ServerAddress>, MongoClientOptions> mongoSettings = getMongoSettings(properties);
        //连接认证
        MongoCredential mongoCredential;
        if (properties.getUsername() != null) {
            mongoCredential = MongoCredential.createScramSha1Credential(
                    properties.getUsername(), properties.getAuthenticationDatabase() != null
                            ? properties.getAuthenticationDatabase() : properties.getDatabase(),
                    properties.getPassword().toCharArray());
            //创建认证客户端
            mongoClient = new MongoClient(mongoSettings.getFirst(), mongoCredential, mongoSettings.getSecond());
            // 创建MongoDbFactory
            return new SimpleMongoDbFactory(mongoClient, properties.getDatabase());
        }
        return null;
    }

    @Configuration
    public class MongoTemplateConfig {

        @Autowired
        //@Qualifier("getMongoDbFactoryAuth")
        @Qualifier("getMongoDbFactory")
        private MongoDbFactory mongoDbFactory;

        @Autowired
        private MongoMappingContext mongoMappingContext;

        /**
         * 设置 MongoTemplate TypeMapper 为 null , 这样保存在 mongo 中的 Collection 就不会有 _class 列
         *
         * @return
         */
        @Bean
        public MongoTemplate getMongoTemplate() {
            DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
            MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
            mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
            return new MongoTemplate(mongoDbFactory, mappingMongoConverter);
        }
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "spring.data.mongodb")
    public class MongoSettingsProperties {
        private List<String> address;
        private String database;
        private String username;
        private String password;
        private String authenticationDatabase;
        private Integer minConnectionsPerHost;
        private Integer maxConnectionsPerHost;
        private Integer threadsAllowedToBlockForConnectionMultiplier;
        private Integer serverSelectionTimeout;
        private Integer maxWaitTime;
        private Integer maxConnectionIdelTime;
        private Integer maxConnectionLifeTime;
        private Integer connectTimeout;
        private Integer socketTimeout;
        private Boolean socketKeepAlive;
        private Boolean sslEnabled;
        private Boolean sslInvalidHostNameAllowed;
        private Boolean alwaysUseMBeans;
        private Integer heartbeatConnectTimeout;
        private Integer heartbeatSocketTimeout;
        private Integer minHeartbeatFrequency;
        private Integer heartbeatFrequency;
    }

}
