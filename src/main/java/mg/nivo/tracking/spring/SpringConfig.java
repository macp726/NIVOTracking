package mg.nivo.tracking.spring;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc 
@ComponentScan({"mg.nivo.tracking"})
public class SpringConfig  {
   
	   @Bean
	   public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	      return new PropertySourcesPlaceholderConfigurer();
	   }
	   
	   @Bean
	   public DataSource trackingDB(){
		   JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		   bean.setJndiName("java:comp/env/jdbc/trackingDS");
		   try {
	            bean.afterPropertiesSet();
	        } catch (IllegalArgumentException | NamingException e) {
	            // rethrow
	            throw new RuntimeException(e);
	        }
		   return (DataSource) bean.getObject();
	   }
}
