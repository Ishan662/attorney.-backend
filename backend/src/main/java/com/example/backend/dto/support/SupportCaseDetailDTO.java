package com.example.backend.dto.support;

import java.util.List;

public class SupportCaseDetailDTO extends SupportCaseListDTO {
    private List<SupportMessageDTO> messages;
    public List<SupportMessageDTO> getMessages() { return messages; }
    public void setMessages(List<SupportMessageDTO> messages) { this.messages = messages; }
}