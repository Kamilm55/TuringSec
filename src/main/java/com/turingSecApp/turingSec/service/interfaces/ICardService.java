package com.turingSecApp.turingSec.service.interfaces;

import com.turingSecApp.turingSec.payload.payment.CardOwnerRequest;
import com.turingSecApp.turingSec.payload.payment.CardRequest;
import com.turingSecApp.turingSec.response.payment.CardResponse;

import java.util.List;

public interface ICardService {
    CardResponse addCardOwnerInformation(CardOwnerRequest cardOwnerInfo);

    CardResponse addCardInformation(CardRequest cardInfoRequest, Long id);

    List<CardResponse> getCards();

    Void deleteCard(Long id);

    CardResponse getCardById(Long id);
}
