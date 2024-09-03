package com.turingSecApp.turingSec.service.program;

import com.turingSecApp.turingSec.exception.custom.PermissionDeniedException;
import com.turingSecApp.turingSec.helper.entityHelper.program.IProgramEntityHelper;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.program.ProgramRepository;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.UtilService;
import com.turingSecApp.turingSec.util.mapper.ProgramMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgramManagementService  {
    private final UserFactory userFactory;
    private final ProgramRepository programRepository;
    private final UtilService utilService;
    private final IProgramEntityHelper programEntityHelper;

    @Transactional
    public ProgramDTO createBugBountyProgram(ProgramPayload programPayload) {
        CompanyEntity company = (CompanyEntity) userFactory.getAuthenticatedBaseUser();

        return convertToProgramEntityAndSave(programPayload, company);
    }

    @Transactional // For commandlineRunner (mock data)
    public void createBugBountyProgramForTest(ProgramPayload programPayload, CompanyEntity company){
//      CompanyEntity company = utilService.getAuthenticatedCompany();

        ProgramDTO program = convertToProgramEntityAndSave(programPayload, company);
    }
    private ProgramDTO convertToProgramEntityAndSave(ProgramPayload programPayload, CompanyEntity company) {
        // Remove current program if exists
        programEntityHelper.removeExistingProgram(company);

        // Create a program
        Program program = createAndPopulateProgram(programPayload, company);

        // Save program
        programRepository.save(program);

        // Map to dto
        return ProgramMapper.INSTANCE.toProgramDTO(program);
    }
    private Program createAndPopulateProgram(ProgramPayload programPayload, CompanyEntity company) {
        // Create program entity (set basic fields) with the associated 'company' entity
        Program program = programEntityHelper.createProgramEntity(programPayload, company);

        // Set 'prohibits'
        programEntityHelper.setProhibits(programPayload,program);

        // Set 'asset'
        programEntityHelper.setProgramAsset(programPayload, program);

        return program;
    }

    @Transactional
    public void deleteBugBountyProgram(Long id) {
        // Get the company associated with the authenticated user
        CompanyEntity company = (CompanyEntity) userFactory.getAuthenticatedBaseUser();

        // Retrieve the bug bounty program by ID
        Program program = utilService.findProgramById(id);

        // Check if the authenticated company is the owner of the program
        validateCompanyForDeleteProgram(company,program);
    }

    private void validateCompanyForDeleteProgram(CompanyEntity company, Program program) {
        if (program.getCompany().getId().equals(company.getId())) {
            company.removeProgram(program.getId());// todo: without this not work , we remove program from set in company entity then we can delete
            programRepository.delete(program);
        } else {
            // If the authenticated company is not the owner, return forbidden status
            throw new PermissionDeniedException();
        }
    }
}
