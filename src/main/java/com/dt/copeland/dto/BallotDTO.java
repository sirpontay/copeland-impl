package com.dt.copeland.dto;

import java.util.List;

public class BallotDTO {

    private Long idNo;
    private String voterUserName;
    private Long electionIdNo;
    private List<String> selectedCandidates;


    public BallotDTO(Long idNo, String voterUserName, Long electionIdNo, List<String> selectedCandidates) {
        this.idNo = idNo;
        this.voterUserName = voterUserName;
        this.electionIdNo = electionIdNo;
        this.selectedCandidates = selectedCandidates;
    }

    public BallotDTO() {
    }

    public Long getIdNo() {
        return idNo;
    }

    public void setIdNo(Long idNo) {
        this.idNo = idNo;
    }

    public String getVoterUserName() {
        return voterUserName;
    }

    public void setVoterUserName(String voterUserName) {
        this.voterUserName = voterUserName;
    }

    public Long getElectionIdNo() {
        return electionIdNo;
    }

    public void setElectionIdNo(Long electionIdNo) {
        this.electionIdNo = electionIdNo;
    }

    public List<String> getSelectedCandidates() {
        return selectedCandidates;
    }

    public void setSelectedCandidates(List<String> selectedCandidates) {
        this.selectedCandidates = selectedCandidates;
    }
}
