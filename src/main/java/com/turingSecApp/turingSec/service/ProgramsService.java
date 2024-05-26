package com.turingSecApp.turingSec.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.turingSecApp.turingSec.dao.entities.Asset;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.*;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.payload.program.AssetPayload;
import com.turingSecApp.turingSec.response.program.AssetTypeDTO;
import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
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

    private final AssetTypeService assetTypeService;
    private final ProgramsRepository programsRepository;
    private final UtilService utilService;
    private final ProgramAssetRepository programAssetRepository;
    private final AssetRepository assetRepository;
    private final LPARepository lpaRepository;
    private final MPARepository mpaRepository;
    private final HPARepository hpaRepository;
    private final CPARepository cpaRepository;

    private final CompanyRepository companyRepository;

    @Override
    public List<Program> getCompanyAllBugBountyPrograms(){
        // Retrieve the company associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();

        // Get programs belonging to the company
        List<Program> programs = programsRepository.findByCompany(company);

        /*programs.stream().map(ProgramMapper.INSTANCE::toDto).collect(Collectors.toList())*/
        return programs;
    }


    @Override
    @Transactional
    public /*BugBountyProgramDTO*/Program createBugBountyProgram(ProgramPayload programPayload) {
        CompanyEntity company = utilService.getAuthenticatedCompany();

        //todo: update if program already exists , if exists delete and post new one with payload

        return convertToBugBountyProgramEntityAndSave(programPayload, company);
    }
    @Transactional // For commandlineRunner (mock data)
    public void createBugBountyProgramForTest(ProgramPayload programPayload , CompanyEntity company) throws JsonProcessingException {
//        CompanyEntity company = getAuthenticatedUser();

        Program program = convertToBugBountyProgramEntityAndSave(programPayload, company);

        System.out.println("createdOrUpdatedProgram " + program);
    }

    // todo: create programentityhelper service and refactorThis.
    public Program convertToBugBountyProgramEntityAndSave(ProgramPayload programPayload, CompanyEntity company) {
        List<Program> programList = programsRepository.findAll();

        if(!programList.isEmpty()){
            Program existingProgram = programList.get(0);

            // Retrieve the saved program asset from the database to create active hibernate session
            Program programFromDB = programsRepository.findById(existingProgram.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + existingProgram.getId()));;

            company.removeProgram(programFromDB.getId());// without this not work , we remove program from set in company entity then we can delete
            programsRepository.delete(programFromDB);

        }

        Program program = new Program();
        program.setFromDate(programPayload.getFromDate());
        program.setToDate(programPayload.getToDate());
        program.setNotes(programPayload.getNotes());
        program.setPolicy(programPayload.getPolicy());
        program.setCompany(company);
        program.setInScope(programPayload.getInScope());
        program.setOutOfScope(programPayload.getOutOfScope());
        program.setProhibits(convertToStrictEntities(programPayload.getProhibits(), program));

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
//

    private List<StrictEntity> convertToStrictEntities(List<StrictPayload> prohibitsDTOs, Program program) {
        return prohibitsDTOs.stream()
                .map(prohibitDTO -> {
                    StrictEntity strictEntity = new StrictEntity();
                    strictEntity.setProhibitAdded(prohibitDTO.getProhibitAdded());
                    strictEntity.setBugBountyProgramForStrict(program);
                    return strictEntity;
                })
                .collect(Collectors.toList());
    }

//    private Program createOrUpdateBugBountyProgram(Program program) {
//        // Check if a program with the same parameters already exists for the company
//        List<Program> programs = programsRepository.findByCompany(program.getCompany());
//        if (!programs.isEmpty()) {
//            Program existingProgram = programs.get(0);
//            // Update existing program with the new data
//            updateProgramFields(existingProgram, program);
//            return programsRepository.save(existingProgram);
//        } else {
//            // Create new program
//
//
//
//            return programsRepository.save(program);
//        }

