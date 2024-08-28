package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.exception.custom.CompanyNotFoundException;
import com.turingSecApp.turingSec.helper.entityHelper.MockDataHelper;
import com.turingSecApp.turingSec.model.entities.MockData;
import com.turingSecApp.turingSec.model.entities.report.embedded.ProofOfConcept;
import com.turingSecApp.turingSec.model.entities.report.embedded.ReportWeakness;
import com.turingSecApp.turingSec.model.entities.role.Role;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.model.repository.report.ReportRepository;
import com.turingSecApp.turingSec.payload.program.ProgramPayload;
import com.turingSecApp.turingSec.payload.program.ProhibitPayload;
import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;
import com.turingSecApp.turingSec.payload.program.asset.BaseProgramAssetPayload;
import com.turingSecApp.turingSec.payload.program.asset.ProgramAssetPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.payload.report.child.ReportAssetPayload;
import com.turingSecApp.turingSec.payload.user.RegisterPayload;
import com.turingSecApp.turingSec.service.interfaces.IReportService;
import com.turingSecApp.turingSec.service.interfaces.IMockDataService;
import com.turingSecApp.turingSec.service.interfaces.IUserService;
import com.turingSecApp.turingSec.service.program.ProgramService;
import com.turingSecApp.turingSec.util.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockDataService implements IMockDataService {
    private final MockDataRepository mockDataRepository;
    private final MockDataHelper mockDataHelper;


    @Override
    public void insertMockData() {
        System.out.println(mockDataRepository.findById(1L));
        MockData mockData = mockDataRepository.findById(1L).orElse(new MockData(1L,0));
        int insertedMockNumber = mockData.getInsertedMockNumber();

        if(insertedMockNumber == 0){
            System.out.println("Insert Mock data for once!");

            // Insert All data if all are insertable
            mockDataHelper.insertAllTransactionally(mockData);


        } else if (insertedMockNumber == 1) {
            String warnMsg = "Mock data has been inserted successfully!";
            log.info(warnMsg);
            System.out.println(warnMsg);
        }
        else {
            String errorMsg = "Mock data insertedMockNumber is neither 0 nor 1!";
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

    }



}
