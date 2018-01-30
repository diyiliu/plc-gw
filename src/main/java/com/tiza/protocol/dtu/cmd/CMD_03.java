package com.tiza.protocol.dtu.cmd;

import com.tiza.protocol.dtu.DtuDataProcess;
import com.tiza.support.model.header.Header;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_03
 * Author: DIYILIU
 * Update: 2018-01-30 09:54
 */

@Service
public class CMD_03 extends DtuDataProcess {

    public CMD_03() {
        this.cmd = 0x03;
    }

    @Override
    public void parse(byte[] content, Header header) {


    }
}
