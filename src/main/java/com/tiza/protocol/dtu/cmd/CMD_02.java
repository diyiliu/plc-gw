package com.tiza.protocol.dtu.cmd;

import com.tiza.protocol.dtu.DtuDataProcess;
import com.tiza.support.model.header.DtuHeader;
import com.tiza.support.model.header.Header;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_02
 * Author: DIYILIU
 * Update: 2018-01-30 09:54
 */

@Service
public class CMD_02 extends DtuDataProcess {

    public CMD_02() {
        this.cmd = 0x02;
    }

    @Override
    public void parse(byte[] content, Header header) {
        DtuHeader dtuHeader = (DtuHeader) header;

        byte b1 = content[0];
        byte b2 = content[1];

    }
}
