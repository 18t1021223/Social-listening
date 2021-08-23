package vn.com.sociallistening.manager.api.configurations;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import vn.com.sociallistening.manager.api.repository.mariadb.RoleRepository;

public class CustomMethodSecurityExpressionHandler extends OAuth2MethodSecurityExpressionHandler {
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    private RoleRepository roleRepository;

    public CustomMethodSecurityExpressionHandler(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, this.roleRepository);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }
}
