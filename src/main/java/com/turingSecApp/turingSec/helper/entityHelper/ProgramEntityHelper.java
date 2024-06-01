package com.turingSecApp.turingSec.helper.entityHelper;

import com.turingSecApp.turingSec.dao.entities.program.Asset;
import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.program.StrictEntity;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.*;
import com.turingSecApp.turingSec.dao.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.dao.repository.program.ProgramsRepository;
import com.turingSecApp.turingSec.dao.repository.program.asset.CPARepository;
import com.turingSecApp.turingSec.dao.repository.program.asset.HPARepository;
import com.turingSecApp.turingSec.dao.repository.program.asset.LPARepository;
import com.turingSecApp.turingSec.dao.repository.program.asset.MPARepository;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.payload.program.StrictPayload;
import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramEntityHelper implements IProgramEntityHelper {
    private final ProgramsRepository programsRepository;
    private final LPARepository lpaRepository;
    private final MPARepository mpaRepository;
    private final HPARepository hpaRepository;
    private final CPARepository cpaRepository;

    @Override
    public void removeExistingProgram(CompanyEntity company) {
        List<Program> programList = programsRepository.findAll();
        if (!programList.isEmpty()) {
            Program existingProgram = programList.get(0);
            Program programFromDB = programsRepository.findById(existingProgram.getId()).orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + existingProgram.getId()));
            company.removeProgram(programFromDB.getId());
            programsRepository.delete(programFromDB);
        }
    }

    @Override
    public Program createProgramEntity(ProgramPayload programPayload, CompanyEntity company) {

        Program program = new Program();
        program.setFromDate(programPayload.getFromDate());
        program.setToDate(programPayload.getToDate());
        program.setNotes(programPayload.getNotes());
        program.setPolicy(programPayload.getPolicy());
        program.setCompany(company);
        program.setInScope(programPayload.getInScope());
        program.setOutOfScope(programPayload.getOutOfScope());
        program.setProhibits(convertToStrictEntities(programPayload.getProhibits(), program));

        return program;
    }

    private List<StrictEntity> convertToStrictEntities(List<StrictPayload> prohibitsDTOs, Program program) {
        return prohibitsDTOs.stream().map(prohibitDTO -> {
            StrictEntity strictEntity = new StrictEntity();
            strictEntity.setProhibitAdded(prohibitDTO.getProhibitAdded());
            strictEntity.setBugBountyProgramForStrict(program);
            return strictEntity;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteBugBountyProgram(Long id, CompanyEntity company) {
        Program program = programsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found"));
        if (program.getCompany().getId().equals(company.getId())) {
            company.removeProgram(program.getId());
            programsRepository.delete(program);
        } else {
            throw new PermissionDeniedException();
        }
    }

    public Set<Asset> convertAssetPayloadsToAssets(Set<AssetPayload> assetPayloads) {
        Set<Asset> assets = new HashSet<>();
        for (AssetPayload assetPayload : assetPayloads) {
            Asset asset = new Asset();
            asset.setType(assetPayload.getType());
            asset.setNames(new HashSet<>(assetPayload.getNames()));
            assets.add(asset);
        }
        return assets;
    }

    @Override
    public <T extends BaseProgramAsset> T setAssetsToBaseProgramAsset(T programAsset, Set<Asset> assets, double price) {
        programAsset.setAssets(assets);
        programAsset.setPrice(price);
        // Assuming there's a method to save the program asset
        return saveBaseProgramAsset(programAsset);
    }


    @Override
    public <T extends BaseProgramAsset> void setBaseProgramAssetProperties(T baseProgramAsset, Set<Asset> assetSet, Double assetPrice) {
        baseProgramAsset.setPrice(assetPrice);
        baseProgramAsset.setAssets(assetSet);

        // Set the base program asset reference in each asset
        assetSet.forEach(asset -> asset.setBaseProgramAsset(baseProgramAsset));
    }

    @Override
    public <T extends BaseProgramAsset> T saveBaseProgramAsset(T baseProgramAsset) {
        if (baseProgramAsset instanceof LowProgramAsset) {
            return (T) lpaRepository.save((LowProgramAsset) baseProgramAsset);
        } else if (baseProgramAsset instanceof MediumProgramAsset) {
            return (T) mpaRepository.save((MediumProgramAsset) baseProgramAsset);
        } else if (baseProgramAsset instanceof HighProgramAsset) {
            return (T) hpaRepository.save((HighProgramAsset) baseProgramAsset);
        } else if (baseProgramAsset instanceof CriticalProgramAsset) {
            return (T) cpaRepository.save((CriticalProgramAsset) baseProgramAsset);
        }
        throw new IllegalArgumentException("Unknown BaseProgramAsset type: " + baseProgramAsset.getClass());
    }

    @Override
    public void setProgramAssetForChildren(ProgramAsset programAsset,
                                           LowProgramAsset lowProgramAsset,
                                           MediumProgramAsset mediumProgramAsset,
                                           HighProgramAsset highProgramAsset,
                                           CriticalProgramAsset criticalProgramAsset) {
        lowProgramAsset.setProgramAsset(programAsset);
        mediumProgramAsset.setProgramAsset(programAsset);
        highProgramAsset.setProgramAsset(programAsset);
        criticalProgramAsset.setProgramAsset(programAsset);
    }

    @Override
    public ProgramAsset createProgramAsset(
            LowProgramAsset lowProgramAsset,
            MediumProgramAsset mediumProgramAsset,
            HighProgramAsset highProgramAsset,
            CriticalProgramAsset criticalProgramAsset) {

        ProgramAsset programAsset = new ProgramAsset();
        programAsset.setLowAsset(lowProgramAsset);
        programAsset.setMediumAsset(mediumProgramAsset);
        programAsset.setHighAsset(highProgramAsset);
        programAsset.setCriticalAsset(criticalProgramAsset);
        return programAsset;
    }

    public void addAssetsToSet(Set<Asset> assets, BaseProgramAsset baseProgramAsset) {
        if (baseProgramAsset != null && baseProgramAsset.getAssets() != null) {
            assets.addAll(baseProgramAsset.getAssets());
        }
    }
}
