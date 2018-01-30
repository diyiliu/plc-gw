package com.tiza.protocol.dtu;

import com.tiza.protocol.IDataProcess;
import com.tiza.support.cache.ICache;
import com.tiza.support.model.header.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Description: DtuDataProcess
 * Author: DIYILIU
 * Update: 2018-01-30 09:45
 */

@Service
public class DtuDataProcess implements IDataProcess {

    protected int cmd = 0xFF;

    @Resource
    private ICache dtuCMDCacheProvider;

    @Override
    public void init() {
        dtuCMDCacheProvider.put(this.cmd, this);
    }

    @Override
    public Header dealHeader(byte[] bytes) {

        return null;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }

    @Override
    public byte[] pack(Header header, Object... argus) {
        return new byte[0];
    }



}
