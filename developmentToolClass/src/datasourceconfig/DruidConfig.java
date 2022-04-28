package datasourceconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author
 * @Time 2022/4/11
 * @Desc druid配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.druid")
@Data
public class DruidConfig {

	private int initialSize;

	private int minIdle;

	private int maxActive;

	private int maxWait;

	private int timeBetweenEvictionRunsMillis;

	private int minEvictableIdleTimeMillis;

	private String validationQuery;

	private Boolean testWhileIdle;

	private Boolean testOnBorrow;

	private Boolean testOnReturn;

	private Boolean poolPreparedStatements;

	private int maxPoolPreparedStatementPerConnectionSize;

	private String filters;

	private String connectionProperties;

}
