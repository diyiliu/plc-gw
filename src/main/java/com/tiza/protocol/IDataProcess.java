package com.tiza.protocol;

import com.tiza.support.model.header.Header;

/**
 * Description: IDataProcess
 * Author: DIYILIU
 * Update: 2018-01-30 09:44
 */
public interface IDataProcess {

    void init();

    Header dealHeader(byte[] bytes);

    void parse(byte[] content, Header header);

    byte[] pack(Header header, Object... argus);
}
