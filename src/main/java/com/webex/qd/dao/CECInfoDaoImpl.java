package com.webex.qd.dao;

import com.webex.qd.vo.CECInfo;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.directory.Attributes;
import java.util.List;

/**
 * author:Tony Wang
 * version:1.0
 * date:12/4/13
 */
public class CECInfoDaoImpl implements CECInfoDao {
    @Override
    public List<CECInfo> getCECInfoByCecList(List<String> cecList) throws Exception {
        throw new UnsupportedOperationException();
    }
    private LdapTemplate ldapTemplate;

    public void setLdapTemplate(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
   @Override
    public CECInfo getCECInfoByCec(final String cecId)throws Exception{
       List<CECInfo> cecInfo = (List<CECInfo>) ldapTemplate.search("", String.format("(uid=%s)", cecId), new AttributesMapper() {
           public Object mapFromAttributes(Attributes attrs) {
               try {
                   return CECInfo.create(attrs);
               } catch (Exception e) {
                   return new CECInfo(cecId);
               }
           }
       });
    return cecInfo==null?new CECInfo(cecId):cecInfo.isEmpty()?new CECInfo(cecId):cecInfo.get(0);
   }

}
