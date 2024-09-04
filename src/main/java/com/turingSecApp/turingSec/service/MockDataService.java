package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.helper.entityHelper.MockDataHelper;
import com.turingSecApp.turingSec.model.entities.MockData;
import com.turingSecApp.turingSec.model.repository.*;
import com.turingSecApp.turingSec.service.interfaces.IMockDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MockDataService implements IMockDataService {
    private final MockDataRepository mockDataRepository;
    private final MockDataHelper mockDataHelper;


    @Override
    public void insertMockData() {
//        System.out.println(mockDataRepository.findById(1L));// test log -> does exist?
        MockData mockData = mockDataRepository.findById(1L).orElse(new MockData(1L,0));
        int insertedMockNumber = mockData.getInsertedMockNumber();

        if(insertedMockNumber == 0){
            log.info("Insert Mock data for once!");

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
