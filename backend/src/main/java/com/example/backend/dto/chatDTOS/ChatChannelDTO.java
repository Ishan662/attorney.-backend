package com.example.backend.dto.chatDTOS;

import java.util.List;
import java.util.UUID;

public class ChatChannelDTO {
    private UUID caseId;
    private String chatChannelId;
    private String caseTitle;
    private List<MemberDTO> members;
    // You could add lastMessage here later for previews

    // Getters and Setters
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public String getChatChannelId() { return chatChannelId; }
    public void setChatChannelId(String chatChannelId) { this.chatChannelId = chatChannelId; }
    public String getCaseTitle() { return caseTitle; }
    public void setCaseTitle(String caseTitle) { this.caseTitle = caseTitle; }
    public List<MemberDTO> getMembers() { return members; }
    public void setMembers(List<MemberDTO> members) { this.members = members; }
}