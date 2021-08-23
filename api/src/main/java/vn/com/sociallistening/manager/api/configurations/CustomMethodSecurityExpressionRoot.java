package vn.com.sociallistening.manager.api.configurations;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import vn.com.sociallistening.manager.api.repository.mariadb.RoleRepository;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private Object filterObject;
    private Object returnObject;
    private RoleRepository roleRepository;

    public CustomMethodSecurityExpressionRoot(Authentication authentication, RoleRepository roleRepository) {
        super(authentication);
        this.roleRepository = roleRepository;
    }

    @Override
    public void setFilterObject(Object o) {
        this.filterObject = o;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object o) {
        this.returnObject = o;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    public boolean customHasAuthority(String s) {
        if (StringUtils.isEmpty(this.authentication.getName())) return false;
        try {
            return roleRepository.findByNameIgnoreCaseAndUsernameIgnoreCase(s.trim(), this.authentication.getName().trim()) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
