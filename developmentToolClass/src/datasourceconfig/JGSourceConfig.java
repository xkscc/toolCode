package datasourceconfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.ourhz.product.datasheet.commonserver.uitls.MultiSourceUtil;
import lombok.Data;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author
 * @Time 2022/4/11
 * @Desc druid多数据源统一填报配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource.jg")
@Data
@MapperScan(basePackages = JGSourceConfig.PACKAGE, sqlSessionFactoryRef = "jgSqlSessionFactory")
public class JGSourceConfig {

	public static final String PACKAGE = "com.ourhz.product.datasheet.commonserver.jg";
	public static final String MAPPER_LOCATION  = "classpath:mapper/jg/*.xml";

	private String driverClassName;
	private String username;
	private String password;
	private String url;

	@Resource
	private DruidConfig druidConfig;

	@Bean("jgDataSource")
	@ConditionalOnExpression("${spring.datasource.druid.enable:true}")
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		MultiSourceUtil.setDataSource(dataSource, druidConfig, url, username, password, driverClassName);
		return dataSource;
	}

	@Bean("jgDataSource")
	@ConditionalOnMissingBean(name = "jgDataSource")
	public DataSource dataSourceLocal() {
		return DataSourceBuilder.create()
				.url(url)
				.username(username)
				.password(password)
				.driverClassName(driverClassName)
				.build();
	}

	@Bean(name = "jgTransactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("jgDataSource") DataSource masterDataSource) {
		return new DataSourceTransactionManager(masterDataSource);
	}

	@Bean(name = "jgSqlSessionFactory")
	public SqlSessionFactory masterSqlSessionFactory(@Qualifier("jgDataSource") DataSource masterDataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(masterDataSource);
		sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(JGSourceConfig.MAPPER_LOCATION));

		return sessionFactory.getObject();
	}

}
