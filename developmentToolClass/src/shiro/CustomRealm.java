package shiro;

import com.ourhz.modules.auth.client.service.SessionUser;
import com.ourhz.product.datasheet.commonserver.fill.entity.DsPermissions;
import com.ourhz.product.datasheet.commonserver.fill.entity.DsRoles;
import com.ourhz.product.datasheet.commonserver.fill.mapper.DsPermissionsMapper;
import com.ourhz.product.datasheet.commonserver.fill.mapper.DsRolesMapper;
import com.ourhz.product.datasheet.commonserver.uitls.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private DsRolesMapper dsRolesMapper;

    @Resource
    private DsPermissionsMapper dsPermissionsMapper;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        log.info("进入权限认证........");

        // 通过PrincipalCollection获取到认证时传入的用户信息
        SessionUser sessoinUser = (SessionUser) principalCollection.getPrimaryPrincipal();

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        log.info("认证的用户信息如下: " + sessoinUser);

        // 判断是否获取到sessionUser信息 未获取到则不查询角色以及权限
       if(sessoinUser != null){
           // 获取当前登录用户id
           Long userUid = sessoinUser.getUid();

           if(userUid != null){

               // 通过userUid拿到用户的角色以权限
               DsRoles userRoleByUserId = this.getUserRoleByUserId(userUid);

               // 判断是否获取到用户的角色信息
               if(userRoleByUserId != null){

                   // 创建集合分别存放用户的角色信息 以及 权限信息
                   Set<String> userRolesSets = new HashSet<>();
                   Set<String> userAuthoritySets = new HashSet<>();

                   // 存放角色英文名enName字段
                   userRolesSets.add(userRoleByUserId.getEnName());

                   List<DsPermissions> permissionsList = userRoleByUserId.getPermissionsList();

                   if(permissionsList != null && permissionsList.size() > 0){

                       for(DsPermissions dsAuthority: permissionsList){
                           // 存放请求路径apiName字段
                           userAuthoritySets.add(dsAuthority.getApiName());
                       }
                   }

                   // 将角色以及权限信息放入simpleAuthorizationInfo对象
                   simpleAuthorizationInfo.setRoles(userRolesSets);
                   simpleAuthorizationInfo.setStringPermissions(userAuthoritySets);

                   log.info("获取的角色信息如下: " + simpleAuthorizationInfo.getRoles());
                   log.info("获取的角色权限如下: " + simpleAuthorizationInfo.getStringPermissions());
               }

           }

       }

        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        log.info("进入认证方法......");

        // 拿到sessionUser对象
        SessionUser sessionUser = SpringBeanUtil.getBean(SessionUser.class);

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(sessionUser,"123","");

        return authenticationInfo;
    }

    /**
     *  获取用户角色以及权限
     */
    private DsRoles getUserRoleByUserId(Long userId){

        DsRoles rolesByUserId = dsRolesMapper.getRolesByUserId(userId);

        if(rolesByUserId != null){

            List<DsPermissions> permissionsByRoleIds =
                    dsPermissionsMapper.getPermissionsByRoleIds(rolesByUserId.getId());

            if(permissionsByRoleIds != null && permissionsByRoleIds.size() > 0){

                rolesByUserId.setPermissionsList(permissionsByRoleIds);

                return rolesByUserId;
            }
        }

        return null;
    }
}
