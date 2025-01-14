package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.payload.payment.CardOwnerRequest;
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
@RequestMapping("/cards")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CardController {
    private final ICardService paymentService;

    @GetMapping
    public BaseResponse<List<CardResponse>> getCards(){
        return BaseResponse.success(paymentService.getCards());
    }

    @GetMapping("/{id}")
    public BaseResponse<CardResponse> getCard(@PathVariable Long id){
        return BaseResponse.success(paymentService.getCardById(id));
    }

    @PostMapping("/user-info")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse<CardResponse> addCardOwnerInfo(@Valid @RequestBody CardOwnerRequest cardOwnerRequest){
        return BaseResponse.success(paymentService.addCardOwnerInformation(cardOwnerRequest));
    }

    @PostMapping("/{id}/card-info")
    public BaseResponse<CardResponse> addCardInfo(@PathVariable Long id,@RequestBody CardRequest cardInfoRequest) {
        return BaseResponse.success(paymentService.addCardInformation(cardInfoRequest, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BaseResponse<Void> deleteCard(@PathVariable Long id){
        return BaseResponse.success(paymentService.deleteCard(id));
    }

}