//    }
    private void updateProgramFields(Program existingProgram, Program newProgram) {
        updateProgramDates(existingProgram, newProgram);
        existingProgram.setNotes(newProgram.getNotes());
        existingProgram.setPolicy(newProgram.getPolicy());
        updateProgramScope(existingProgram, newProgram);
        updateProgramProhibits(existingProgram, newProgram);

        updateProgramAssetTypes(existingProgram, newProgram);
    }

    private void updateProgramDates(Program existingProgram, Program newProgram) {
        existingProgram.setFromDate(newProgram.getFromDate());
        existingProgram.setToDate(newProgram.getToDate());
    }

    private void updateProgramScope(Program existingProgram, Program newProgram) {
        existingProgram.setInScope(newProgram.getInScope());
        existingProgram.setOutOfScope(newProgram.getOutOfScope());
    }

    private void updateProgramAssetTypes(Program existingProgram, Program newProgram) {
        existingProgram.setAsset(newProgram.getAsset());
    }

    private void updateProgramProhibits(Program existingProgram, Program newProgram) {
        List<StrictEntity> updatedProhibits = new ArrayList<>();
        for (StrictEntity strictEntity : newProgram.getProhibits()) {
            StrictEntity existingProhibit = findExistingProhibits(existingProgram.getProhibits(), strictEntity);
            if (existingProhibit != null) {
                // Update existing prohibit
                existingProhibit.setProhibitAdded(strictEntity.getProhibitAdded());
                updatedProhibits.add(existingProhibit);
            } else {
                // Add new prohibit
                strictEntity.setBugBountyProgramForStrict(existingProgram);
                updatedProhibits.add(strictEntity);
            }
        }
        existingProgram.getProhibits().clear();
        existingProgram.getProhibits().addAll(updatedProhibits);
    }


    // fixme: fix this method
    @Override
    public Set<Asset> getCompanyProgramAssets() {
       // Retrieve the company and its unique program associated with the authenticated user
        CompanyEntity company = utilService.getAuthenticatedCompany();
        Program companyProgram = programsRepository.findByCompany(company).get(0);// Always contains one element

        // Get assets belonging to the company
        Set<ProgramAsset> programAssets = programAssetRepository.findProgramAssetByProgram(companyProgram);

        Set<Asset> assets = new HashSet<>();
        for (ProgramAsset programAsset : programAssets) {
            addAssetsFromAllBaseProgramAssets(assets,programAsset);
        }


        return assets;
    }

    private void addAssetsFromAllBaseProgramAssets(Set<Asset> assets, ProgramAsset programAsset) {
        // Iterate all assets , todo: iterate with fields (dry)
        BaseProgramAsset lowProgramAsset = programAsset.getLowAsset();
        addAssetsToAssetSetFromBaseProgramAsset(assets,lowProgramAsset);

        BaseProgramAsset mediumProgramAsset = programAsset.getMediumAsset();
        addAssetsToAssetSetFromBaseProgramAsset(assets,mediumProgramAsset);

        BaseProgramAsset highProgramAsset = programAsset.getHighAsset();
        addAssetsToAssetSetFromBaseProgramAsset(assets,highProgramAsset);

        BaseProgramAsset criticalProgramAsset = programAsset.getCriticalAsset();
        addAssetsToAssetSetFromBaseProgramAsset(assets,criticalProgramAsset);
    }

    private void addAssetsToAssetSetFromBaseProgramAsset(Set<Asset> assets,BaseProgramAsset baseProgramAsset) {
        if (baseProgramAsset!=null) {
            assets.addAll(baseProgramAsset.getAssets());
        }
    }

    //
    private AssetTypeDTO mapToDTO(AssetTypeEntity assetTypeEntity) {
        AssetTypeDTO dto = new AssetTypeDTO();
//        dto.setId(assetTypeEntity.getId());
        dto.setLevel(assetTypeEntity.getLevel());
        dto.setAssetType(assetTypeEntity.getAssetType());
        dto.setPrice(assetTypeEntity.getPrice());
        return dto;
    }

    @Override
    @Transactional
    public void deleteBugBountyProgram(Long id){
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

    private void setParentInChildEntityAndUpdate(LowProgramAsset savedLowProgramAsset, ProgramAsset savedProgramAsset, MediumProgramAsset savedMediumProgramAsset, HighProgramAsset savedHighProgramAsset, CriticalProgramAsset savedCriticalProgramAsset) {
        // Retrieve the saved program asset from the database to create active hibernate session
        ProgramAsset programAssetFromDB = programAssetRepository.findById(savedProgramAsset.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + savedProgramAsset.getId()));


        savedLowProgramAsset.setProgramAsset(programAssetFromDB);
        savedMediumProgramAsset.setProgramAsset(programAssetFromDB);
        savedHighProgramAsset.setProgramAsset(programAssetFromDB);
        savedCriticalProgramAsset.setProgramAsset(programAssetFromDB);

        // Update parent entity with child entities
        programAssetRepository.save(programAssetFromDB);
    }

    private ProgramAsset createAndSaveProgramAsset(LowProgramAsset savedLowProgramAsset, MediumProgramAsset savedMediumProgramAsset, HighProgramAsset savedHighProgramAsset, CriticalProgramAsset savedCriticalProgramAsset) {
        ProgramAsset programAsset = new ProgramAsset();
        programAsset.setLowAsset(savedLowProgramAsset);
        programAsset.setMediumAsset(savedMediumProgramAsset);
        programAsset.setHighAsset(savedHighProgramAsset);
        programAsset.setCriticalAsset(savedCriticalProgramAsset);

        // Save parent entity
        return programAssetRepository.save(programAsset);
    }

    private CriticalProgramAsset getCriticalProgramAsset(ProgramPayload programPayload) {
        // Convert AssetPayload into Asset for CriticalProgramAsset
        Set<Asset> assetSetForCritical = new HashSet<>();
        for (AssetPayload assetPayload : programPayload.getAsset().getCriticalAsset().getAssets()) {
            Asset asset = new Asset();
            asset.setType(assetPayload.getType());
            asset.setNames(new HashSet<>(assetPayload.getNames()));
            assetSetForCritical.add(asset);
        }

// Create and save criticalProgramAsset
        CriticalProgramAsset criticalProgramAsset = new CriticalProgramAsset();
        CriticalProgramAsset savedCriticalProgramAsset = setAssetsToBaseProgramAsset(criticalProgramAsset, assetSetForCritical, programPayload.getAsset().getCriticalAsset().getPrice());
        return savedCriticalProgramAsset;
    }

    private HighProgramAsset getHighProgramAsset(ProgramPayload programPayload) {
        // Convert AssetPayload into Asset for HighProgramAsset
        Set<Asset> assetSetForHigh = new HashSet<>();
        for (AssetPayload assetPayload : programPayload.getAsset().getHighAsset().getAssets()) {
            Asset asset = new Asset();
            asset.setType(assetPayload.getType());
            asset.setNames(new HashSet<>(assetPayload.getNames()));
            assetSetForHigh.add(asset);
        }

// Create and save highProgramAsset
        HighProgramAsset highProgramAsset = new HighProgramAsset();
        HighProgramAsset savedHighProgramAsset = setAssetsToBaseProgramAsset(highProgramAsset, assetSetForHigh, programPayload.getAsset().getHighAsset().getPrice());
        return savedHighProgramAsset;
    }

    private MediumProgramAsset getMediumProgramAsset(ProgramPayload programPayload) {
        // Convert AssetPayload into Asset for MediumProgramAsset
        Set<Asset> assetSetForMedium = new HashSet<>();
        for (AssetPayload assetPayload : programPayload.getAsset().getMediumAsset().getAssets()) {
            Asset asset = new Asset();
            asset.setType(assetPayload.getType());
            asset.setNames(new HashSet<>(assetPayload.getNames()));
            assetSetForMedium.add(asset);
        }

// Create and save mediumProgramAsset
        MediumProgramAsset mediumProgramAsset = new MediumProgramAsset();
        MediumProgramAsset savedMediumProgramAsset = setAssetsToBaseProgramAsset(mediumProgramAsset, assetSetForMedium, programPayload.getAsset().getMediumAsset().getPrice());
        return savedMediumProgramAsset;
    }

    private LowProgramAsset getLowProgramAsset(ProgramPayload programPayload) {
        // Convert AssetPayload into Asset
        Set<Asset> assetSetForLow = new HashSet<>();
        for (AssetPayload assetPayload : programPayload.getAsset().getLowAsset().getAssets()) {
            Asset asset = new Asset();
            asset.setType(assetPayload.getType());
            asset.setNames(new HashSet<>(assetPayload.getNames()));
            assetSetForLow.add(asset);
        }

// Create and save lowProgramAsset
        LowProgramAsset lowProgramAsset = new LowProgramAsset();
        LowProgramAsset savedLowProgramAsset = setAssetsToBaseProgramAsset(lowProgramAsset, assetSetForLow, programPayload.getAsset().getLowAsset().getPrice());
        return savedLowProgramAsset;
    }

    private <T extends BaseProgramAsset> T setAssetsToBaseProgramAsset(T baseProgramAsset, Set<Asset> assetSet, Double assetPrice) {
        baseProgramAsset.setPrice(assetPrice);

        // Set all child entity of baseProgramAsset in parent
        baseProgramAsset.setAssets(assetSet);

        // All assets must be set to Parent entity in child
        assetSet.forEach(asset2 -> {asset2.setBaseProgramAsset(baseProgramAsset);});

        if(baseProgramAsset instanceof LowProgramAsset lowProgramAsset){
            return (T) lpaRepository.save(lowProgramAsset);
        } else if (baseProgramAsset instanceof MediumProgramAsset mediumProgramAsset) {
            return (T) mpaRepository.save(mediumProgramAsset);
        }
        else if (baseProgramAsset instanceof HighProgramAsset highProgramAsset) {
            return (T) hpaRepository.save(highProgramAsset);
        }else if (baseProgramAsset instanceof CriticalProgramAsset criticalProgramAsset) {
            return (T) cpaRepository.save(criticalProgramAsset);
        }

        return null;// fixme
    }


    // Utils
//    private ProgramAsset findExistingAssetType(Set<ProgramAsset> existingAssetTypes, ProgramAsset assetType) {
//        for (ProgramAsset existing : existingAssetTypes) {
//            if (existing.getId().equals(assetType.getId())) {
//                return existing;
//            }
//        }
//        return null;
//    }

    private StrictEntity findExistingProhibits(List<StrictEntity> existingProhibits, StrictEntity strictEntity) {
        for (StrictEntity existing : existingProhibits) {
            if (existing.getId().equals(strictEntity.getId())) {
                return existing;
            }
        }
        return null;
    }


    // Related to UserService
    public List<Program> getAllBugBountyProgramsAsEntity() {
        return programsRepository.findAll();
    }


    public Program getBugBountyProgramById(Long id) {
        return programsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found"));
    }
    //

}
