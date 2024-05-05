package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.response.HackerDTO;

public interface IHackerService {
    HackerDTO findById(Long hackerId);
}
