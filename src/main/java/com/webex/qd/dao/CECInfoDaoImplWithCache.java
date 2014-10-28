package com.webex.qd.dao;

import com.webex.qd.vo.CECInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * author:Tony Wang
 * version:1.0
 * date:12/5/13
 */
@Repository("cecDao")
@Transactional
public class CECInfoDaoImplWithCache implements CECInfoDao {
    @Autowired
    private CECInfoDao cecDao_no_cache;
    private HashMap<String, CECInfo> cache = new HashMap<String, CECInfo>();

    @Override
    public CECInfo getCECInfoByCec(String cecId) throws Exception {
        if (cache.containsKey(cecId)) {
            return cache.get(cecId);
        }
        CECInfo cecInfo = cecDao_no_cache.getCECInfoByCec(cecId);
        if (cecInfo != null) {
            cache.put(cecId, cecInfo);
        } else {
            cache.put(cecId, new CECInfo(cecId));// will never try to use ldap to get the data
        }
        return cecInfo;
    }

    @Override
    public List<CECInfo> getCECInfoByCecList(List<String> cecList) throws Exception {
        List<CECInfo> list = new LinkedList<CECInfo>();
        for (String cecId : cecList) {
            CECInfo info = getCECInfoByCec(cecId);
            list.add(info);
        }
        return list;
    }
}
