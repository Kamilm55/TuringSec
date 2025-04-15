package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.payment.Card;
import com.turingSecApp.turingSec.payload.payment.CardRequest;
import com.turingSecApp.turingSec.response.payment.CardResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {
    Card toEntity(CardRequest cardRequest);
    CardResponse toDto(Card card);
}
