package com.agriguardian.service.interfaces;

public interface DataEncoder {
    public String encode(String toEncrypt);
    public String decode(String toDecrypt);
}
