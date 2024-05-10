package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.response.user.HackerDTO;

public interface IHackerService {
    HackerDTO findById(Long hackerId);
}
