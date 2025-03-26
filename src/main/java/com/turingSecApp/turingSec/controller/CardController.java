package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.payment.CardRequest;
import com.turingSecApp.turingSec.response.base.BaseResponse;
import com.turingSecApp.turingSec.response.payment.CardResponse;
import com.turingSecApp.turingSec.service.interfaces.ICardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CardController {
    private final ICardService paymentService;

    @GetMapping
    public BaseResponse<List<CardResponse>> getCards() {
        return BaseResponse.success(paymentService.getCards());
    }

    @GetMapping("/{id}")
    public BaseResponse<CardResponse> getCard(@PathVariable Long id) {
        return BaseResponse.success(paymentService.getCardById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<CardResponse> addCard(@Valid @RequestBody CardRequest cardRequest) {
        return BaseResponse.success(paymentService.addCard(cardRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BaseResponse<Void> deleteCard(@PathVariable Long id) {
        return BaseResponse.success(paymentService.deleteCard(id));
    }

}
