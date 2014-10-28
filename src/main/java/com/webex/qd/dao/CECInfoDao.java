package com.webex.qd.dao;

import com.webex.qd.vo.CECInfo;

import java.util.List;

/**
 * author:Tony Wang
 * version:1.0
 * date:12/4/13
 */
public interface CECInfoDao {
    CECInfo getCECInfoByCec(String cecId) throws Exception;
    List<CECInfo> getCECInfoByCecList(List<String> cecList)throws Exception;
}
