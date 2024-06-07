package com.turingSecApp.turingSec.util;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Data
//@Component
public class MockData {
    public static Set<String> mockDataNames = new HashSet<>();
    public static void addToMockDataNameToSet(String mockDataName){
        mockDataNames.add(mockDataName);
    }
}
