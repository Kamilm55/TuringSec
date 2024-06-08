package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.model.entities.program.asset.child.*;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.CompanyRepository;
import com.turingSecApp.turingSec.model.repository.program.ProgramsRepository;
import com.turingSecApp.turingSec.model.repository.program.asset.AssetRepository;
import com.turingSecApp.turingSec.model.repository.program.asset.ProgramAssetRepository;
import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.helper.entityHelper.program.IProgramEntityHelper;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.service.interfaces.IProgramsService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProgramService implements IProgramsService {

    private final ProgramsRepository programsRepository;
    private final UtilService utilService;
    private final ProgramAssetRepository programAssetRepository;
    private final AssetRepository assetRepository;
    private final IProgramEntityHelper programEntityHelper;

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

        return convertToBugBountyProgramEntityAndSave(programPayload, company);
    }

    @Transactional // For commandlineRunner (mock data)
    public void createBugBountyProgramForTest(ProgramPayload programPayload, CompanyEntity company){
//        CompanyEntity company = getAuthenticatedUser();

        Program program = convertToBugBountyProgramEntityAndSave(programPayload, company);

        System.out.println("createdOrUpdatedProgram " + program);
    }

    public Program convertToBugBountyProgramEntityAndSave(ProgramPayload programPayload, CompanyEntity company) {
        // Remove existing program if exists
        programEntityHelper.removeExistingProgram(company);

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

    private void addAssetsFromAllBaseProgramAssets(Set<Asset> assets, ProgramAsset programAsset) {
        // Iterate all assets
        // Add assets from all base program assets to the assets set
        programEntityHelper.addAssetsToSet(assets, programAsset.getLowAsset());
        programEntityHelper.addAssetsToSet(assets, programAsset.getMediumAsset());
        programEntityHelper.addAssetsToSet(assets, programAsset.getHighAsset());
        programEntityHelper.addAssetsToSet(assets, programAsset.getCriticalAsset());
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
            company.removeProgram(program.getId());// fixme: without this not work , we remove program from set in company entity then we can delete
            programsRepository.delete(program);
        } else {
            // If the authenticated company is not the owner, return forbidden status
            throw new PermissionDeniedException();
        }
    }

    private ProgramAsset saveProgramAssets(ProgramPayload programPayload) {
        // Extract fields from payload
        LowProgramAsset savedLowProgramAsset = programEntityHelper.getLowProgramAsset(programPayload);

        MediumProgramAsset savedMediumProgramAsset = programEntityHelper.getMediumProgramAsset(programPayload);

        HighProgramAsset savedHighProgramAsset = programEntityHelper.getHighProgramAsset(programPayload);

        CriticalProgramAsset savedCriticalProgramAsset = programEntityHelper.getCriticalProgramAsset(programPayload);

        // Create ProgramAsset and set saved child entities in the parent entity
        ProgramAsset savedProgramAsset = createAndSaveProgramAsset(savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);

        // Set parent entity in child entities
        setParentInChildEntityAndUpdate(savedLowProgramAsset, savedProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);

        return savedProgramAsset;
    }

    private void setParentInChildEntityAndUpdate(LowProgramAsset savedLowProgramAsset, ProgramAsset savedProgramAsset, MediumProgramAsset savedMediumProgramAsset, HighProgramAsset savedHighProgramAsset, CriticalProgramAsset savedCriticalProgramAsset) {
        // Retrieve the saved program asset from the database to create active hibernate session
        ProgramAsset programAssetFromDB = programAssetRepository.findById(savedProgramAsset.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Program Asset not found with id:" + savedProgramAsset.getId()));

        programEntityHelper.setProgramAssetForChildren(programAssetFromDB, savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);

        // Update parent entity with child entities
        programAssetRepository.save(programAssetFromDB);
    }

    private ProgramAsset createAndSaveProgramAsset(LowProgramAsset savedLowProgramAsset, MediumProgramAsset savedMediumProgramAsset, HighProgramAsset savedHighProgramAsset, CriticalProgramAsset savedCriticalProgramAsset) {
        ProgramAsset programAsset = programEntityHelper.createProgramAsset(
                savedLowProgramAsset, savedMediumProgramAsset, savedHighProgramAsset, savedCriticalProgramAsset);
        // Save parent entity
        return programAssetRepository.save(programAsset);
    }


    // Related to UserService
    public List<Program> getAllBugBountyProgramsAsEntity() {
        return programsRepository.findAll();
    }

    public Program getBugBountyProgramById(Long id) {
        return programsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bug Bounty Program not found with id:" + id));
    }
}
