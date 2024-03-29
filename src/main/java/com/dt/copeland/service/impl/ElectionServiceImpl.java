package com.dt.copeland.service.impl;

import com.dt.copeland.dto.BallotDTO;
import com.dt.copeland.dto.ElectionDTO;
import com.dt.copeland.exception.ResourceNotFoundException;
import com.dt.copeland.model.Ballot;
import com.dt.copeland.model.Election;
import com.dt.copeland.repository.ElectionRepository;
import com.dt.copeland.service.BallotService;
import com.dt.copeland.service.ElectionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final BallotService ballotService;
    private final ModelMapper modelMapper;

    public ElectionServiceImpl(ElectionRepository electionRepository, BallotService ballotService, ModelMapper modelMapper) {
        this.electionRepository = electionRepository;
        this.ballotService = ballotService;
        this.modelMapper = modelMapper;
    }

    @Override
    public ElectionDTO create(ElectionDTO electionDTO) {
        Election election = modelMapper.map(electionDTO, Election.class);

        LocalDateTime currentDateTime = LocalDateTime.now();
        election.setEntryDate(java.util.Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant()));

        Election savedElection = electionRepository.save(election);
        return modelMapper.map(savedElection, ElectionDTO.class);
    }

    @Override
    public ElectionDTO readOne(Long idNo) {
        Election election = electionRepository
                .findById(idNo)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with the given ID!"));
        return modelMapper.map(election, ElectionDTO.class);
    }

    @Override
    public List<ElectionDTO> readAllElectionsByModerator(String moderator) {
        List<Election> elections = electionRepository.findAllByModerator(moderator);
        return elections.stream().map(election -> modelMapper.map(election, ElectionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<ElectionDTO> readAllElectionsFromAllUsers() {
        List<Election> elections = electionRepository.findAll();
        return elections.stream().map(election -> modelMapper.map(election, ElectionDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<BallotDTO> readAllVotes(Long idNo) {
        List<Ballot> ballots = ballotService.readAllVotesForElection(idNo);
        return ballots.stream().map(ballot -> modelMapper.map(ballot, BallotDTO.class)).collect(Collectors.toList());
    }

    @Override
    public String getWinner(Long idNo) { // COPELAND ALGORITHM IMPLEMENTED BY YOURS TRULY: ASHLEY PONTAY KNOWN AS devashpontay
        Election electionSession = electionRepository
                .findById(idNo)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with the given ID!"));

        List<Ballot> ballots = ballotService.readAllVotesForElection(idNo);

        if(ballots.isEmpty()) {
            return "NO_VOTES";
        }

        double[] candidatesScore = new double[electionSession.getCandidateCount()];
        for (int i = 0; i < electionSession.getCandidateCount(); i++) {
            for (int j = i + 1; j < electionSession.getCandidateCount(); j++) {
                int candidateOne = 0;
                int candidateTwo = 0;
                for (Ballot ballot : ballots) {
                    if (ballot.getSelectedCandidates().indexOf(i) < ballot.getSelectedCandidates().indexOf(j)) {
                        candidateOne += 1;
                    }else if(ballot.getSelectedCandidates().indexOf(j) < ballot.getSelectedCandidates().indexOf(i)){
                        candidateTwo += 1;
                    }
                }

                if(candidateOne > candidateTwo) {
                    candidatesScore[i] += 1.0;
                }else if(candidateTwo > candidateOne) {
                    candidatesScore[j] += 1.0;
                }else {
                    candidatesScore[i] += 0.5;
                    candidatesScore[j] += 0.5;
                }

            }
        }

        List<Integer> maxIndices = new ArrayList<>();
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < electionSession.getCandidates().size(); i++) {
            if (candidatesScore[i] > max) {
                max = candidatesScore[i];
                maxIndices.clear();
                maxIndices.add(i);
            } else if (candidatesScore[i] == max) {
                maxIndices.add(i);
            }
        }

        if (maxIndices.size() != 1) {
            return maxIndices.stream().map(i -> electionSession.getCandidates().get(i)).collect(Collectors.joining(", "));
        }

        return electionSession.getCandidates().get(maxIndices.get(0));
    }

    @Override
    public BallotDTO isDoneVoting(Long idNo, String user) {
        List<Ballot> ballots = ballotService.readAllVotesForElection(idNo);


        for(Ballot ballot : ballots) {
            if(ballot.getVoterUserName().equals(user)) {
                return modelMapper.map(ballot, BallotDTO.class);
            }
        }

        throw new ResourceNotFoundException("Ballot not found dude.");
    }

    @Override
    public ElectionDTO markAsClosed(Long idNo) {
        Election election = electionRepository
                .findById(idNo)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found bro"));
        election.setStatus("CLOSED");
        return modelMapper.map(electionRepository.save(election), ElectionDTO.class);
    }

    @Override
    public Integer votesCountForElection(Long idNo) {
        List<Ballot> ballots = ballotService.readAllVotesForElection(idNo);
        return ballots.size();
    }

    @Override
    public void delete(Long idNo) {
        electionRepository
                .findById(idNo)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));
        electionRepository.deleteById(idNo);
    }
}
