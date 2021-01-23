package org.fhi360.module.Hts.config;

import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import org.fhi360.module.Hts.domain.HtsDomain;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = HtsDomain.class)
public class DomainConfiguration {
}
