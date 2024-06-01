package com.turingSecApp.turingSec.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.turingSecApp.turingSec.dao.entities.program.Asset;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.*;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.dao.repository.program.*;
import com.turingSecApp.turingSec.dao.repository.program.asset.*;
import com.turingSecApp.turingSec.helper.entityHelper.ProgramEntityHelper;
import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;
import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.program.StrictEntity;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.payload.program.StrictPayload;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramsService implements IProgramsService {

    private final ProgramsRepository programsRepository;
    private final UtilService utilService;
    private final ProgramAssetRepository programAssetRepository;
    private final AssetRepository assetRepository;
    private final ProgramEntityHelper programEntityHelper;

    private final CompanyRepository companyRepository;

    @Override
    public List<Program> getCompanyAllBugBountyPrograms() {
        // Retrieve the company associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();

        // Get programs belonging to the company
        return programsRepository.findByCompany(company);
    }

    @Override
    @Transactional
    public /*BugBountyProgramDTO*/Program createBugBountyProgram(ProgramPayload programPayload) {
        CompanyEntity company = utilService.getAuthenticatedCompany();

        //todo: update if program already exists , if exists delete and post new one with payload

        return convertToBugBountyProgramEntityAndSave(programPayload, company);
    }

    @Transactional // For commandlineRunner (mock data)
    public void createBugBountyProgramForTest(ProgramPayload programPayload, CompanyEntity company) throws JsonProcessingException {
//        CompanyEntity company = getAuthenticatedUser();

        Program program = convertToBugBountyProgramEntityAndSave(programPayload, company);

        System.out.println("createdOrUpdatedProgram " + program);
    }

    // todo: create programEntityHelper service and refactorThis.
    //update
    public Program convertToBugBountyProgramEntityAndSave(ProgramPayload programPayload, CompanyEntity company) {
/*        List<Program> programList = programsRepository.findAll();

        if(!programList.isEmpty()){
            Program existingProgram = programList.get(0);

            // Retrieve the saved program asset from the database to create active hibernate session
            Program programFromDB = programsRepository.findById(existingProgram.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + existingProgram.getId()));;

            company.removeProgram(programFromDB.getId());// without this not work , we remove program from set in company entity then we can delete
            programsRepository.delete(programFromDB);

        }*/
        // Remove existing program if exists
        programEntityHelper.removeExistingProgram(company);
       /* Program program = new Program();
        program.setFromDate(programPayload.getFromDate());
        program.setToDate(programPayload.getToDate());
        program.setNotes(programPayload.getNotes());
        program.setPolicy(programPayload.getPolicy());
        program.setCompany(company);
        program.setInScope(programPayload.getInScope());
        program.setOutOfScope(programPayload.getOutOfScope());
        program.setProhibits(convertToStrictEntities(programPayload.getProhibits(), program));*/
        // Create program entity and save it to the database with the associated company entity
        Program program = programEntityHelper.createProgramEntity(programPayload, company);
        // Set asset and save program
        setProgramAsset(programPayload, program);
        return program;
    }

    private void setProgramAsset(ProgramPayload programPayload, Program program) {
        ProgramAsset savedProgramAsset = saveProgramAssets(programPayload);

        // Retrieve the saved program asset from the database to create active hibernate session
        ProgramAsset programAssetFromDB = programAssetRepository.findById(savedProgramAsset.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + savedProgramAsset.getId()));

        // Set parent in child entity
        programAssetFromDB.setProgram(program);

        // Set child in Parent entity
        program.setAsset(programAssetFromDB);

        // Save parent
        programsRepository.save(program);
    }

    /*    private List<StrictEntity> convertToStrictEntities(List<StrictPayload> prohibitsDTOs, Program program) {
            return prohibitsDTOs.stream()
                    .map(prohibitDTO -> {
                        StrictEntity strictEntity = new StrictEntity();
                        strictEntity.setProhibitAdded(prohibitDTO.getProhibitAdded());
                        strictEntity.setBugBountyProgramForStrict(program);
                        return strictEntity;
                    })
                    .collect(Collectors.toList());
        }*/
    //////////////////////////
    // fixme: fix this method
    @Override
    public Set<Asset> getAllAssets(Long id) {
        Program program = getBugBountyProgramById(id);
        Set<ProgramAsset> programAssets = programAssetRepository.findProgramAssetByProgram(program);

        Set<Asset> assets = new HashSet<>();
        for (ProgramAsset programAsset : programAssets) {
            addAssetsFromAllBaseProgramAssets(assets, programAsset);
        }

        return assets;
    }

    //update
    private void addAssetsFromAllBaseProgramAssets(Set<Asset> assets, ProgramAsset programAsset) {
        // Iterate all assets , todo: iterate with fields (dry)
        // Add assets from all base program assets to the assets set
        programEntityHelper.addAssetsToSet(assets, programAsset.getLowAsset());
        programEntityHelper.addAssetsToSet(assets, programAsset.getMediumAsset());
        programEntityHelper.addAssetsToSet(assets, programAsset.getHighAsset());
        programEntityHelper.addAssetsToSet(assets, programAsset.getCriticalAsset());
    }

    private void addAssetsToAssetSetFromBaseProgramAsset(Set<Asset> assets, BaseProgramAsset baseProgramAsset) {
        if (baseProgramAsset != null) {
            assets.addAll(baseProgramAsset.getAssets());
        }
    }

    @Override
    @Transactional
    public void deleteBugBountyProgram(Long id) {
        // Get the company associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();

        // Retrieve the bug bounty program by ID
        Program program = getBugBountyProgramById(id);

        // Check if the authenticated company is the owner of the program
        if (program.getCompany().getId().equals(company.getId())) {
            company.removeProgram(program.getId());// without this not work , we remove program from set in company entity then we can delete
            programsRepository.delete(program);
        } else {
            // If the authenticated company is not the owner, return forbidden status
            throw new PermissionDeniedException();
        }
    }

    /// fixme: TEST
    @Override
    public ProgramAsset saveProgramAssets(ProgramPayload programPayload) {

        LowProgramAsset savedLowProgramAsset = getLowProgramAsset(programPayload);

        MediumProgramAsset savedMediumProgramAsset = getMediumProgramAsset(programPayload);

        HighProgramAsset savedHighProgramAsset = getHighProgramAsset(programPayload);

        CriticalProgramAsset savedCriticalProgramAsset = getCriticalProgramAsset(programPayload);

        // Create ProgramAsset and set saved child entities in the parent entity
        ProgramAsset savedProgramAsset = createAndSaveProgramAsset(savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);

        // Set parent entity in child entities
        setParentInChildEntityAndUpdate(savedLowProgramAsset, savedProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);


//        // check db LOG
//        System.out.println(" // Check db //");
//        System.out.println("All assets:" );
//        assetRepository.findAll().forEach( System.out::println);
//        System.out.println("All BaseProgramAssets l ,m,h,c:" );
//        lpaRepository.findAll().forEach(System.out::println);
//        mpaRepository.findAll().forEach(System.out::println);
//        hpaRepository.findAll().forEach(System.out::println);
//        cpaRepository.findAll().forEach(System.out::println);
//        System.out.println("All Program Assets:" );
//        programAssetRepository.findAll().forEach(System.out::println);

        return savedProgramAsset;
    }

    //update
    private void setParentInChildEntityAndUpdate(LowProgramAsset savedLowProgramAsset, ProgramAsset savedProgramAsset, MediumProgramAsset savedMediumProgramAsset, HighProgramAsset savedHighProgramAsset, CriticalProgramAsset savedCriticalProgramAsset) {
        // Retrieve the saved program asset from the database to create active hibernate session
        ProgramAsset programAssetFromDB = programAssetRepository.findById(savedProgramAsset.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + savedProgramAsset.getId()));

        programEntityHelper.setProgramAssetForChildren(programAssetFromDB, savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);

        // Update parent entity with child entities
        programAssetRepository.save(programAssetFromDB);
    }

    //Update
    private ProgramAsset createAndSaveProgramAsset(LowProgramAsset savedLowProgramAsset, MediumProgramAsset savedMediumProgramAsset, HighProgramAsset savedHighProgramAsset, CriticalProgramAsset savedCriticalProgramAsset) {
        ProgramAsset programAsset = programEntityHelper.createProgramAsset(
                savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);
        // Save parent entity
        return programAssetRepository.save(programAsset);
    }

    //update
    private CriticalProgramAsset getCriticalProgramAsset(ProgramPayload programPayload) {
        Set<Asset> assets = programEntityHelper.convertAssetPayloadsToAssets(programPayload.getAsset().getCriticalAsset().getAssets());
        CriticalProgramAsset criticalProgramAsset = new CriticalProgramAsset();
        return programEntityHelper.setAssetsToBaseProgramAsset(criticalProgramAsset, assets, programPayload.getAsset().getCriticalAsset().getPrice());
    }

    //update
    private HighProgramAsset getHighProgramAsset(ProgramPayload programPayload) {
        Set<Asset> assets = programEntityHelper.convertAssetPayloadsToAssets(programPayload.getAsset().getHighAsset().getAssets());
        HighProgramAsset highProgramAsset = new HighProgramAsset();
        return programEntityHelper.setAssetsToBaseProgramAsset(highProgramAsset, assets, programPayload.getAsset().getHighAsset().getPrice());
    }

    //update
    private MediumProgramAsset getMediumProgramAsset(ProgramPayload programPayload) {
        Set<Asset> assets = programEntityHelper.convertAssetPayloadsToAssets(programPayload.getAsset().getMediumAsset().getAssets());
        MediumProgramAsset mediumProgramAsset = new MediumProgramAsset();
        return programEntityHelper.setAssetsToBaseProgramAsset(mediumProgramAsset, assets, programPayload.getAsset().getMediumAsset().getPrice());
    }

    //update
    private LowProgramAsset getLowProgramAsset(ProgramPayload programPayload) {
        Set<Asset> assets = programEntityHelper.convertAssetPayloadsToAssets(programPayload.getAsset().getLowAsset().getAssets());
        LowProgramAsset lowProgramAsset = new LowProgramAsset();
        return programEntityHelper.setAssetsToBaseProgramAsset(lowProgramAsset, assets, programPayload.getAsset().getLowAsset().getPrice());
    }

    private <T extends BaseProgramAsset> T setAssetsToBaseProgramAsset(T baseProgramAsset, Set<Asset> assetSet, Double assetPrice) {
        // Set the price and assets to the base program asset
        programEntityHelper.setBaseProgramAssetProperties(baseProgramAsset, assetSet, assetPrice);
        // Save the base program asset and return the saved entity
        return programEntityHelper.saveBaseProgramAsset(baseProgramAsset);
    }

    // Related to UserService
    public List<Program> getAllBugBountyProgramsAsEntity() {
        return programsRepository.findAll();
    }

    public Program getBugBountyProgramById(Long id) {
        return programsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found with id:" + id));
    }
}
