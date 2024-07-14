package com.turingSecApp.turingSec.helper.entityHelper.program;

import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.program.Prohibit;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.*;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.model.repository.program.asset.*;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.payload.program.ProhibitPayload;
import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramEntityHelper implements IProgramEntityHelper {
    private final ProgramRepository programRepository;
    private final ProgramAssetRepository programAssetRepository;
    private final LPARepository lpaRepository;
    private final MPARepository mpaRepository;
    private final HPARepository hpaRepository;
    private final CPARepository cpaRepository;

    @Override
    public void removeExistingProgram(CompanyEntity company) {
        List<Program> programList = programRepository.findAll();
        if (!programList.isEmpty()) {
            Program existingProgram = programList.get(0);
            Program programFromDB = programRepository.findById(existingProgram.getId()).orElseThrow(() -> new ResourceNotFoundException("Program not found with id:" + existingProgram.getId()));
            company.removeProgram(programFromDB.getId());
            programRepository.delete(programFromDB);
        }
    }

    @Override
    public Program createProgramEntity(ProgramPayload programPayload, CompanyEntity company) {
        Program program = new Program();

        // Set basic or embedded fields
        program.setFromDate(programPayload.getFromDate());
        program.setToDate(programPayload.getToDate());
        program.setNotes(programPayload.getNotes());
        program.setPolicy(programPayload.getPolicy());
        program.setCompany(company);
        program.setInScope(programPayload.getInScope());
        program.setOutOfScope(programPayload.getOutOfScope());

        return program;
    }

    @Override
    public void setProhibits(ProgramPayload programPayload, Program program) {
        // Set parent in child entities
        List<Prohibit> prohibits = setProgramInProhibits(programPayload.getProhibits(), program);

        // Set child in parent entity
        program.setProhibits(prohibits);
    }

    @Override
    @Transactional
    public void setProgramAsset(ProgramPayload programPayload, Program program) {
        // To set program asset, we need to save it, then we can set
        ProgramAsset savedProgramAsset = saveProgramAssets(programPayload);

        // Retrieve the saved program asset from the database to create active hibernate session
        ProgramAsset programAssetFromDB = programAssetRepository.findById(savedProgramAsset.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + savedProgramAsset.getId()));

        // Set parent in child entity
        programAssetFromDB.setProgram(program);

        // Set child in Parent entity
        program.setAsset(programAssetFromDB);
    }

    private ProgramAsset saveProgramAssets(ProgramPayload programPayload) {
        // Extract fields from payload
        LowProgramAsset savedLowProgramAsset = getLowProgramAsset(programPayload);
        MediumProgramAsset savedMediumProgramAsset = getMediumProgramAsset(programPayload);
        HighProgramAsset savedHighProgramAsset = getHighProgramAsset(programPayload);
        CriticalProgramAsset savedCriticalProgramAsset = getCriticalProgramAsset(programPayload);

        // Create ProgramAsset and set saved child entities in the parent entity
        ProgramAsset programAsset = createProgramAsset(savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);

        // Set parent entity in child entities
        setProgramAssetForChildren(programAsset,savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);

        // Save parent entity
        return programAssetRepository.save(programAsset);
    }

    //
    private List<Prohibit> setProgramInProhibits(List<ProhibitPayload> prohibitsPayload, Program program) {
        return prohibitsPayload.stream().map(prohibitPayload -> {
            Prohibit prohibit = new Prohibit();
            prohibit.setProhibitAdded(prohibitPayload.getProhibitAdded());

            // Set parent in child entity
            prohibit.setBugBountyProgramForStrict(program);
            return prohibit;
        }).collect(Collectors.toList());
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

    //

    private CriticalProgramAsset getCriticalProgramAsset(ProgramPayload programPayload) {
        return getProgramAsset(
                programPayload.getAsset().getCriticalAsset().getAssets(),
                new CriticalProgramAsset()
        );
    }

    private HighProgramAsset getHighProgramAsset(ProgramPayload programPayload) {
        return getProgramAsset(
                programPayload.getAsset().getHighAsset().getAssets(),
                new HighProgramAsset()
        );
    }

    private MediumProgramAsset getMediumProgramAsset(ProgramPayload programPayload) {
        return getProgramAsset(
                programPayload.getAsset().getMediumAsset().getAssets(),
                new MediumProgramAsset()
        );
    }

    private LowProgramAsset getLowProgramAsset(ProgramPayload programPayload) {
        return getProgramAsset(
                programPayload.getAsset().getLowAsset().getAssets(),
                new LowProgramAsset()
        );
    }

    //
    private <T extends BaseProgramAsset> T getProgramAsset(Set<AssetPayload> assetPayloads, T programAsset) {
        // Asset payload to asset
        Set<Asset> assets = convertAssetPayloadsToAssets(assetPayloads);

        // Set parent in child entity
        assets.forEach(asset -> asset.setBaseProgramAsset(programAsset));

        // Set child in parent entity
        programAsset.setAssets(assets);

        // Save program asset for type
        return saveBaseProgramAsset(programAsset);
    }

    private Set<Asset> convertAssetPayloadsToAssets(Set<AssetPayload> assetPayloads) {
        return assetPayloads.stream().map(assetPayload -> {
            Asset asset = new Asset();
            asset.setPrice(assetPayload.getPrice());
            asset.setType(assetPayload.getType());
            asset.setNames(new HashSet<>(assetPayload.getNames()));
            return asset;
        }).collect(Collectors.toSet());
    }

    private  <T extends BaseProgramAsset> T saveBaseProgramAsset(T baseProgramAsset) {
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

}
