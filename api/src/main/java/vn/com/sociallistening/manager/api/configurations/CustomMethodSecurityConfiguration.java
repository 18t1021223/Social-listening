package vn.com.sociallistening.manager.api.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import vn.com.sociallistening.manager.api.repository.mariadb.RoleRepository;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class CustomMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
    private final RoleRepository roleRepository;

    @Autowired
    public CustomMethodSecurityConfiguration(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler(this.roleRepository);
        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return expressionHandler;
    }
}
