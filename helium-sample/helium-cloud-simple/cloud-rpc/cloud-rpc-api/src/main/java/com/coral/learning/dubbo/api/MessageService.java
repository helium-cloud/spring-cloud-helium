package com.coral.learning.dubbo.api;

public interface MessageService {
    MessageResponse send(MessageRequest messageRequest);
}
