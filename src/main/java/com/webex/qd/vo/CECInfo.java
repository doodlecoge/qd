package com.webex.qd.vo;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.map.annotate.JsonFilter;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.lang.reflect.Field;

/**
 * author:Tony Wang
 * version:1.0
 * date:12/4/13
 */
@JsonFilter("info")
public class CECInfo {
    private String city;
    private String uid;//cec id
    private String sn;//last name
    private String nickname;//first name
    private String givenName;//full name
    private String manager;//manager
    private String distinguishedName;//ldap path
    public CECInfo(){}
    public CECInfo(String cecId){
        uid = cecId;
    }
    public static CECInfo create(Attributes attributes) throws Exception {
        CECInfo cecInfo = new CECInfo();
        for (Field field : CECInfo.class.getDeclaredFields()) {
            Attribute attribute = attributes.get(field.getName());
            if (attribute != null)
            BeanUtils.setProperty(cecInfo, field.getName(), attribute.get());
        }
        return cecInfo;
    }

    public String getPrettyName() {
        if (!(nickname == null||sn == null)){
            return String.format("%s %s (%s)", nickname, sn, uid);
        }
        else if (!(givenName == null||sn == null)){
            return String.format("%s %s (%s)", givenName, sn, uid);
        }else {
            return uid;
        }
    }

    public String getPhotoUrl() {
        return String.format(CECInfoExtension.PHOTO_URL, uid);
    }

    public String getDetailUrl() {
        return String.format(CECInfoExtension.DETAIL_URL, uid);
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    private static class CECInfoExtension {
        private static final String PHOTO_URL = "http://wwwin.cisex.com/dir/photo/std/%s.jpg";
        private static final String DETAIL_URL = "http://wwwin-tools.cisex.com/dir/details/%s";
    }
}
