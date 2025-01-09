package com.turingSecApp.turingSec.model.repository.payment;

import com.turingSecApp.turingSec.model.entities.payment.Card;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CardRepository extends JpaRepository<Card,Long> {

    List<Card> findByHacker(HackerEntity hacker);

    Optional<Card> findByIdAndHacker(Long cardId, HackerEntity hacker);
}
