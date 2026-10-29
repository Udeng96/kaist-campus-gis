package com.eseict.flame.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

import static com.eseict.flame.config.ApiConstant.*;


@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.eseict.flame.data",
                "com.eseict.flame.repository",
        },
        entityManagerFactoryRef = entity_manager_factory_ref,
        transactionManagerRef = transaction_manager_ref
)
public class DataConfig {

    // primary
    @Value("${spring.datasource.url}")
    String jdbcUrl;
    @Value("${spring.datasource.username}")
    String userName;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${spring.datasource.driver-class-name}")
    String driverName;
    @Value("${spring.datasource.dialect}")
    private String dialect;
    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maxPoolSize;
    @Value("${spring.datasource.hikari.connection-timeout}")
    private long connectionTimeout;

    @Primary
    @Bean(name = "rino")
    public DataSource dataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(jdbcUrl);
        hikariDataSource.setDriverClassName(driverName);
        hikariDataSource.setUsername(userName);
        hikariDataSource.setPassword(password);
        hikariDataSource.setMaximumPoolSize(maxPoolSize);
        hikariDataSource.setConnectionTimeout(connectionTimeout);
        hikariDataSource.setDataSourceProperties(jpaHibernateProperties());

        log.info("####################################################################################################################");
        log.info("## KAIST GIS PRIMARY DataSource Info.");
        log.info("Driver   : {}", driverName);
        log.info("URL      : {}", jdbcUrl);
        log.info("USERNAME : {}", userName);
        log.info("####################################################################################################################");

        return hikariDataSource;
    }

    private Properties jpaHibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.show_sql", "false");
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.time_zone", TIME_ZONE);
        props.setProperty("hibernate.dialect", dialect);
        props.setProperty("hibernate.c3p0.max_size", String.valueOf(maxPoolSize) );
        props.setProperty("hibernate.c3p0.timeout", String.valueOf(connectionTimeout));
        return props;
    }

    @Primary
    @Autowired
    @Bean(name = transaction_manager_ref)
    public JpaTransactionManager jpaTransactionManager(@Qualifier(entity_manager_factory_ref) LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = entity_manager_factory_ref)
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            EntityManagerFactoryBuilder builder,
            @Qualifier("rino") DataSource ds) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(true);
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setDataSource(ds);
        factoryBean.setPackagesToScan(
                "com.eseict.flame.data.vo"
        );
        factoryBean.setJpaProperties(jpaHibernateProperties());
        return factoryBean;
    }

}
