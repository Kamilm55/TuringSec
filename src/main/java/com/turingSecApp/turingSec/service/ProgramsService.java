package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.StrictEntity;
import com.turingSecApp.turingSec.dao.repository.ProgramsRepository;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


import java.util.List;
import java.util.Optional;

@Service
public class ProgramsService {

    @Autowired
    private ProgramsRepository programsRepository;

    public BugBountyProgramEntity createOrUpdateBugBountyProgram(BugBountyProgramEntity program) {
        // Check if a program with the same parameters already exists for the company
        List<BugBountyProgramEntity> programs = programsRepository.findByCompany(program.getCompany());
        if (!programs.isEmpty()) {
            BugBountyProgramEntity existingProgram = programs.get(0);
            // Update existing program with the new data
            updateProgramFields(existingProgram, program);
            return programsRepository.save(existingProgram);
        } else {
            // Create new program
            return programsRepository.save(program);
        }

    }

    private void updateProgramFields(BugBountyProgramEntity existingProgram, BugBountyProgramEntity newProgram) {
        // Update fields of the existing program with the new program data
        existingProgram.setFromDate(newProgram.getFromDate());
        existingProgram.setToDate(newProgram.getToDate());
        existingProgram.setNotes(newProgram.getNotes());
        existingProgram.setPolicy(newProgram.getPolicy());

        // Update or add new asset types (if needed)
        List<AssetTypeEntity> existingAssetTypes = existingProgram.getAssetTypes();
        List<AssetTypeEntity> updatedAssetTypes = new ArrayList<>();

        // Update or add new strict  (if needed)
        List<StrictEntity> existingProgramProhibits = existingProgram.getProhibits();
        List<StrictEntity> updateProhibits = new ArrayList<>();

        for (StrictEntity strictEntity : newProgram.getProhibits()) {
            StrictEntity existingProhibit = findExistingProhibits(existingProgramProhibits, strictEntity);
            if (existingProhibit != null) {
                // Update existing asset type
                existingProhibit.setProhibitAdded(strictEntity.getProhibitAdded());

                updateProhibits.add(existingProhibit);
            } else {
                // Add new asset type
                strictEntity.setBugBountyProgramForStrict(existingProgram);
                updateProhibits.add(strictEntity);
            }
        }

        existingProgram.getProhibits().clear();
        existingProgram.getProhibits().addAll(updateProhibits);

//////////////////////////////////////////
        for (AssetTypeEntity assetType : newProgram.getAssetTypes()) {
            AssetTypeEntity existingAssetType = findExistingAssetType(existingAssetTypes, assetType);
            if (existingAssetType != null) {
                // Update existing asset type
                existingAssetType.setLevel(assetType.getLevel());
                existingAssetType.setAssetType(assetType.getAssetType());
                existingAssetType.setPrice(assetType.getPrice());
                updatedAssetTypes.add(existingAssetType);
            } else {
                // Add new asset type
                assetType.setBugBountyProgram(existingProgram);
                updatedAssetTypes.add(assetType);
            }
        }

        existingProgram.getAssetTypes().clear();
        existingProgram.getAssetTypes().addAll(updatedAssetTypes);
    }

    private AssetTypeEntity findExistingAssetType(List<AssetTypeEntity> existingAssetTypes, AssetTypeEntity assetType) {
        for (AssetTypeEntity existing : existingAssetTypes) {
            if (existing.getId().equals(assetType.getId())) {
                return existing;
            }
        }
        return null;
    }

    private StrictEntity findExistingProhibits(List<StrictEntity> existingProhibits, StrictEntity strictEntity) {
        for (StrictEntity existing : existingProhibits) {
            if (existing.getId().equals(strictEntity.getId())) {
                return existing;
            }
        }
        return null;
    }


    public List<BugBountyProgramEntity> getAllBugBountyPrograms() {
        return programsRepository.findAll();
    }

    public BugBountyProgramEntity getBugBountyProgramById(Long id) {
        Optional<BugBountyProgramEntity> program = programsRepository.findById(id);
        return program.orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found"));
    }

    public void deleteBugBountyProgram(Long id) {
        BugBountyProgramEntity program = getBugBountyProgramById(id);
        programsRepository.delete(program);
    }

    public List<BugBountyProgramEntity> getAllBugBountyProgramsByCompany(CompanyEntity company) {
        return programsRepository.findByCompany(company);
    }
}
