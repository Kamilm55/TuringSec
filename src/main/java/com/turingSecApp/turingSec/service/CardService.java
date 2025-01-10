package com.turingSecApp.turingSec.service;

import com.turingSecApp.turingSec.exception.custom.ResourceNotFoundException;
import com.turingSecApp.turingSec.model.entities.payment.Card;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.model.repository.payment.CardRepository;
import com.turingSecApp.turingSec.model.repository.HackerRepository;
import com.turingSecApp.turingSec.payload.payment.CardOwnerRequest;
import com.turingSecApp.turingSec.payload.payment.CardRequest;
import com.turingSecApp.turingSec.response.payment.CardResponse;
import com.turingSecApp.turingSec.service.interfaces.ICardService;
import com.turingSecApp.turingSec.service.user.factory.UserFactory;
import com.turingSecApp.turingSec.util.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService implements ICardService {
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final HackerRepository hackerRepository;
    private final UserFactory userFactory;


    @Override
    public List<CardResponse> getCards() {
        HackerEntity hacker = userFactory.getAuthenticatedHacker();
        log.info("Operation of getting all cards started by user");
        List<Card> cards = cardRepository.findByHacker(hacker);
        List<CardResponse> cardResponses = cards.stream().map(cardMapper::toDto).toList();
        log.info("Cards successfully returned");
        return cardResponses;
    }

    @Override
    public CardResponse getCardById(Long id) {
        HackerEntity hacker = userFactory.getAuthenticatedHacker();
        log.info("Operation of getting a card with ID {} started by user", id);
        Card card = cardRepository.findByIdAndHacker(id, hacker).orElseThrow(() -> {
            log.warn("Failed to get card: Card with ID {} not found for user", id);
            return new ResourceNotFoundException("CARD_NOT_FOUND");
        });
        CardResponse cardResponse = cardMapper.toDto(card);
        log.info("Card successfully returned to user");
        return cardResponse;
    }

    @Override
    public CardResponse addCardOwnerInformation(CardOwnerRequest cardOwnerInfo) {
        HackerEntity hacker = userFactory.getAuthenticatedHacker();
        log.info("Operation of adding payment method started: User try to add card owner information");
        Card card = Card.builder()
                .firstName(cardOwnerInfo.getFirstName())
                .lastName(cardOwnerInfo.getLastName())
                .city(cardOwnerInfo.getCity())
                .dateOfBirth(cardOwnerInfo.getDateOfBirth())
                .hacker(hacker)
                .build();
        cardRepository.save(card);
        log.info("Card owner info added");
        return cardMapper.toDto(card);
    }

    @Override
    public CardResponse addCardInformation(CardRequest cardInfoRequest, Long cardId) {
        HackerEntity hacker = userFactory.getAuthenticatedHacker();
        log.info("Operation of adding payment method started: User try to add card information");
        Card card = cardRepository.findByIdAndHacker(cardId, hacker).orElseThrow(() -> {
            log.warn("Failed to add card information: Card not found");
            return new ResourceNotFoundException("NOT_FOUND");
        });
        cardMapper.mapForUpdate(card, cardInfoRequest);
        cardRepository.save(card);
        log.info("Card information successfully added");
        return cardMapper.toDto(card);
    }

    @Override
    public Void deleteCard(Long id) {
        HackerEntity hacker = userFactory.getAuthenticatedHacker();
        log.info("Operation of deleting payment method started: User try to delete card with ID {}", id);
        Card card = cardRepository.findByIdAndHacker(id, hacker).orElseThrow(() -> {
            log.warn("Failed to delete card:Card with ID {} not found for user", id);
            return new ResourceNotFoundException("CARD_NOT_FOUND");
        });
        cardRepository.delete(card);
        log.info("Card with ID {} successfully deleted", id);
        return null;
    }

}
